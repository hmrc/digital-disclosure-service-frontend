#!/bin/bash

echo ""
echo "Applying migration ReceivedALetter"

echo "Adding routes to conf/$section$.routes"

echo "" >> ../conf/$section$.routes
echo "GET        /receivedALetter                        controllers.ReceivedALetterController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/$section$.routes
echo "POST       /receivedALetter                        controllers.ReceivedALetterController.onSubmit(mode: Mode = NormalMode)" >> ../conf/$section$.routes

echo "GET        /changeReceivedALetter                  controllers.ReceivedALetterController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/$section$.routes
echo "POST       /changeReceivedALetter                  controllers.ReceivedALetterController.onSubmit(mode: Mode = CheckMode)" >> ../conf/$section$.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "receivedALetter.title = receivedALetter" >> ../conf/messages.en
echo "receivedALetter.heading = receivedALetter" >> ../conf/messages.en
echo "receivedALetter.checkYourAnswersLabel = receivedALetter" >> ../conf/messages.en
echo "receivedALetter.error.required = Select yes if receivedALetter" >> ../conf/messages.en
echo "receivedALetter.change.hidden = ReceivedALetter" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReceivedALetterUserAnswersEntry: Arbitrary[(ReceivedALetterPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReceivedALetterPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReceivedALetterPage: Arbitrary[ReceivedALetterPage.type] =";\
    print "    Arbitrary(ReceivedALetterPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReceivedALetterPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration ReceivedALetter completed"
