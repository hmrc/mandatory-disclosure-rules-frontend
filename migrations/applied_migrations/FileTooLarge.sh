#!/bin/bash

echo ""
echo "Applying migration FileTooLarge"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /fileTooLarge                       controllers.FileTooLargeController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "fileTooLarge.title = fileTooLarge" >> ../conf/messages.en
echo "fileTooLarge.heading = fileTooLarge" >> ../conf/messages.en

echo "Migration FileTooLarge completed"
