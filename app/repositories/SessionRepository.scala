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

package repositories

import config.FrontendAppConfig
import models.UserAnswers
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model._
import play.api.libs.json.Format
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.{Clock, Instant}
import java.util.concurrent.TimeUnit
import com.google.inject.{ImplementedBy, Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SessionRepositoryImpl @Inject() (
  mongoComponent: MongoComponent,
  appConfig: FrontendAppConfig,
  clock: Clock
)(implicit ec: ExecutionContext)
    extends PlayMongoRepository[UserAnswers](
      collectionName = "user-answers",
      mongoComponent = mongoComponent,
      domainFormat = UserAnswers.format,
      indexes = Seq(
        IndexModel(
          Indexes.ascending("lastUpdated"),
          IndexOptions()
            .name("lastUpdatedIdx")
            .expireAfter(appConfig.cacheTtl, TimeUnit.SECONDS)
        ),
        IndexModel(
          Indexes.compoundIndex(
            Indexes.ascending("userId"),
            Indexes.ascending("sessionId")
          ),
          IndexOptions()
            .name("sessionIdx")
            .unique(true)
        )
      ),
      replaceIndexes = true
    )
    with SessionRepository {

  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat

  private def byId(userId: String, sessionId: String): Bson = Filters.and(
    Filters.equal("userId", userId),
    Filters.equal("sessionId", sessionId)
  )

  def keepAlive(userId: String, sessionId: String): Future[Boolean] =
    collection
      .updateOne(
        filter = byId(userId, sessionId),
        update = Updates.set("lastUpdated", Instant.now(clock))
      )
      .toFuture()
      .map(_ => true)

  def get(userId: String, sessionId: String): Future[Option[UserAnswers]] =
    keepAlive(userId, sessionId).flatMap { _ =>
      collection
        .find(byId(userId, sessionId))
        .headOption()
    }

  def set(answers: UserAnswers): Future[Boolean] = {

    val updatedAnswers = answers copy (lastUpdated = Instant.now(clock))

    collection
      .replaceOne(
        filter = byId(updatedAnswers.id, updatedAnswers.sessionId),
        replacement = updatedAnswers,
        options = ReplaceOptions().upsert(true)
      )
      .toFuture()
      .map(_ => true)
  }

  def clear(userId: String, sessionId: String): Future[Boolean] =
    collection
      .deleteOne(byId(userId, sessionId))
      .toFuture()
      .map(_ => true)
}

@ImplementedBy(classOf[SessionRepositoryImpl])
trait SessionRepository {
  def set(answers: UserAnswers): Future[Boolean]
  def get(userId: String, sessionId: String): Future[Option[UserAnswers]]
  def keepAlive(userId: String, sessionId: String): Future[Boolean]
  def clear(userId: String, sessionId: String): Future[Boolean]
}
