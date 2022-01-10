#!/bin/bash

echo ""
echo "Applying migration SecondContactHavePhone"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /secondContactHavePhone                        controllers.SecondContactHavePhoneController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /secondContactHavePhone                        controllers.SecondContactHavePhoneController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSecondContactHavePhone                  controllers.SecondContactHavePhoneController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSecondContactHavePhone                  controllers.SecondContactHavePhoneController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "secondContactHavePhone.title = secondContactHavePhone" >> ../conf/messages.en
echo "secondContactHavePhone.heading = secondContactHavePhone" >> ../conf/messages.en
echo "secondContactHavePhone.checkYourAnswersLabel = secondContactHavePhone" >> ../conf/messages.en
echo "secondContactHavePhone.error.required = Select yes if secondContactHavePhone" >> ../conf/messages.en
echo "secondContactHavePhone.change.hidden = SecondContactHavePhone" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySecondContactHavePhoneUserAnswersEntry: Arbitrary[(SecondContactHavePhonePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SecondContactHavePhonePage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySecondContactHavePhonePage: Arbitrary[SecondContactHavePhonePage.type] =";\
    print "    Arbitrary(SecondContactHavePhonePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SecondContactHavePhonePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration SecondContactHavePhone completed"
