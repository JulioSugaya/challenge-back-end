# Ame Digital Challenge - SwPlanetsAPIAme

This is the solution for the backend dev challenge. This solution is an implementation of a 'Star Wars Planets' manager which performs actions through endpoints using Spring Webflux framework.

## Prerequisites

- Java jdk 1.8
- Spring Webflux
- Postman
- Apache Maven
- DynamoDB

## Requirements

### Run DynamoDB at local
Download from [AWS Dynamo DB Local](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html)
```
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb
```

In the DynamoDB console at : `localhost:8000/shell/`

Execute:
```
AWS.config.update({
  region: "eu-west-1",
  endpoint: 'http://localhost:8000',
  accessKeyId: "sugaya",
  secretAccessKey: "mysecret"
});

var dynamodb = new AWS.DynamoDB();
    var params = {
        TableName : "Planet",
        KeySchema: [
            { AttributeName: "Id", KeyType: "HASH"}
        ],
        AttributeDefinitions: [
            { AttributeName: "Id", AttributeType: "S" },
            { AttributeName: "Name", AttributeType: "S" }
        ],
        ProvisionedThroughput: {
            ReadCapacityUnits: 5,
            WriteCapacityUnits: 5
        },
        GlobalSecondaryIndexes: [{
          IndexName: "PlanetIndex",
          KeySchema: [
              {
                  AttributeName: "Name",
                  KeyType: "HASH"
                  }
          ],
          Projection: {
              ProjectionType: "ALL"
              },
          ProvisionedThroughput: {
              ReadCapacityUnits: 5,
              WriteCapacityUnits: 5
              }
          }]
    };
    dynamodb.createTable(params, function(err, data) {
        if (err) {
            ppJson(err);
        } else {
            ppJson(data);
        }
    });

var dynamodb2 = new AWS.DynamoDB();
    var params = {
        TableName : "PlanetTest",
        KeySchema: [
            { AttributeName: "Id", KeyType: "HASH"}
        ],
        AttributeDefinitions: [
            { AttributeName: "Id", AttributeType: "S" },
            { AttributeName: "Name", AttributeType: "S" }
        ],
        ProvisionedThroughput: {
            ReadCapacityUnits: 5,
            WriteCapacityUnits: 5
        },
        GlobalSecondaryIndexes: [{
          IndexName: "PlanetIndex",
          KeySchema: [
              {
                  AttributeName: "Name",
                  KeyType: "HASH"
                  }
          ],
          Projection: {
              ProjectionType: "ALL"
              },
          ProvisionedThroughput: {
              ReadCapacityUnits: 5,
              WriteCapacityUnits: 5
              }
          }]
    };
    dynamodb2.createTable(params, function(err, data) {
        if (err) {
            ppJson(err);
        } else {
            ppJson(data);
        }
    });
```


Run the app `mvn spring-boot:run`


The server will start at <http://localhost:8080>.

## Exploring the Rest APIs

The application defines following REST APIs

```
1. GET /planets - Get All Planets

2. GET /planets?search={name} - Get Planets by name (case sensitive)

3. POST /planets - Create a new Planet

4. GET /planets/{id} - Retrieve a Planet by Id

5. DELETE /planets/{id} - Remove a Planet

6. GET /planets/swapi/all - Retrieve all planets from Star Wars API (https://swapi.co/)

```

## Running the tests

There are Unit tests to the handler And an integration test which perform an end to end validation.

For run the tests, go to the project root in a command shell and execute: 
```
mvn clean test
```

## Built With

* [Spring Webflux](http://www.spring.io) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [AWS Dynamo DB](https://aws.amazon.com/dynamodb/?sc_channel=PS&sc_campaign=acquisition_BR&sc_publisher=google&sc_medium=english_dynamodb_b&sc_content=dynamodb_e&sc_detail=dynamodb%20aws&sc_category=dynamodb&sc_segment=89108950348&sc_matchtype=e&sc_country=BR&s_kwcid=AL!4422!3!89108950348!e!!g!!dynamodb%20aws&ef_id=Cj0KCQjwrfvsBRD7ARIsAKuDvMNMZECQDk-IgWpRVNdjaEIu23PdbKVMIC72qoP1zd8OEath7eTL1ccaApaUEALw_wcB:G:s) - NoSql Database

## Authors

* **Júlio Sugaya** - (https://github.com/JulioSugaya)
