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

package models.store.notification

import play.api.libs.json.{Json, OFormat}
import java.time.Instant
import play.api.Logging 

final case class Notification (
  userId: String,
  notificationId: String,
  lastUpdated: Instant,
  metadata: Metadata,
  background: Background,
  aboutYou: AboutYou,
  aboutTheIndividual: Option[AboutTheIndividual] = None,
  aboutTheCompany: Option[AboutTheCompany] = None,
  aboutTheTrust: Option[AboutTheTrust] = None,
  aboutTheLLP: Option[AboutTheLLP] = None,
  aboutTheEstate: Option[AboutTheEstate] = None,
  customerId: Option[CustomerId] = None
) extends Logging {
  def disclosingAboutThemselves: Boolean = background.disclosureEntity match {
    case Some(DisclosureEntity(Individual, Some(true))) => true
    case _ => false
  }

  def isComplete: Boolean = {
    val sectionsCompleteForEntity: Boolean = background.disclosureEntity.map(_.entity) match {
      case Some(Individual) if disclosingAboutThemselves => aboutYou.isComplete(true)
      case Some(Individual) => aboutYou.isComplete(false) && (aboutTheIndividual.map(_.isComplete) == Some(true))
      case Some(Estate) => aboutYou.isComplete(false) && (aboutTheEstate.map(_.isComplete) == Some(true))
      case Some(Company) => aboutYou.isComplete(false) && (aboutTheCompany.map(_.isComplete) == Some(true))
      case Some(LLP) => aboutYou.isComplete(false) && (aboutTheLLP.map(_.isComplete) == Some(true))
      case Some(Trust) => aboutYou.isComplete(false) && (aboutTheTrust.map(_.isComplete) == Some(true))
      case _ => false
    }
    logger.info(s"------------------------")
    logger.info(s"------------------------")
    logger.info(s"------------------------")
    logger.info(s"------------------------")
    logger.info(s"------------------------")
    logger.info(s"sectionsCompleteForEntity: $sectionsCompleteForEntity")
    logger.info(s"background: ${background.isComplete}")
    logger.info(s"themselves: $disclosingAboutThemselves")
    background.isComplete && sectionsCompleteForEntity
  }
}

object Notification {
  implicit val format: OFormat[Notification] = Json.format[Notification]
}