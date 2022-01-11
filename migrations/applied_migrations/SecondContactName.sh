#!/bin/bash

echo ""
echo "Applying migration SecondContactName"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /secondContactName                        controllers.SecondContactNameController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /secondContactName                        controllers.SecondContactNameController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSecondContactName                  controllers.SecondContactNameController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSecondContactName                  controllers.SecondContactNameController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "secondContactName.title = secondContactName" >> ../conf/messages.en
echo "secondContactName.heading = secondContactName" >> ../conf/messages.en
echo "secondContactName.checkYourAnswersLabel = secondContactName" >> ../conf/messages.en
echo "secondContactName.error.required = Enter secondContactName" >> ../conf/messages.en
echo "secondContactName.error.length = SecondContactName must be 100 characters or less" >> ../conf/messages.en
echo "secondContactName.change.hidden = SecondContactName" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySecondContactNameUserAnswersEntry: Arbitrary[(SecondContactNamePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SecondContactNamePage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySecondContactNamePage: Arbitrary[SecondContactNamePage.type] =";\
    print "    Arbitrary(SecondContactNamePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SecondContactNamePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration SecondContactName completed"
