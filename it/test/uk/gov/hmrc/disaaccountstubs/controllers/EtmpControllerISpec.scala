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

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.disaaccountstubs.models.registrationDetails.RegistrationDetails
import uk.gov.hmrc.http.HttpReads.Implicits.{readFromJson, readRaw}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import scala.concurrent.ExecutionContext.Implicits.global

class EtmpControllerISpec
  extends AnyWordSpec
     with Matchers
     with ScalaFutures
     with IntegrationPatience
     with GuiceOneServerPerSuite:

  private val httpClient = app.injector.instanceOf[HttpClientV2]
  private val baseUrl    = s"http://localhost:$port"

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .build()

  "GET /etmp/registration/:zref" should:
    "respond with 200 and the requested zref in the journey data" in:
      val zref = "Z1234"

      val response =
        httpClient
          .get(url"$baseUrl/etmp/registration/$zref")(HeaderCarrier())
          .execute[RegistrationDetails]
          .futureValue

      response.organisationDetails.flatMap(_.zRefNumber) shouldBe Some(zref)

    "respond with 404 when the zref is the not-found stub value" in:
      val response =
        httpClient
          .get(url"$baseUrl/etmp/registration/Z0404")(HeaderCarrier())
          .execute[HttpResponse]
          .futureValue

      response.status shouldBe 404
