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

package base

import org.mockito.ArgumentMatchers.{any, refEq}
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SessionService
import play.api.libs.json.Writes
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import scala.concurrent.Future
import models.UserAnswers

trait ControllerSpecBase extends SpecBase with MockitoSugar with ScalaCheckPropertyChecks {

  def testChangeAnswerRouting[A](
                                  previousAnswer: A,
                                  newAnswer:A,
                                  page: QuestionPage[A],
                                  urlToTest: String,
                                  destinationRoute: String,
                                  pagesToRemove: List[QuestionPage[_]] = Nil
                                )(implicit writes: Writes[A]) = {

    val userAnswers = UserAnswers("id")

    val mockSessionService = mock[SessionService]
    when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

    val previousUa = userAnswers.set(page, previousAnswer).success.value

    val expectedUa = (for {
      updatedUa <-  userAnswers.set(page, newAnswer)
      clearedUa <- updatedUa.remove(pagesToRemove)
    } yield clearedUa).success.value

    val application = applicationBuilderWithSessionService(userAnswers = Some(previousUa), mockSessionService).build()

    running(application) {
      val request =
        FakeRequest(POST, urlToTest)
          .withFormUrlEncodedBody(("value", newAnswer.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual destinationRoute

      verify(mockSessionService, times(1)).set(refEq(expectedUa))(any())
    }
    
  }
}
