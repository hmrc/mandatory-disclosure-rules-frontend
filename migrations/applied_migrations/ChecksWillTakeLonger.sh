#!/bin/bash

echo ""
echo "Applying migration ChecksWillTakeLonger"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /checksWillTakeLonger                       controllers.ChecksWillTakeLongerController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "checksWillTakeLonger.title = checksWillTakeLonger" >> ../conf/messages.en
echo "checksWillTakeLonger.heading = checksWillTakeLonger" >> ../conf/messages.en

echo "Migration ChecksWillTakeLonger completed"
