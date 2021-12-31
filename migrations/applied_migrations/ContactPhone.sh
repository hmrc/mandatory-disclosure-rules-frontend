#!/bin/bash

echo ""
echo "Applying migration ContactPhone"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /contactPhone                        controllers.ContactPhoneController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /contactPhone                        controllers.ContactPhoneController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeContactPhone                  controllers.ContactPhoneController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeContactPhone                  controllers.ContactPhoneController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "contactPhone.title = contactPhone" >> ../conf/messages.en
echo "contactPhone.heading = contactPhone" >> ../conf/messages.en
echo "contactPhone.checkYourAnswersLabel = contactPhone" >> ../conf/messages.en
echo "contactPhone.error.required = Enter contactPhone" >> ../conf/messages.en
echo "contactPhone.error.length = ContactPhone must be 100 characters or less" >> ../conf/messages.en
echo "contactPhone.change.hidden = ContactPhone" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryContactPhoneUserAnswersEntry: Arbitrary[(ContactPhonePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ContactPhonePage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryContactPhonePage: Arbitrary[ContactPhonePage.type] =";\
    print "    Arbitrary(ContactPhonePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ContactPhonePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration ContactPhone completed"
