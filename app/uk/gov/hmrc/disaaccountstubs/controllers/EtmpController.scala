/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.disaaccountstubs.controllers

import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.disaaccountstubs.controllers.EtmpController.NotFoundZref
import uk.gov.hmrc.disaaccountstubs.models.YesNoAnswer
import uk.gov.hmrc.disaaccountstubs.models.registrationDetails.certificatesofauthority.{CertificatesOfAuthority, CertificatesOfAuthorityYesNo, FcaArticles, FinancialOrganisation}
import uk.gov.hmrc.disaaccountstubs.models.registrationDetails.isaProducts.{InnovativeFinancialProduct, IsaProduct, IsaProducts}
import uk.gov.hmrc.disaaccountstubs.models.registrationDetails.liaisonofficers.{LiaisonOfficer, LiaisonOfficerCommunication, LiaisonOfficers}
import uk.gov.hmrc.disaaccountstubs.models.registrationDetails.orgdetails.OrganisationDetails
import uk.gov.hmrc.disaaccountstubs.models.registrationDetails.signatories.{Signatories, Signatory}
import uk.gov.hmrc.disaaccountstubs.models.registrationDetails.thirdparty.{ThirdParty, ThirdPartyOrganisations}
import uk.gov.hmrc.disaaccountstubs.models.registrationDetails.{BusinessVerification, CorrespondenceAddress, OrganisationEmail, RegistrationDetails}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.Instant
import java.util.UUID
import javax.inject.{Inject, Singleton}

@Singleton
class EtmpController @Inject() (
  cc: ControllerComponents
) extends BackendController(cc)
    with Logging {

  def retrieveRegistrationDetails(zref: String): Action[AnyContent] = Action { implicit request =>
    zref match {
      case NotFoundZref =>
        logger.info(
          s"[EtmpController][retrieveRegistrationDetails] Returning registration details not found response for zref: [$zref]"
        )
        NotFound

      case _ =>
        logger.info(
          s"[EtmpController][retrieveRegistrationDetails] Returning registration details success response for zref: [$zref]"
        )
        Ok(Json.toJson(registrationDetails(zref)))
    }
  }

  private def registrationDetails(zref: String): RegistrationDetails =
    RegistrationDetails(
      groupId = UUID.randomUUID().toString,
      businessVerification = Some(
        BusinessVerification(
          ctUtr = Some("1234567890"),
          companyName = Some("Test Isa Manager Ltd"),
          companyNumber = Some("12345678"),
          businessPartnerId = Some("XA0001234567890")
        )
      ),
      organisationDetails = Some(
        OrganisationDetails(
          zRefNumber = Some(zref),
          tradingName = Some("Test Trading Name"),
          fcaNumber = Some("123456"),
          correspondenceAddress = Some(
            CorrespondenceAddress(
              addressLine1 = Some("1 Test Street"),
              addressLine2 = Some("Test Town"),
              addressLine3 = Some("Test County"),
              postCode = Some("AA1 1AA")
            )
          ),
          orgTelephoneNumber = Some("01234567890")
        )
      ),
      organisationEmail = Some(OrganisationEmail(organisationEmail = Some("test@example.com"), verified = Some(true))),
      isaProducts = Some(
        IsaProducts(
          isaProducts = Some(Seq(IsaProduct.CashIsas, IsaProduct.StocksAndSharesIsas)),
          innovativeFinancialProducts = Some(Seq(InnovativeFinancialProduct.CrowdFundedDebentures)),
          p2pPlatform = None,
          p2pPlatformNumber = None
        )
      ),
      certificatesOfAuthority = Some(
        CertificatesOfAuthority(
          certificatesYesNo = Some(CertificatesOfAuthorityYesNo.Yes),
          fcaArticles = Some(Seq(FcaArticles.Article36H)),
          financialOrganisation = Some(Seq(FinancialOrganisation.Bank))
        )
      ),
      liaisonOfficers = Some(
        LiaisonOfficers(
          liaisonOfficers = Seq(
            LiaisonOfficer(
              id = UUID.randomUUID().toString,
              fullName = Some("Test Officer"),
              phoneNumber = Some("01234567890"),
              communication = Set(LiaisonOfficerCommunication.ByEmail),
              email = Some("officer@example.com")
            )
          )
        )
      ),
      signatories = Some(
        Signatories(
          signatories = Seq(
            Signatory(id = "signatory-test-id-1", fullName = Some("Test Signatory"), jobTitle = Some("Director"))
          )
        )
      ),
      thirdPartyOrganisations = Some(
        ThirdPartyOrganisations(
          managedByThirdParty = Some(YesNoAnswer.No),
          thirdParties = Seq.empty[ThirdParty],
          connectedOrganisations = Seq.empty[String]
        )
      ),
      lastUpdated = Some(Instant.now())
    )
}

object EtmpController {
  private val NotFoundZref = "Z0404"
}
