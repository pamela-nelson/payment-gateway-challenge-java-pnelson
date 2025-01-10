# Payment Gateway Coding Challenge

## Table of Contents

- [Candidate Details](#candidate-details)
- [Features](#features)
- [Notes](#notes)
- [Assumptions](#assumptions)
- [Future Improvements](#future-improvements)
- [Getting Started](#getting-started)
  - [Configuring the Application](#configuring-the-application)
  - [Testing the Application](#testing-the-application)

## Candidate Details

**Name:** Pamela Nelson

**LinkedIn:** [/in/PamelaJayneNelson](https://www.linkedin.com/in/pamelajaynenelson/)

## Features
* Logging of all requests/responses with linked correlationId


* Validations of request payloads using Spring validation


* Card numbers are luhn checked before being sent to acquiring bank to reduce calls for known invalid cards


* Postman Collection to make requests including sample responses

## Notes
The Declined scenario card fails Luhn check so cannot be sent to the simulator.
In the interest of completeness of the coded solution I have corrected the check digit to the following.

original: 2222405343248112
new: 2222405343248117

This is the only change on the simulator seen as a bug fix.

If this is not allowed the Luhn check validation can be disabled by commenting out the
@LuhnCheck annotation on the cardNumber field in PostPaymentRequest.

### Assumptions

* Rejected Payments should not be stored in the payment db.


### Future Improvements

* Adding Integration Tests


* Asking the merchant to perform MLE encryption on the cardNumber and cvv fields for extra security


* Adding user authentication via access token to ensure only authorised users can view payments that 
have been made by them.


* Adding a fallback retry mechanism for recoverable issues such as 500 from the acquiring bank


* Adding an actual database to store the payment info.


* Adding a Health check endpoint to check health status of dependencies.
eg. Database and acquiring bank



## Getting Started

### Configuring the Application

There is an application.properties file including the following settings.

* acquiringBank.client.baseUrl - url to point to acquiring bank simulator


### Testing the Application

I have included unit tests to test various scenarios both happy path and negative. I've made use of parameterised tests and created a mock client for the simulator.

For time reasons I haven't included automated integration tests. 

For integration testing I have used a postman collection which is included in the postman folder. It shows example responses and can be used to send requests to the service.


