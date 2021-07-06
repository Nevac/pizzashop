# Pizzashop
### Example project by Cagatay Özyurt


I present my solution for the pizzashop assignment given by ti&m.

### Implemented Mandatory Tasks

- Spring Boot as technology
- Fully REST API
- CRUD Pizzas
- CRUD Orders
- Maven for dependency management and building

### Implemented Additional Tasks

- OpenAPI Contract
- Exchangable Datasource
- Unit Tests for Service layer
- Unit Tests for Specifications (For flexible querying)
- Integration Tests for Controller layer
- Flexible querying for pizzas and orders

### Setting up the project
##### Building
After cloning the project to a local repository, perform `mvn package` in the base directory to build the project and generate a .war file

```sh
mvn package
```

A file named pizzashop.war is generated and can be found in `{baseDir}/target`.

##### Creating the database
The project was developed to run with a PostgreSQL database. In order to run the application properly, a database must be created.
Check out the link https://www.postgresqltutorial.com/install-postgresql/ to learn how to setup a PostgreSQL database. The name of the database will later be important to connect it to the application.


##### Deploying to tomcat
To deploy the application to a tomcat server, the pizzashop.war file can be moved to `{tomcat_baseDir}/webapps`.

Some environment variables need to be set in order to run the application. These can be done in the setenv.bat (Windows) or setenv.sh (Linux) files located in `{tomcat_baseDir}/bin`.

setenv.bat (Windows)
```sh
set PORT={port}
set DB_URL={address}/{database_name}
set DB_USER={user}
set DB_PASSWORD={password}
```

setenv.sh (Linux)
```sh
export PORT={port}
export DB_URL={address}/{database_name}
export DB_USER={user}
export DB_PASSWORD={password}
```

Depending on your systems configuration, you might also want to set the `JAVA_HOME` variable.

If the environment variables have been set properly, you should be able to run the tomcat server. The routes for the pizzashop application are available under `http://{hosting_address}/pizzashop`. 


### Dependencies

Following dependencies are used by the application

| Plugin  | Description
| ------ | ------ |
| Spring Boot Starter Data JPA | Spring JPA Repositories for abstract persistence interface|
| Spring Boot Starter Web | Spring Web extension |
| Spring Boot Starter Tomcat | Spring tomcat extension  |
| Spring Boot Starter Test | Adds all needed test depndencies (JUnit, Mockito, Hamcrest) |
| Spring Boot Starter Validation | Easy calidation, used for validate dtos in requests |
| JUnit Vintage | Used to run JUnit 4 tests |
| [MapStruct](https://github.com/mapstruct/mapstruct) | Easy mapping between java bean classes, especially models and dtos |
| [Lombok](https://github.com/projectlombok/lombok) | Annotiations to generate boilerplate code (for example getter and setter) |
| [PostgreSQL](https://github.com/pgjdbc/pgjdbc) | PostgreSQL JDBC driver |
| [h2database](https://github.com/h2database/h2database) | In memory h2 database driver, primarily used for tests |
| [RESTassured](https://github.com/rest-assured/rest-assured) | Easy testing of REST services, used for integration tests  |

##### Why did I use JUnit 4 instead of 5?
The workplace I worked previously did not migrate to JUnit 5 yet. In projects I was involved, tests were still written with JUnit 4. I would need to study the differences between them in order to write propper tests with JUnit 5. At some point I will do that, but I did not want to work on this project for too long.

### Working process
1. Defining of the OpenAPI specification. In a bigger project this would have served as a contract between this backend and dependent systems.
2. Designing of a database schema to see the relation between models.
3. Initializing the spring project
4. Implementing of the persistence layer (Repositories, setting up postgreSQL DB)
5. Designing of the service interface
6. Implementing the service layer
7. Unit testing the service layer
8. Implementing the controller layer
9. Implementing advice for error handling and returning correct statuscode
10. Implementing specifications (for flexible queries)
11. Unit testing specifications
12. Integration testing through all of the layers (Controller -> Service -> Persistence)
13. Polishing

Documents regarding OpenAPI and database schema can be found under  `src/main/resources/documents`

### General Note to commenting code
I do not know what the customs in ti&m are for commenting code. The principle I follow is generally that if a code is very simple and explains itself, no additional commenting is necessary. In cases where it is not very clear, I use comments.


### Note to additional tasks

##### Authentication
I did not implement an authentication, if I were to do one I would have set up another application as auth server (for example keycloak). This way the Oauth 2.0 process could be realized.

##### CRUD on all resources
Pizzas and orders have full CRUD capabilities, except a DELETE request on a pizza changes its active flag.

##### Active flag on pizzas
I extended the pizza model with an active flag. The reasoning behind it was, that maybe in future some pizzas will not be served anymore, but older orders should be kept for statistical purpose. 

Deleting pizzas from the database would either mean that dependend orders also need to be removed (cascading) or orders would have null values in the pizza column (in a relational database at least), which would not be helpful in generating statistics.

With an active flag, these problems do not persist. When a DELETE request is made on any pizza, the active flag will be set to false. The flag can also be manipulated with a PUT request, eanbling or disablbing the pizza.

When an authentication is in place, there could be an option for and admin (superuser) to completely delete pizzas and corresponding orders.

##### Flexible Query
Pizzas and orders support flexible querying. The search query parameter can query most fields of the models. Following some examples:

Get Pizzas with name Margharita
`/pizza?search=name:Margharita`

Get Pizzas with price greater than 12
`/pizza?search=price>12`

Get Pizzas with name Padrone AND is active
`/pizza?search=name:padrone,active:true`

Get Pizzas with name Padrone OR (`'` prefix) price smaller than 15
`/pizza?search=name:padrone,'price<15`

Supported operations
| Operation | Description |
| ----- | ----- |
| `:` | equals |
| `!` | not equals |
| `>` | bigger than |
| `<` | smaller than |

The pizza list in orders cannot be queried. The provided solution could be extended with an "includes" operator.

