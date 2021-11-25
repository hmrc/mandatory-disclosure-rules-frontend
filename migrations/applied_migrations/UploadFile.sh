#!/bin/bash

echo ""
echo "Applying migration UploadFile"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /uploadFile                       controllers.UploadFileController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "uploadFile.title = uploadFile" >> ../conf/messages.en
echo "uploadFile.heading = uploadFile" >> ../conf/messages.en

echo "Migration UploadFile completed"
