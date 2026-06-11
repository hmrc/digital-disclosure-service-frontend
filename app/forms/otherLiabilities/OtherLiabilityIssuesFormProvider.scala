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

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import models.OtherLiabilityIssues

class OtherLiabilityIssuesFormProvider @Inject() extends Mappings {

  def apply(): Form[Set[OtherLiabilityIssues]] =
      Form(
        "value" -> list(enumerable[OtherLiabilityIssues]("otherLiabilityIssues.error.required"))
          .transform(
            (list: List[OtherLiabilityIssues]) => {
              val set: Set[OtherLiabilityIssues] = list.toSet

              if (set.contains(OtherLiabilityIssues.NoExclusion) && set.size > 1)
                set - OtherLiabilityIssues.NoExclusion
              else
                set
            },
            (set: Set[OtherLiabilityIssues]) => {
              set.toList
            }
          )
          .verifying("otherLiabilityIssues.error.required", _.nonEmpty)
      )

}
