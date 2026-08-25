
# disa-account-stubs

This is the stub API for use with the DISA account services.

### Before running the app

#### MongoDB

This repository relies on having mongodb running locally. You can start it with:

```bash
# first check to see if mongo is already running
docker ps | grep mongodb

# if not, start it
docker run --restart unless-stopped --name mongodb -p 27017:27017 -d percona/percona-server-mongodb:7.0 --replSet rs0
```

Reference instructions for [setting up docker](https://docs.tax.service.gov.uk/mdtp-handbook/documentation/developer-set-up/install-docker.html) and [running mongodb](https://docs.tax.service.gov.uk/mdtp-handbook/documentation/developer-set-up/set-up-mongodb.html#install-mongodb-applesilicon-mac).

```bash
sm2 --start DISA_ACCOUNT_ALL
```

### Running the app locally

```bash
sbt run
```

The service runs on port `12106` by default.

```bash
# other useful commands
sbt clean

sbt reload

sbt compile
```

### Running the test suite

To run the unit tests:

```bash
sbt test
```

To run the integration tests:

```bash
sbt it/test
```

### Before you commit

This service leverages scalaFmt to ensure that the code is formatted correctly.

Before you commit, please run the following commands to check that the code is formatted correctly:

```bash
# checks all source and sbt files are correctly formatted
sbt prePrChecks

# if checks fail, you can format with the following commands

# formats all source files
sbt scalafmtAll

# formats all sbt files
sbt scalafmtSbt

# formats just the main source files (excludes test and configuration files)
sbt scalafmt
```
### POST /email-verification/v2/send-code

Simulates sending an email verification code.

The response is driven by the email field in the request body.

| Scenario          | `email`                  | Response                                                                                   | HTTP Status                 | Description                       |
| ----------------- | ------------------------ | ------------------------------------------------------------------------------------------ | --------------------------- | --------------------------------- |
| Code not sent     | `code-not-sent@test.com` | `{ "status": "CODE_NOT_SENT" }`                                                            | `400 Bad Request`           | Simulates failure to send code    |
| Internal error    | `server-error@test.com`  | No body                                                                                    | `500 Internal Server Error` | Simulates upstream/system failure |
| Success (default) | any other value          | `{ "status": "CODE_SENT", "message": "Email containing verification code has been sent" }` | `200 OK`                    | Successful send                   |

### POST /email-verification/v2/verify-code

Simulates verification of an email verification code.

The response is driven by the verificationCode field in the request body.

| Scenario        | `verificationCode` | Response                                                                                                | HTTP Status                 | Description                       |
| --------------- | ------------------ | ------------------------------------------------------------------------------------------------------- | --------------------------- | --------------------------------- |
| Verified        | `ABCDEF`           | `{ "status": "CODE_VERIFIED", "message": "The verification code for the email verified successfully" }` | `200 OK`                    | Successful verification           |
| Not validated   | `NOTVAL`           | `{ "status": "CODE_NOT_VALIDATED" }`                                                                    | `400 Bad Request`           | Code failed validation            |
| Not found       | `NOTFND`           | `{ "status": "CODE_NOT_FOUND", "message": "Verification code not found" }`                              | `404 Not Found`             | Code not found or expired         |
| Internal error  | `SERERR`           | No body                                                                                                 | `500 Internal Server Error` | Simulates upstream/system failure |
| Default failure | any other value    | `{ "status": "CODE_NOT_VALIDATED", "message": "Invalid verification code" }`                            | `400 Bad Request`           | Unknown/invalid code              |


### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").