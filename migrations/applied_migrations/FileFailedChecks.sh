#!/bin/bash

echo ""
echo "Applying migration FileFailedChecks"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /fileFailedChecks                       controllers.FileFailedChecksController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "fileFailedChecks.title = fileFailedChecks" >> ../conf/messages.en
echo "fileFailedChecks.heading = fileFailedChecks" >> ../conf/messages.en

echo "Migration FileFailedChecks completed"
