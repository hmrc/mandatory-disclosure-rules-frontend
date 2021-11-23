#!/bin/bash

echo ""
echo "Applying migration FileValidation"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /fileValidation                       controllers.FileValidationController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "fileValidation.title = fileValidation" >> ../conf/messages.en
echo "fileValidation.heading = fileValidation" >> ../conf/messages.en

echo "Migration FileValidation completed"
