# Mandatory Disclosure Rules Frontend

This service provides a UI that allows registered users to submit their cross border arrangement to HMRC. Only users with the correct credentials will be redirected to this service. Users can change their contact details within this service and check the status of previous submissions.

Users will be notified if the submission has a problem or if the submission does not pass validation. 

### Information:
Read subscription endpoint: 

     /mandatory-disclosure-rules/subscription/read-subscription
Update subscription endpoint: 

     /mandatory-disclosure-rules/subscription/update-subscription
     
Submission endpoint: 

     /mandatory-disclosure-rules/submit

#### *API specs*: 

 - [MDR Read Subscription API
   spec](https://confluence.tools.tax.service.gov.uk/display/DAC6/MDR+Specs?preview=/388662598/434373869/AEOI-DCT70d-1.2-EISAPISpecification-MDRSubscriptionDisplay.pdf)
   
  - [MDR Update Subscription API
   Spec](https://confluence.tools.tax.service.gov.uk/display/DAC6/MDR+Specs?preview=/388662598/434373871/AEOI-DCT70e-1.2-EISAPISpecification-MDRSubscriptionAmend.pdf)

## Run Locally

Run the following command to start services locally:

    sm --start MDR_ALL -f

Mandatory Disclosure Rules runs on port 10018

#### *Auth login details*: 

      enrolmentKey = "HMRC-MDR-ORG"  
      identifier = "MDRID"  
      identifier value = "XAMDR0009234568"
      redirect url = "/report-under-mandatory-disclosure-rules"



## Requirements

This service is written in [Scala](http://www.scala-lang.org/) and [Play](http://playframework.com/), and requires a Java 8 [JRE] to run.

[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
