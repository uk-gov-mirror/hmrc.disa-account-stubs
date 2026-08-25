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

package models.emailverification

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsError, JsObject, Json}
import uk.gov.hmrc.disaaccountstubs.models.emailverification.VerifyCodeResult

class VerifyCodeResultSpec extends AnyWordSpec with Matchers {

  private val model = VerifyCodeResult(
    status = "CODE_VERIFIED",
    message = Some("The verification code for the email verified successfully")
  )

  private val json: JsObject = Json.obj(
    "status"  -> "CODE_VERIFIED",
    "message" -> "The verification code for the email verified successfully"
  )

  "VerifyCodeResult" should {

    "serialise to JSON" in {
      Json.toJson(model) mustBe json
    }

    "deserialise from JSON" in {
      json.as[VerifyCodeResult] mustBe model
    }

    "deserialise from JSON with only the required status field to the default values" in {
      Json.obj("status" -> "CODE_VERIFIED").as[VerifyCodeResult] mustBe VerifyCodeResult("CODE_VERIFIED", None)
    }

    "fail to deserialise when the required status field is missing" in {
      Json.obj().validate[VerifyCodeResult] mustBe a[JsError]
    }
  }
}
