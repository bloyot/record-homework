# Overview
This is a Java project, using the Spring Boot framework, which consists of three modules:
- `record-homework-api` this is the restful api, which exposes endpoints to create and retrieve records 
- `record-homework-cli` this is a cli tool which allows joining three input files in different formats, and sorting the output. 
- `record-homework-common` this is a library for shared code between the api and cli, primarily the parsing logic, and the core `Record` object

# Build
To build the project, from the root directory run:
```shell script
./mvnw clean install
```

This will run the tests and build the project, storing the built artifacts in your local m2 repository. 

# Sample data
Some sample data for testing is provided in `record-homework-cli/src/test/resources`. These files are also used for the junit tests. 

# Run
## Cli
The command line interface requires 4 arguments:
* `--sort-type` how to sort the data, currently one of three options, `gender` which sorts by gender (female -> male) then last name ascending, `birth_date` which sorts by birth date ascending, and `last_name` which sorts by last name descending
* `--input-csv` the fully qualified path to the input comma separated value file
* `--input-psv` the fully qualified path to the input pipe separated value file
* `--input-ssv` the fully qualified path to the input space separated value file
 
To run with the sample files, run the following from the root directory (replacing <root path> with the path to your project) 
```shell script
java -jar record-homework-cli/target/record-homework-cli-1.0-SNAPSHOT.jar \
--sort-type=last_name \
--input-csv=<root-path>/record-homework/record-homework-cli/src/test/resources/records.csv \
--input-ssv=<root-path>/record-homework/record-homework-cli/src/test/resources/records.ssv \
--input-psv=<root-path>/record-homework/record-homework-cli/src/test/resources/records.psv 
```
 
## Api
To start the api, simply run:
```shell script
java -jar record-homework-cli/target/record-homework-api-1.0-SNAPSHOT.jar
```

This starts the api using port `8080`, and exposes the following endpoints:
* `POST /records` - This method must have a request body which has the following json structure: 
```
{
	"delimiter": ",", 
	"data": "Aadil,Knight,female,red,04/03/2003"
}
```
The data is the actual record (same as in the files processed by the command line), and the delimiter field specifies the delimiter to be used when parsing. Records added are stored only in memory in the api.
  
* `GET /records/name` - returns an array of records, sorted by last name ascending. You can provide `sortOrder=desc` as a query param to sort in the opposite order. 
* `GET /records/birthdate` - returns an array of records, sorted by birthdate ascending. You can provide `sortOrder=desc` as a query param to sort in the opposite order. 
* `GET /records/gender` - returns an array of records, sorted by gender (male -> female). You can provide `sortOrder=desc` as a query param to sort in the opposite order. 

Sample request/response:
* Request `curl localhost:8080/records/birthdate`
* Response
```
[
    {
        "lastName": "Aadil",
        "firstName": "Knight",
        "gender": "FEMALE",
        "favoriteColor": "red",
        "dateOfBirth": "2003-04-03"
    },
    {
        "lastName": "Mohamad",
        "firstName": "Reid",
        "gender": "MALE",
        "favoriteColor": "black",
        "dateOfBirth": "2031-08-02"
    },
    {
        "lastName": "Katelyn",
        "firstName": "Paterson",
        "gender": "FEMALE",
        "favoriteColor": "blue",
        "dateOfBirth": "2075-06-03"
    }
]
```
  
# Tests
The whole test suite can be run from the root directory with `./mvnw test`

## Coverage
![Test Coverage Image](TestCoverage.PNG)

A note on coverage: The vast majority of the logic is in the api and the common modules. The cli modules contains mostly just a wrapper and some cli option parsing. 
The coverage for the api and common modules are at 80% and 76% respectively, and nearly all the uncovered code is getters/setters.
The coverage for the cli application is only 10%, but contains virtually no logic that would be a good fit for testing. Although the overall test coverage (by line) is only 50%,
I feel quite confident that the actual logic is tested thoroughly, and most coverage gaps are boilerplate (which is quite common in java). 