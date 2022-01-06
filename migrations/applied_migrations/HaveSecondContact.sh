#!/bin/bash

echo ""
echo "Applying migration HaveSecondContact"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /haveSecondContact                        controllers.HaveSecondContactController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /haveSecondContact                        controllers.HaveSecondContactController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHaveSecondContact                  controllers.HaveSecondContactController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHaveSecondContact                  controllers.HaveSecondContactController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "haveSecondContact.title = haveSecondContact" >> ../conf/messages.en
echo "haveSecondContact.heading = haveSecondContact" >> ../conf/messages.en
echo "haveSecondContact.checkYourAnswersLabel = haveSecondContact" >> ../conf/messages.en
echo "haveSecondContact.error.required = Select yes if haveSecondContact" >> ../conf/messages.en
echo "haveSecondContact.change.hidden = HaveSecondContact" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHaveSecondContactUserAnswersEntry: Arbitrary[(HaveSecondContactPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[HaveSecondContactPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHaveSecondContactPage: Arbitrary[HaveSecondContactPage.type] =";\
    print "    Arbitrary(HaveSecondContactPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(HaveSecondContactPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration HaveSecondContact completed"
