#!/bin/bash

echo ""
echo "Applying migration FileStatus"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /fileStatus                       controllers.FileStatusController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "fileStatus.title = fileStatus" >> ../conf/messages.en
echo "fileStatus.heading = fileStatus" >> ../conf/messages.en

echo "Migration FileStatus completed"
