/*
 * Copyright 2023 HM Revenue & Customs
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

package models.store

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsObject, JsSuccess, Json}
import models.store.notification._
import java.time.Instant

class SubmissionSpec extends AnyWordSpec with Matchers {

  "reads" should {
    "convert json to Notification where created is missing" in {
      val actual = Json
        .toJson(Notification("123", "456", Instant.now(), Metadata(), PersonalDetails(Background(), AboutYou())))
        .as[JsObject] - "created"
      actual.validate[Notification] shouldBe a[JsSuccess[_]]
    }

    "convert json to Notification where declaration is missing" in {
      val actual = Json
        .toJson(Notification("123", "456", Instant.now(), Metadata(), PersonalDetails(Background(), AboutYou())))
        .as[JsObject] - "madeDeclaration"
      actual.validate[Notification] shouldBe a[JsSuccess[_]]
    }
  }

}
