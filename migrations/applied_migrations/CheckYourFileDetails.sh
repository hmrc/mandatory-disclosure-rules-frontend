#!/bin/bash

echo ""
echo "Applying migration CheckYourFileDetails"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /checkYourFileDetails                       controllers.CheckYourFileDetailsController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "checkYourFileDetails.title = checkYourFileDetails" >> ../conf/messages.en
echo "checkYourFileDetails.heading = checkYourFileDetails" >> ../conf/messages.en

echo "Migration CheckYourFileDetails completed"
