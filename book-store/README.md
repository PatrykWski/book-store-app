# Project Title

Book store application

## Description

Secure REST API backend build for an online bookstore. I made it to handle things like user registration, managing a shopping cart and browsing books.

## Getting Started

### Dependencies

* Java 22
* Maven 3.8+
* MySql Database
* Os: Windows, macOS, Linux

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
