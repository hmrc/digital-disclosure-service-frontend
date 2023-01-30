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

package models

import play.api.libs.json._
import queries.{Gettable, Settable}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats
import models.store.notification._
import java.time.Instant
import scala.util.{Failure, Success, Try}
import pages.WhichYearsPage

final case class UserAnswers(
                              id: String,
                              notificationId: String = UserAnswers.defaultNotificationId,
                              submissionType: SubmissionType = SubmissionType.Notification,
                              data: JsObject = Json.obj(),
                              lastUpdated: Instant = Instant.now,
                              metadata: Metadata = Metadata()
                            ) {

  def get[A](page: Gettable[A])(implicit rds: Reads[A]): Option[A] = get(page.path)

  def set[A](page: Settable[A], value: A)(implicit writes: Writes[A]): Try[UserAnswers] = {
    val updatedData = set(page.path, value)
    cleanupPage(page, updatedData)
  }

  def remove[A](page: Settable[A]): Try[UserAnswers] = {
    val updatedData = remove(page.path)
    cleanupPage(page, updatedData)
  }

  def getByIndex[A](page: Gettable[Set[A]], index: Int)(implicit rds: Reads[A]): Option[A] = {
    val path = page.path \ index
    get(path)
  }

  def setByIndex[A](page: Settable[Set[A]], index: Int, value: A)(implicit writes: Writes[A]): Try[UserAnswers] = {
    val path = page.path \ index
    val updatedData = set(path, value)
    cleanupPage(page, updatedData)
  }

  def addToSet[A](page: Settable[Set[A]], value: A)(implicit writes:Writes[A], rds: Reads[A]): Try[UserAnswers] = {
    val path = page.path
    val data = get[Set[A]](path)
    val updatedData = data match {
      case Some(valueSet:Set[A]) => set(path, valueSet + value)
      case _ => set(path, Set(value))
    }
    cleanupPage(page, updatedData)
  }

  def removeByIndex[A](page: Settable[Set[A]], index: Int): Try[UserAnswers] = {
    val path = page.path \ index
    val updatedData = remove(path)
    cleanupPage(page, updatedData)
  }

  def getByKey[A](page: Gettable[Map[String, A]], key: String)(implicit rds: Reads[A]): Option[A] = {
    val path = page.path \ key
    get(path)
  }

  def setByKey[A](page: Settable[Map[String, A]], key: String, value: A)(implicit writes: Writes[A]): Try[UserAnswers] = {
    val path = page.path \ key
    val updatedData = set(path, value)
    cleanupPage(page, updatedData)
  }

  def removeByKey[A](page: Settable[Map[String, A]], key: String): Try[UserAnswers] = {
    val path = page.path \ key
    val updatedData = remove(path)
    cleanupPage(page, updatedData)
  }

  def get[A](path: JsPath)(implicit rds: Reads[A]): Option[A] =
    Reads.optionNoError(Reads.at(path)).reads(data).getOrElse(None)

  def set[A](path: JsPath, value: A)(implicit writes: Writes[A]): Try[JsObject] =
    data.setObject(path, Json.toJson(value)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(errors) =>
        Failure(JsResultException(errors))
    }


  def remove[A](path: JsPath): Try[JsObject] =
    data.removeObject(path) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(_) =>
        Success(data)
    }

  def remove(pages: List[Settable[_]]) : Try[UserAnswers] = 
    pages.foldLeft(Try(this))((oldAnswerList, page) => {
      oldAnswerList.flatMap(_.remove(page))
    })

  def cleanupPage(page: Settable[_], updatedData: Try[JsObject]): Try[UserAnswers] = {
    updatedData.flatMap {
      d =>
        val updatedAnswers = copy (data = d)
        page.cleanup(None, updatedAnswers)
    }
  }

  def inverselySortedOffshoreTaxYears: Option[Seq[TaxYearStarting]] = get(WhichYearsPage).map{ yearSet =>
    yearSet.collect{case TaxYearStarting(y) => TaxYearStarting(y)}.toSeq.sorted
  }

}

object UserAnswers {

  val defaultNotificationId = "Individual"

  val reads: Reads[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "_id").read[String] and
      (__ \ "notificationId").read[String] and
      (__ \ "submissionType").read[SubmissionType] and
      (__ \ "data").read[JsObject] and
      (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat) and
      (__ \ "metadata").read[Metadata]
    ) (UserAnswers.apply _)
  }

  val writes: OWrites[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "_id").write[String] and
      (__ \ "notificationId").write[String] and
      (__ \ "submissionType").write[SubmissionType] and
      (__ \ "data").write[JsObject] and
      (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat) and
      (__ \ "metadata").write[Metadata] 
    ) (unlift(UserAnswers.unapply))
  }

  implicit val format: OFormat[UserAnswers] = OFormat(reads, writes)
}
