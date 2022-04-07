# Mandatory Disclosure Rules Frontend

This service provides a UI that allows registered users to submit their cross border arrangement to HMRC.

### Overview:

Only users with the correct credentials will be redirected to this service. Users can change their contact details within this service and check the status of previous submissions. Users will be notified if the submission has a problem or if the submission does not pass validation. 

This service interacts with [mandatory disclosure rules (backend)](https://github.com/hmrc/mandatory-disclosure-rules).

### API Calls:


| PATH | Supported Methods | Description |
|------|-------------------|-------------|
|```/mandatory-disclosure-rules/callback ``` | POST | Upscan callback |
|```/upscan/v2/initiate ``` | POST | Retrieve Upscan form data |
|```/mandatory-disclosure-rules/upscan/details/:uploadId``` | GET | Retrieves upscan session details containing UploadId, Reference & Status from backend |
|```/mandatory-disclosure-rules/upscan/status/:uploadId``` | GET | Retrieves only the upload Status from backend |
|```/mandatory-disclosure-rules/upscan/upload``` | POST | Request an upload |
|```/mandatory-disclosure-rules/validate-submission``` | POST | Passes Upscan URL to backend to perform XML Validation |
|```/mandatory-disclosure-rules/submit``` | POST | Submits Schema validated XML to backend |
|```/mandatory-disclosure-rules/subscription/read-subscription``` | POST | Retrieves Subscription Details (Contact Details) from backend |
|```/mandatory-disclosure-rules/subscription/update-subscription``` | POST | Updates Subscription Details (Contact Details) from backend |
|```/mandatory-disclosure-rules/files/:conversationId/details``` | GET | Retrieves a specific file from backend containing subscriptionID, messageRefID, file status, file name & timestamps  |
|```/mandatory-disclosure-rules/files/details``` | GET | Retrieves details of all submitted files from backend |
|```/mandatory-disclosure-rules/files/:conversationId/status``` | GET | Retrieves file status for a specific file from backend |


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
      
#### *Acceptance test repo*:  
[mandatory-disclosure-rules-file-upload-ui-tests](https://github.com/hmrc/mandatory-disclosure-rules-file-upload-ui-tests)
      
## Requirements

This service is written in [Scala](http://www.scala-lang.org/) and [Play](http://playframework.com/), and requires a Java 8 [JRE] to run.

[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
