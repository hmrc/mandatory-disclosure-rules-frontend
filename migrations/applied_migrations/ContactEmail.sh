#!/bin/bash

echo ""
echo "Applying migration ContactEmail"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /contactEmail                        controllers.ContactEmailController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /contactEmail                        controllers.ContactEmailController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeContactEmail                  controllers.ContactEmailController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeContactEmail                  controllers.ContactEmailController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "contactEmail.title = contactEmail" >> ../conf/messages.en
echo "contactEmail.heading = contactEmail" >> ../conf/messages.en
echo "contactEmail.checkYourAnswersLabel = contactEmail" >> ../conf/messages.en
echo "contactEmail.error.required = Enter contactEmail" >> ../conf/messages.en
echo "contactEmail.error.length = ContactEmail must be 100 characters or less" >> ../conf/messages.en
echo "contactEmail.change.hidden = ContactEmail" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryContactEmailUserAnswersEntry: Arbitrary[(ContactEmailPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ContactEmailPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryContactEmailPage: Arbitrary[ContactEmailPage.type] =";\
    print "    Arbitrary(ContactEmailPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ContactEmailPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration ContactEmail completed"
