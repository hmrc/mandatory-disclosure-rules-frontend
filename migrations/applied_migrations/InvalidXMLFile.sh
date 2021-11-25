#!/bin/bash

echo ""
echo "Applying migration InvalidXMLFile"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /invalidXMLFile                       controllers.InvalidXMLFileController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "invalidXMLFile.title = invalidXMLFile" >> ../conf/messages.en
echo "invalidXMLFile.heading = invalidXMLFile" >> ../conf/messages.en

echo "Migration InvalidXMLFile completed"
