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

package services

import java.time.{LocalDate, LocalDateTime}
import com.google.inject.{Singleton, Inject, ImplementedBy}
import play.api.Configuration

@Singleton
class TimeServiceImpl extends TimeService {

  def now: LocalDateTime = LocalDateTime.now
  def date: LocalDate = LocalDate.now

}

@Singleton
class TestTimeService @Inject() (configuration: Configuration) extends TimeService {

  val year = configuration.get[Int]("test-with-tax-year-starting.yearStarting")

  def now: LocalDateTime = LocalDateTime.of(year, 4, 6, 0, 0, 0, 0)
  def date: LocalDate = LocalDate.of(year, 4, 6)

}

@ImplementedBy(classOf[TimeServiceImpl])
trait TimeService {
  def now: LocalDateTime
  def date: LocalDate
}