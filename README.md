Sure, here's a basic `README.md` for your project. You can modify it according to your needs.


# Loan Application

This is a Spring Boot application for managing loans. It provides APIs for requesting loans, taking action on loans, fetching loans, and fetching loan details.

## Running Directly

- I've included a jar file in the root directory. You can run it directly using the following command:
```bash
java -jar loan-0.0.1-SNAPSHOT.jar
```
- The application will start running at `http://localhost:8080`.
- Make sure test.db is in the same directory as the jar file.
- Java 11+ Required.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Java 11
- Maven
- SQLite

### Installing

1. Clone the repository
```bash
git clone https://github.com/imviplovemittal/loan.git
```

2. Navigate to the project directory
```bash
cd loan
```

3. Build the project
```bash
mvn clean install
```

4. Run the application
```bash
mvn spring-boot:run
```

The application will start running at `http://localhost:8080`.

## Running the tests

To run the tests, use the following command:

```bash
mvn test
```

## Swagger

The application uses Swagger to document the APIs. You can access the Swagger UI at `http://localhost:8080/swagger-ui.html`.

## Postman Collection

You can import the Postman collection from `AspireBETest.postman_collection.json` to test the APIs.

# Changes from Requirements

1. Scheduled payments are only generated for approved loans.
2. Scheduled payments remain pending till paid.
3. Options are provided for part payments and paying multiple installments at once.
4. Loans can also have interest. Interest is always paid first.
5. All loans summary is present in the account, but user can only pay for 1 loan at a time.
6. User's payable is divided in principal and interest.
7. User can pay for a loan only if the loan is approved.
8. Because of loan summary and feature to pay for multiple installments at once/part payments, the minimum installment amount is not enforced.

## Built With

- [Spring Boot](https://spring.io/projects/spring-boot) - The web framework used
- [Maven](https://maven.apache.org/) - Dependency Management
- [SQLite](https://www.sqlite.org/index.html) - Used as the database
- [Swagger](https://swagger.io/) - Used to document the APIs

## Authors

- [imviplovemittal](https://github.com/imviplovemittal)

