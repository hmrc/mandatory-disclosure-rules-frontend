#!/bin/bash

echo ""
echo "Applying migration NotXMLFile"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /notXMLFile                       controllers.NotXMLFileController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "notXMLFile.title = notXMLFile" >> ../conf/messages.en
echo "notXMLFile.heading = notXMLFile" >> ../conf/messages.en

echo "Migration NotXMLFile completed"
