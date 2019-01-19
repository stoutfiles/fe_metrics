# fe_metrics

## Notes

This example project currently works as is with all functional requirements supported, but it is complete.  

Had I kept going, I would have:

-Finished unit tests.  There is currently just one test, a similar approach would have been used for the other tests.

-Used a different profile for unit tests and had them run against a different database.

-Put MySQL and the application into Docker images.

-Not granted test_user all privileges, but rather CRUD privileges to the databases of this project.

-Documented out Big O complexity of each call.


## Setup

### Run the following commands against MySQL.

CREATE USER 'test_user'@'%' identified by 'test_pass';

GRANT ALL PRIVILEGES ON \*.\* TO 'test_user'@'%' WITH GRANT OPTION;

CREATE DATABASE TEST;

USE TEST;

CREATE TABLE metric (id VARCHAR(40), metric_name VARCHAR(255), metric_type VARCHAR(10), created_date DATETIME, updated_date DATETIME);

CREATE TABLE metric_data (id int NOT NULL AUTO_INCREMENT, metric_id VARCHAR(40), value DECIMAL(10,4), metric_type VARCHAR(10), timestamp DATETIME, PRIMARY KEY (id));

CREATE INDEX faster_math on metric_data (metric_id, value);

### Run application as Spring Boot App

Once running, API can be used from Swagger.  [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
