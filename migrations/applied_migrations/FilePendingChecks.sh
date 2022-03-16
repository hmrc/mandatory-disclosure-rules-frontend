#!/bin/bash

echo ""
echo "Applying migration FilePendingChecks"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /filePendingChecks                       controllers.FilePendingChecksController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "filePendingChecks.title = filePendingChecks" >> ../conf/messages.en
echo "filePendingChecks.heading = filePendingChecks" >> ../conf/messages.en

echo "Migration FilePendingChecks completed"
