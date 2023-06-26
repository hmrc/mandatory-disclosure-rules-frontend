#!/bin/bash

echo ""
echo "Applying migration RegisteredAddressInUK"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /registeredAddressInUK                        controllers.RegisteredAddressInUKController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /registeredAddressInUK                        controllers.RegisteredAddressInUKController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeRegisteredAddressInUK                  controllers.RegisteredAddressInUKController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeRegisteredAddressInUK                  controllers.RegisteredAddressInUKController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "registeredAddressInUK.title = registeredAddressInUK" >> ../conf/messages.en
echo "registeredAddressInUK.heading = registeredAddressInUK" >> ../conf/messages.en
echo "registeredAddressInUK.checkYourAnswersLabel = registeredAddressInUK" >> ../conf/messages.en
echo "registeredAddressInUK.error.required = Select yes if registeredAddressInUK" >> ../conf/messages.en
echo "registeredAddressInUK.change.hidden = registeredAddressInUK" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryregisteredAddressInUKUserAnswersEntry: Arbitrary[(registeredAddressInUKPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[registeredAddressInUKPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryregisteredAddressInUKPage: Arbitrary[registeredAddressInUKPage.type] =";\
    print "    Arbitrary(registeredAddressInUKPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(registeredAddressInUKPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration registeredAddressInUK completed"
