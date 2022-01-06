#!/bin/bash

echo ""
echo "Applying migration SecondContactEmail"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /secondContactEmail                        controllers.SecondContactEmailController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /secondContactEmail                        controllers.SecondContactEmailController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSecondContactEmail                  controllers.SecondContactEmailController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSecondContactEmail                  controllers.SecondContactEmailController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "secondContactEmail.title = secondContactEmail" >> ../conf/messages.en
echo "secondContactEmail.heading = secondContactEmail" >> ../conf/messages.en
echo "secondContactEmail.checkYourAnswersLabel = secondContactEmail" >> ../conf/messages.en
echo "secondContactEmail.error.required = Enter secondContactEmail" >> ../conf/messages.en
echo "secondContactEmail.error.length = SecondContactEmail must be 100 characters or less" >> ../conf/messages.en
echo "secondContactEmail.change.hidden = SecondContactEmail" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySecondContactEmailUserAnswersEntry: Arbitrary[(SecondContactEmailPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SecondContactEmailPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySecondContactEmailPage: Arbitrary[SecondContactEmailPage.type] =";\
    print "    Arbitrary(SecondContactEmailPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SecondContactEmailPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration SecondContactEmail completed"
