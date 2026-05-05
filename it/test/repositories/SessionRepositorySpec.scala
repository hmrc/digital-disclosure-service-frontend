/*
 * Copyright 2024 HM Revenue & Customs
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

package repositories

import config.FrontendAppConfig
import models.{UserAnswers, SubmissionType}
import org.mockito.Mockito.when
import org.mongodb.scala.model.Filters
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.Json
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import org.scalatest.matchers.should.Matchers

import java.time.{Clock, Instant, ZoneId}
import java.time.temporal.ChronoUnit
import scala.concurrent.ExecutionContext.Implicits.global

class SessionRepositorySpec
  extends AnyFreeSpec
    with Matchers
    with DefaultPlayMongoRepositorySupport[UserAnswers]
    with ScalaFutures
    with IntegrationPatience
    with OptionValues
    with MockitoSugar {

  private val instant = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val stubClock: Clock = Clock.fixed(instant, ZoneId.systemDefault)

  private val userAnswers = UserAnswers("id", "session-123", "submissionId", SubmissionType.Notification, Json.obj("foo" -> "bar"), Instant.ofEpochSecond(1), created = instant)

  private val mockAppConfig = mock[FrontendAppConfig]
  when(mockAppConfig.cacheTtl) `thenReturn` 1

  private val repoImpl = new SessionRepositoryImpl(
    mongoComponent = mongoComponent,
    appConfig      = mockAppConfig,
    clock          = stubClock
  )
  override protected val repository: PlayMongoRepository[UserAnswers] =
    repoImpl

  private val sessionRepository: SessionRepository =
    repoImpl

//  protected override val repository = new SessionRepositoryImpl(
//    mongoComponent = mongoComponent,
//    appConfig      = mockAppConfig,
//    clock          = stubClock
//  )

  ".set" - {

    "must set the last updated time on the supplied user answers to `now`, and save them" in {

      val expectedResult = userAnswers.copy(lastUpdated = instant)

      val setResult     = sessionRepository.set(userAnswers).futureValue
      val updatedRecord = find(Filters.equal("userId", userAnswers.id)).futureValue.headOption.value

      setResult shouldBe true
      updatedRecord shouldBe expectedResult
    }
  }

  ".get" - {

    "when there is a record for this id" - {

      "must update the lastUpdated time and get the record" in {

        insert(userAnswers).futureValue

        val result         = sessionRepository.get(userAnswers.id, userAnswers.sessionId).futureValue
        val expectedResult = userAnswers.copy(lastUpdated = instant)

        result.value shouldBe expectedResult
      }
    }

    "when there is no record for this id" - {

      "must return None" in {

        sessionRepository.get("id that does not exist", "session id that does not exist").futureValue shouldBe empty
      }
    }
  }

  ".clear" - {

    "must remove a record" in {

      insert(userAnswers).futureValue

      val result = sessionRepository.clear(userAnswers.id, userAnswers.sessionId).futureValue

      result shouldBe true
      sessionRepository.get(userAnswers.id, userAnswers.sessionId).futureValue shouldBe empty
    }

    "must return true when there is no record to remove" in {
      val result = sessionRepository.clear("id that does not exist", "session id that does not exist").futureValue

      result shouldBe true
    }
  }

  ".keepAlive" - {

    "when there is a record for this id" - {

      "must update its lastUpdated to `now` and return true" in {

        insert(userAnswers).futureValue

        val result = sessionRepository.keepAlive(userAnswers.id, userAnswers.sessionId).futureValue

        val expectedUpdatedAnswers = userAnswers.copy(lastUpdated = instant)

        result shouldBe true
        val updatedAnswers = find(Filters.equal("userId", userAnswers.id)).futureValue.headOption.value
        updatedAnswers shouldBe expectedUpdatedAnswers
      }
    }

    "when there is no record for this id" - {

      "must return true" in {

        sessionRepository.keepAlive("id that does not exist", "session id that does not exist").futureValue shouldBe true
      }
    }
  }
}
