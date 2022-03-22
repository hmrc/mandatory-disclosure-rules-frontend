#!/bin/bash

echo ""
echo "Applying migration FilePassedChecks"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /filePassedChecks                       controllers.FilePassedChecksController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "filePassedChecks.title = filePassedChecks" >> ../conf/messages.en
echo "filePassedChecks.heading = filePassedChecks" >> ../conf/messages.en

echo "Migration FilePassedChecks completed"
