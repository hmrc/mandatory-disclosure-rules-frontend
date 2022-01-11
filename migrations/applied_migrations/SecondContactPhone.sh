#!/bin/bash

echo ""
echo "Applying migration SecondContactPhone"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /secondContactPhone                        controllers.SecondContactPhoneController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /secondContactPhone                        controllers.SecondContactPhoneController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSecondContactPhone                  controllers.SecondContactPhoneController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSecondContactPhone                  controllers.SecondContactPhoneController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "secondContactPhone.title = secondContactPhone" >> ../conf/messages.en
echo "secondContactPhone.heading = secondContactPhone" >> ../conf/messages.en
echo "secondContactPhone.checkYourAnswersLabel = secondContactPhone" >> ../conf/messages.en
echo "secondContactPhone.error.required = Enter secondContactPhone" >> ../conf/messages.en
echo "secondContactPhone.error.length = SecondContactPhone must be 100 characters or less" >> ../conf/messages.en
echo "secondContactPhone.change.hidden = SecondContactPhone" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySecondContactPhoneUserAnswersEntry: Arbitrary[(SecondContactPhonePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SecondContactPhonePage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySecondContactPhonePage: Arbitrary[SecondContactPhonePage.type] =";\
    print "    Arbitrary(SecondContactPhonePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SecondContactPhonePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration SecondContactPhone completed"
