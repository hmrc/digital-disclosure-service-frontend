#!/bin/bash

echo ""
echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/$section$.routes"

echo "" >> ../conf/$section$.routes
echo "GET        /$url$                  controllers.$section$.$className$Controller.onPageLoad(mode: Mode = NormalMode)" >> ../conf/$section$.routes
echo "POST       /$url$                  controllers.$section$.$className$Controller.onSubmit(mode: Mode = NormalMode)" >> ../conf/$section$.routes

echo "GET        /$url$/change                        controllers.$section$.$className$Controller.onPageLoad(mode: Mode = CheckMode)" >> ../conf/$section$.routes
echo "POST       /$url$/change                        controllers.$section$.$className$Controller.onSubmit(mode: Mode = CheckMode)" >> ../conf/$section$.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "$className;format="decap"$.title = $className$" >> ../conf/messages.en
echo "$className;format="decap"$.heading = $className$" >> ../conf/messages.en
echo "$className;format="decap"$.checkYourAnswersLabel = $className$" >> ../conf/messages.en
echo "$className;format="decap"$.error.nonNumeric = Enter your $className;format="decap"$ using numbers" >> ../conf/messages.en
echo "$className;format="decap"$.error.required = Enter your $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.error.wholeNumber = Enter your $className;format="decap"$ using whole numbers" >> ../conf/messages.en
echo "$className;format="decap"$.error.outOfRange = $className$ must be between {0} and {1}" >> ../conf/messages.en
echo "$className;format="decap"$.change.hidden = $className$" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrary$className$UserAnswersEntry: Arbitrary[($className$Page.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[$className$Page.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrary$className$Page: Arbitrary[$className$Page.type] =";\
    print "    Arbitrary($className$Page)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[($className$Page.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration $className;format="snake"$ completed"
