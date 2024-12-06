# Documentation

This repository contains a spring boot application called accreditation-service.

Some patterns of note are:

* **RESTful** design pattern to align with HTTP standard.
* **Layered Architecture** separating code concerns into their own distinct layers, simplifying maintenance and testing
* **Dependancy Injection** for dependency management which reduces tight coupling within the code
* **DTO Pattern (Data Transfer Object)** which is used to transfer data between different layers and ensure the domain model is not
  exposed to external clients.

* **Global Exception Handler** to manage exceptions throughout the application, ensuring consistent error responses.
* **Bean Validation** to validate input DTO's

### Testing
Unit and Integration tests have been provided.

### Booting the Application

The script **run.sh** has been provided to run the application.

To run this script first make it executable by running **'chmod +x run.sh'**

And then **'./run.sh'**

## Audit log

An Audit Log would track the changes of accreditation statuses. 

Some new components we could add to the application would include:

* an UpdateHistory model class which would contain a unique **accreditation_history_id**
along with fields like **accreditationId**, **oldStatus**, **newStatus**, **timestamp** and **updatedBy**

* functionally this action would be taken in the service layer when accreditations
are processed (e.g. **finalizeAccreditation**, **createAccreditation**)

* We would need a new `/GET` endpoint to retrieve the historical data, 
passing the **accreditationId** to the endpoint to get a historical view of the 
updates assoiated with that accreditation



## Handling High Traffic

Some solutions for handling and scaling for high traffic:

* Separate out the audit functionality to a message queue such as RabbitMQ
  or kafka so that those updates happen asynchronously and don't contribute
  to computational load
* Use a caching solution like Redis for frequently requested data
* We can use indexing for frequently queried columns
* Implement pagination to reduce the number of records returned per request
* Implement Horizontal Scaling using a load balancer to distribute the traffic over 
multiple servers, and use auto-scaling to increase resources on-demand


