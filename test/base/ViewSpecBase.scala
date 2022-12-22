/*
 * Copyright 2022 HM Revenue & Customs
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

package base

import org.scalatestplus.play.PlaySpec
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContent, Request}
import play.api.test.CSRFTokenHelper.CSRFRequest
import play.api.test.{FakeRequest, Injecting}
import config.{NoOpInternalAuthTokenInitialiser, InternalAuthTokenInitialiser}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder

trait ViewSpecBase extends PlaySpec with Injecting {

  val app = new GuiceApplicationBuilder()
    .overrides(bind[InternalAuthTokenInitialiser].to[NoOpInternalAuthTokenInitialiser]).build

  val request: Request[AnyContent] = FakeRequest().withCSRFToken
  protected val realMessagesApi: MessagesApi = inject[MessagesApi]

  implicit def messages: Messages =
    realMessagesApi.preferred(request)

}
