#!/bin/bash

echo ""
echo "Applying migration FileCheck"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /fileCheck                       controllers.FileCheckController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "fileCheck.title = fileCheck" >> ../conf/messages.en
echo "fileCheck.heading = fileCheck" >> ../conf/messages.en

echo "Migration FileCheck completed"
