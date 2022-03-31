#!/bin/bash

echo ""
echo "Applying migration FileProblem"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /fileProblem                       controllers.FileProblemController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "fileProblem.title = fileProblem" >> ../conf/messages.en
echo "fileProblem.heading = fileProblem" >> ../conf/messages.en

echo "Migration FileProblem completed"
