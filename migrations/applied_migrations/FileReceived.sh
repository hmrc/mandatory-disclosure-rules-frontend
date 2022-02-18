#!/bin/bash

echo ""
echo "Applying migration FileReceived"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /fileReceived                       controllers.FileReceivedController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "fileReceived.title = fileReceived" >> ../conf/messages.en
echo "fileReceived.heading = fileReceived" >> ../conf/messages.en

echo "Migration FileReceived completed"
