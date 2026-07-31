# Project Title

Book store application

## Description

This application is a backend implementation of an e-commerce bookstore, designed with a focus on security and clean architecture.

# Features:
* Authenticaton & Authorization: The app uses tokens (JWT) to secure endpoints. It is designed with two roles: 'ADMIN' and 'USER'. Each user has their own shopping cart and is able to make an order.
* Admin & User capabilities:
    * Admin can add, update, and manage books and categories.
    * User can view books, search by category, add items to the cart, and place an order.
* Validation: The application checks input data (for example, correct email format or non-empty fields) to prevent bad requests.
* Global Exception Handling: Errors are handled globally and return clear JSON messages.
* Testing: The project includes unit tests for controllers, services and repositories.

### Dependencies

* Java 22
* Maven 3.8+
* MySql Database
* Spring Boot
* Spring Security
* JWT
* Spring Data JPA
* Hibernate
* Liquibase
* MapStruct
* Swagger / OpenAPI
* Docker
* JUnit 5
* Mockito
* O: Windows, macOS, Linux

### Installing

1. Clone the repository:
```bash
git clone [https://github.com/PatrykWski/book-store-app.git](https://github.com/PatrykWski/book-store-app.git)
cd book-store-app/book-store
```

2. Configure your database in: src/main/resources/application.properties
```bash
spring.datasource.url=jdbc:mysql://localhost:3306/book_store_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

### Executing program

* Run the application via Maven:
```
mvn clean spring-boot:run
```
* Open Swagger UI to test the API:
```
http://localhost:8080/swagger-ui/index.html
```
## Help

Common problems or issues:
403 Forbidden in tests or requests: Make sure you are passing a valid JWT token or configured security context in your requests. In @WebMvcTest, handling Spring Security stateless filters can be tricky—ensure your authentication tokens are properly mocked.

Database connection error: Verify that your MySQL server is running and that the username and password in application.properties match your local setup.

## Authors

* @PatrykWski

## Version History

* 0.1
    * Initial Release - Added User Authentication, Book Catalog, and Shopping Cart features with full testing suite.

## License

This project is licensed under the MIT License.

## Acknowledgments

Inspiration, code snippets, etc.
* [awesome-readme](https://github.com/matiassingers/awesome-readme)
* [PurpleBooth](https://gist.github.com/PurpleBooth/109311bb0361f32d87a2)
