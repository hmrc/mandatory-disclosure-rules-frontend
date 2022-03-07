#!/bin/bash

echo ""
echo "Applying migration SendYourFile"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /sendYourFile                       controllers.SendYourFileController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "sendYourFile.title = sendYourFile" >> ../conf/messages.en
echo "sendYourFile.heading = sendYourFile" >> ../conf/messages.en

echo "Migration SendYourFile completed"
