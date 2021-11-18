#!/bin/bash

echo ""
echo "Applying migration VirusFileFound"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /virusFileFound                       controllers.VirusFileFoundController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "virusFileFound.title = virusFileFound" >> ../conf/messages.en
echo "virusFileFound.heading = virusFileFound" >> ../conf/messages.en

echo "Migration VirusFileFound completed"
