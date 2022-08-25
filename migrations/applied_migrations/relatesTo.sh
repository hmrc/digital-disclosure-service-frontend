#!/bin/bash

echo ""
echo "Applying migration relatesTo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /relatesTo                        controllers.relatesToController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /relatesTo                        controllers.relatesToController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changerelatesTo                  controllers.relatesToController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changerelatesTo                  controllers.relatesToController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "relatesTo.title = Who Does this disclosure relate to?" >> ../conf/messages.en
echo "relatesTo.heading = Who Does this disclosure relate to?" >> ../conf/messages.en
echo "relatesTo.an individual = Option 1" >> ../conf/messages.en
echo "relatesTo.an estate = Option 2" >> ../conf/messages.en
echo "relatesTo.checkYourAnswersLabel = Who Does this disclosure relate to?" >> ../conf/messages.en
echo "relatesTo.error.required = Select relatesTo" >> ../conf/messages.en
echo "relatesTo.change.hidden = relatesTo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryrelatesToUserAnswersEntry: Arbitrary[(relatesToPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[relatesToPage.type]";\
    print "        value <- arbitrary[relatesTo].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryrelatesToPage: Arbitrary[relatesToPage.type] =";\
    print "    Arbitrary(relatesToPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryrelatesTo: Arbitrary[relatesTo] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(relatesTo.values.toSeq)";\
    print "    }";\
    next }1' ../test-utils/generators/ModelGenerators.scala > tmp && mv tmp ../test-utils/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(relatesToPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration relatesTo completed"
