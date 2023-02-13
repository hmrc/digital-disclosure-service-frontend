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

package controllers

import base.SpecBase
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.TaskListView
import viewmodels.{TaskListRow, TaskListViewModel}
import models._
import models.address._
import models.store.notification._
import models.store.disclosure._
import java.time.LocalDate
import models.store.YesNoOrUnsure._

class TaskListControllerSpec extends SpecBase with MockitoSugar {

  val address = Address("line 1", Some("line 2"), Some("line 3"), Some("line 4"), Some("postcode"), Country("GBR"))

  "TaskList Controller" - {

    "must return OK and the correct view for a GET when userAnswers is empty" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      implicit val mess = messages(application)

      running(application) {
        val request = FakeRequest(GET, routes.TaskListController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TaskListView]

        val entity = "individual"
        val isTheUserAgent = true
        val isAllTaskCompleted = false
        val tasksComplete = 0
        val notificationSectionKey = "taskList.add.heading.first.none"

        val personalDetailsTask = TaskListRow(
          id = "personal-detail-task-list",
          sectionTitle = mess("taskList.add.sectionTitle.first.none"),
          status = mess("taskList.status.notStarted"),
          link = controllers.notification.routes.ReceivedALetterController.onPageLoad(NormalMode)
        )

        val caseReferenceTask = TaskListRow(
          id = "case-reference-task-list",
          sectionTitle = mess("taskList.add.sectionTitle.second"),
          status = mess("taskList.status.notStarted"),
          link = controllers.reference.routes.DoYouHaveACaseReferenceController.onPageLoad(NormalMode)
        )

        val otherLiabilitiesTask = TaskListRow(
          id = "other-liability-issues-task-list",
          sectionTitle = mess("taskList.add.sectionTitle.fifth"),
          status = mess("taskList.status.notStarted"),
          link = controllers.otherLiabilities.routes.OtherLiabilityIssuesController.onPageLoad(NormalMode)
        )

        val reasonTask = TaskListRow(
          id = "reason-for-coming-forward-now-task-list",
          sectionTitle = mess("taskList.add.sectionTitle.sixth"),
          status = mess("taskList.status.notStarted"),
          link = reason.routes.WhyAreYouMakingADisclosureController.onPageLoad(NormalMode)
        )
        
        val list = TaskListViewModel(Seq(personalDetailsTask), Seq(caseReferenceTask), Seq(otherLiabilitiesTask, reasonTask))

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(list, notificationSectionKey, isTheUserAgent, entity, isAllTaskCompleted, tasksComplete)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, routes.TaskListController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

  }

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
  val sut = application.injector.instanceOf[TaskListController]
  implicit val mess = messages(application)

  "getStatusMessage" - {

    "should return taskList.status.completed when section is complete" in {
      sut.getStatusMessage(true, true) mustEqual mess("taskList.status.completed")
    }

    "should return taskList.status.inProgress when first page is complete but section is not" in {
      sut.getStatusMessage(false, true) mustEqual mess("taskList.status.inProgress")
    }

    "should return taskList.status.notStarted when no pages are complete" in {
      sut.getStatusMessage(false, false) mustEqual mess("taskList.status.notStarted")
    }

  }

  "buildYourPersonalDetailsRow" - {
    "should display Not Started when the section isn't complete" in {
      
      val personalDetails = PersonalDetails(background = Background(), aboutYou = AboutYou())
      
      val expectedTask = TaskListRow(
        id = "personal-detail-task-list",
        sectionTitle = mess("taskList.add.sectionTitle.first.none"),
        status = mess("taskList.status.notStarted"),
        link = controllers.notification.routes.ReceivedALetterController.onPageLoad(NormalMode)
      )
      val expectedSectionKey = "taskList.add.heading.first.none"
      val (actualKey, actualTask) = sut.buildYourPersonalDetailsRow(personalDetails, "none") 

      actualKey mustEqual expectedSectionKey
      actualTask mustEqual expectedTask    

    }

    "should display In Progress when the section has been started but isn't complete" in {
      
      val personalDetails = PersonalDetails(background = Background(haveYouReceivedALetter = Some(true)), aboutYou = AboutYou())
      
      val expectedTask = TaskListRow(
        id = "personal-detail-task-list",
        sectionTitle = mess("taskList.edit.sectionTitle.first.individual"),
        status = mess("taskList.status.inProgress"),
        link = controllers.notification.routes.ReceivedALetterController.onPageLoad(NormalMode)
      )
      val expectedSectionKey = "taskList.edit.heading.first.individual"
      val (actualKey, actualTask) = sut.buildYourPersonalDetailsRow(personalDetails, "individual") 

      actualKey mustEqual expectedSectionKey
      actualTask mustEqual expectedTask    
    }

    "should display Completed when the section is complete" in {
      val completedBackground = Background(Some(false), None, Some(DisclosureEntity(Individual, Some(true))), Some(false), None, Some(true), Some(false))
      val aboutYou = AboutYou(Some("name"), None, Some("email"), Some(LocalDate.now), Some("mainOccupation"), Some(ContactPreferences(Set(Email))), Some(No), None, Some(No), None, Some(No), None, Some(address))
      val personalDetails = PersonalDetails(completedBackground, aboutYou, None, None, None, None, None)
      
      val expectedTask = TaskListRow(
        id = "personal-detail-task-list",
        sectionTitle = mess("taskList.edit.sectionTitle.first.company"),
        status = mess("taskList.status.completed"),
        link = controllers.notification.routes.CheckYourAnswersController.onPageLoad
      )
      val expectedSectionKey = "taskList.edit.heading.first.company"
      val (actualKey, actualTask) = sut.buildYourPersonalDetailsRow(personalDetails, "company") 

      actualKey mustEqual expectedSectionKey
      actualTask mustEqual expectedTask    
    }
  }

  "buildCaseReferenceRow" - {
    "should display Not Started when the section isn't complete" in {
      
      val model = CaseReference()
      
      val expectedTask = TaskListRow(
        id = "case-reference-task-list",
        sectionTitle = mess("taskList.add.sectionTitle.second"),
        status = mess("taskList.status.notStarted"),
        link = controllers.reference.routes.DoYouHaveACaseReferenceController.onPageLoad(NormalMode)
      )
      val actualTask = sut.buildCaseReferenceRow(model) 

      actualTask mustEqual expectedTask    

    }

    "should display In Progress when the section has been started but isn't complete" in {
      
      val model = CaseReference(doYouHaveACaseReference = Some(true))
      
      val expectedTask = TaskListRow(
        id = "case-reference-task-list",
        sectionTitle = mess("taskList.edit.sectionTitle.second"),
        status = mess("taskList.status.inProgress"),
        link = controllers.reference.routes.DoYouHaveACaseReferenceController.onPageLoad(NormalMode)
      )
      val actualTask = sut.buildCaseReferenceRow(model) 

      actualTask mustEqual expectedTask  
    }

    "should display Completed when the section is complete" in {
      val model = CaseReference(doYouHaveACaseReference = Some(true), whatIsTheCaseReference = Some("Case ref"))
      
      val expectedTask = TaskListRow(
        id = "case-reference-task-list",
        sectionTitle = mess("taskList.edit.sectionTitle.second"),
        status = mess("taskList.status.completed"),
        link = controllers.reference.routes.DoYouHaveACaseReferenceController.onPageLoad(NormalMode)
      )
      val actualTask = sut.buildCaseReferenceRow(model) 

      actualTask mustEqual expectedTask    
    }
  }

  "buildOffshoreLiabilitiesDetailRow" - {
    "should display Not Started when the section isn't complete" in {
      
      val model = OffshoreLiabilities()
      
      val expectedTask = TaskListRow(
        id = "offshore-liabilities-task-list",
        sectionTitle = mess("taskList.add.sectionTitle.forth"),
        status = mess("taskList.status.notStarted"),
        link = controllers.offshore.routes.WhyAreYouMakingThisDisclosureController.onPageLoad(NormalMode)
      )
      val actualTask = sut.buildOffshoreLiabilitiesDetailRow(model) 

      actualTask mustEqual expectedTask    

    }

    "should display In Progress when the section has been started but isn't complete" in {
    
      val model = OffshoreLiabilities(behaviour = Some(Set()))
      
      val expectedTask = TaskListRow(
        id = "offshore-liabilities-task-list",
        sectionTitle = mess("taskList.edit.sectionTitle.forth"),
        status = mess("taskList.status.inProgress"),
        link = controllers.offshore.routes.WhyAreYouMakingThisDisclosureController.onPageLoad(NormalMode)
      )
      val actualTask = sut.buildOffshoreLiabilitiesDetailRow(model) 

      actualTask mustEqual expectedTask  
    }

    "should display Completed when the section is complete" in {

      val liabilities = TaxYearLiabilities(
        income = BigInt(2000),
        chargeableTransfers = BigInt(2000),
        capitalGains = BigInt(2000),
        unpaidTax = BigInt(2000),
        interest = BigInt(2000),
        penaltyRate = 12,
        penaltyRateReason = "Reason",
        foreignTaxCredit = false
      )
      val model = OffshoreLiabilities(
        Some(Set(WhyAreYouMakingThisDisclosure.DidNotNotifyHasExcuse)), 
        None, 
        None, 
        None, 
        Some(Set(TaxYearStarting(2012))),
        None,
        None,
        None,
        None,
        None,
        Some(Map("2012" -> TaxYearWithLiabilities(TaxYearStarting(2012), liabilities))),
        Some(Set(YourLegalInterpretation.NoExclusion)),
        None,
        None,
        Some(TheMaximumValueOfAllAssets.Below500k)
      )

      val expectedTask = TaskListRow(
        id = "offshore-liabilities-task-list",
        sectionTitle = mess("taskList.edit.sectionTitle.forth"),
        status = mess("taskList.status.completed"),
        link = controllers.offshore.routes.CheckYourAnswersController.onPageLoad
      )
      val actualTask = sut.buildOffshoreLiabilitiesDetailRow(model) 

      actualTask mustEqual expectedTask    
    }
  }

  "buildOtherLiabilityIssueRow" - {
    "should display Not Started when the section isn't complete" in {
      
      val model = OtherLiabilities()
      
      val expectedTask = TaskListRow(
        id = "other-liability-issues-task-list",
        sectionTitle = mess("taskList.add.sectionTitle.fifth"),
        status = mess("taskList.status.notStarted"),
        link = controllers.otherLiabilities.routes.OtherLiabilityIssuesController.onPageLoad(NormalMode)
      )
      val actualTask = sut.buildOtherLiabilityIssueRow(model, false) 

      actualTask mustEqual expectedTask    

    }

    "should display In Progress when the section has been started but isn't complete" in {
    
      val model = OtherLiabilities(Some(Set(OtherLiabilityIssues.InheritanceTaxIssues)))

      val expectedTask = TaskListRow(
        id = "other-liability-issues-task-list",
        sectionTitle = mess("taskList.edit.sectionTitle.fifth"),
        status = mess("taskList.status.inProgress"),
        link = controllers.otherLiabilities.routes.OtherLiabilityIssuesController.onPageLoad(NormalMode)
      )
      val actualTask = sut.buildOtherLiabilityIssueRow(model, false) 

      actualTask mustEqual expectedTask  
    }

    "should display Completed when the section is complete" in {

      val model = OtherLiabilities(Some(Set(OtherLiabilityIssues.InheritanceTaxIssues)), Some("Some string"), None, None)

      val expectedTask = TaskListRow(
        id = "other-liability-issues-task-list",
        sectionTitle = mess("taskList.edit.sectionTitle.fifth"),
        status = mess("taskList.status.completed"),
        link = controllers.otherLiabilities.routes.CheckYourAnswersController.onPageLoad
      )
      val actualTask = sut.buildOtherLiabilityIssueRow(model, false) 

      actualTask mustEqual expectedTask    
    }
  }

  "buildTheReasonForComingForwardNowRow" - {
    "should display Not Started when the section isn't complete" in {
      
      val model = ReasonForDisclosingNow()
      
      val expectedTask = TaskListRow(
        id = "reason-for-coming-forward-now-task-list",
        sectionTitle = mess("taskList.add.sectionTitle.sixth"),
        status = mess("taskList.status.notStarted"),
        link = reason.routes.WhyAreYouMakingADisclosureController.onPageLoad(NormalMode)
      )
      val actualTask = sut.buildTheReasonForComingForwardNowRow(model) 

      actualTask mustEqual expectedTask    

    }

    "should display In Progress when the section has been started but isn't complete" in {
    
      val model = ReasonForDisclosingNow(Some(Set(WhyAreYouMakingADisclosure.GovUkGuidance)))

      val expectedTask = TaskListRow(
        id = "reason-for-coming-forward-now-task-list",
        sectionTitle = mess("taskList.edit.sectionTitle.sixth"),
        status = mess("taskList.status.inProgress"),
        link = reason.routes.WhyAreYouMakingADisclosureController.onPageLoad(NormalMode)
      )
      val actualTask = sut.buildTheReasonForComingForwardNowRow(model) 

      actualTask mustEqual expectedTask  
    }

    "should display Completed when the section is complete" in {

      val model = ReasonForDisclosingNow(
        Some(Set(WhyAreYouMakingADisclosure.GovUkGuidance)), 
        None, 
        Some("Some reason"), 
        Some(true), 
        Some("Some guy"), 
        Some(false), 
        None, 
        Some("Some profession"),
        Some(AdviceGiven("Some advice", MonthYear(12, 2012), AdviceContactPreference.No))
      )

      val expectedTask = TaskListRow(
        id = "reason-for-coming-forward-now-task-list",
        sectionTitle = mess("taskList.edit.sectionTitle.sixth"),
        status = mess("taskList.status.completed"),
        link = controllers.reason.routes.CheckYourAnswersController.onPageLoad
      )
      val actualTask = sut.buildTheReasonForComingForwardNowRow(model) 

      actualTask mustEqual expectedTask    
    }
  }
}