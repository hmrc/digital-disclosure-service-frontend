#!/bin/bash

echo ""
echo "Applying migration PropertyStoppedBeingLetOut"

echo "Adding routes to conf/letting.routes"

echo "" >> ../conf/letting.routes
echo "GET        /letting-property-has-letting-stopped                         controllers.letting.PropertyStoppedBeingLetOutController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/letting.routes
echo "POST       /letting-property-has-letting-stopped                         controllers.letting.PropertyStoppedBeingLetOutController.onSubmit(mode: Mode = NormalMode)" >> ../conf/letting.routes

echo "GET        /letting-property-has-letting-stopped/change                 controllers.letting.PropertyStoppedBeingLetOutController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/letting.routes
echo "POST       /letting-property-has-letting-stopped/change                 controllers.letting.PropertyStoppedBeingLetOutController.onSubmit(mode: Mode = CheckMode)" >> ../conf/letting.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "propertyStoppedBeingLetOut.title = propertyStoppedBeingLetOut" >> ../conf/messages.en
echo "propertyStoppedBeingLetOut.heading = propertyStoppedBeingLetOut" >> ../conf/messages.en
echo "propertyStoppedBeingLetOut.checkYourAnswersLabel = propertyStoppedBeingLetOut" >> ../conf/messages.en
echo "propertyStoppedBeingLetOut.error.required = Select yes if propertyStoppedBeingLetOut" >> ../conf/messages.en
echo "propertyStoppedBeingLetOut.change.hidden = PropertyStoppedBeingLetOut" >> ../conf/messages.en
echo "propertyStoppedBeingLetOut.yes = Yes" >> ../conf/messages.en
echo "propertyStoppedBeingLetOut.no = No" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPropertyStoppedBeingLetOutUserAnswersEntry: Arbitrary[(PropertyStoppedBeingLetOutPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[PropertyStoppedBeingLetOutPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPropertyStoppedBeingLetOutPage: Arbitrary[PropertyStoppedBeingLetOutPage.type] =";\
    print "    Arbitrary(PropertyStoppedBeingLetOutPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(PropertyStoppedBeingLetOutPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration PropertyStoppedBeingLetOut completed"
