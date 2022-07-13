#!/bin/bash

echo ""
echo "Applying migration ContactUs"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /contactUs                       controllers.ContactUsController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "contactUs.title = contactUs" >> ../conf/messages.en
echo "contactUs.heading = contactUs" >> ../conf/messages.en

echo "Migration ContactUs completed"
