#!/bin/bash

echo ""
echo "Applying migration HaveTelephone"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /haveTelephone                        controllers.HaveTelephoneController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /haveTelephone                        controllers.HaveTelephoneController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHaveTelephone                  controllers.HaveTelephoneController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHaveTelephone                  controllers.HaveTelephoneController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "haveTelephone.title = haveTelephone" >> ../conf/messages.en
echo "haveTelephone.heading = haveTelephone" >> ../conf/messages.en
echo "haveTelephone.checkYourAnswersLabel = haveTelephone" >> ../conf/messages.en
echo "haveTelephone.error.required = Select yes if haveTelephone" >> ../conf/messages.en
echo "haveTelephone.change.hidden = HaveTelephone" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHaveTelephoneUserAnswersEntry: Arbitrary[(HaveTelephonePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[HaveTelephonePage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHaveTelephonePage: Arbitrary[HaveTelephonePage.type] =";\
    print "    Arbitrary(HaveTelephonePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(HaveTelephonePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration HaveTelephone completed"
