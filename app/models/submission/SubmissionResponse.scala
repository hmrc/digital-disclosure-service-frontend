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

package models.submission

import play.api.libs.json.{Json, OFormat}

sealed trait SubmissionResponse extends Product with Serializable

object SubmissionResponse {

  final case class Success(id: String) extends SubmissionResponse

  object Success {
    implicit lazy val format: OFormat[Success] = Json.format[Success]
  }

  final case class Failure(errors: Seq[String]) extends SubmissionResponse

  object Failure {
    implicit lazy val format: OFormat[Failure] = Json.format[Failure]
  }
}
