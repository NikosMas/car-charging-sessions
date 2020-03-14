# Car-Charging-Sessions

## Thread-safe application written in Java 11 which represents a store for car charging session entities. It will hold all records in memory and provide REST API. Each entity of the store represents unique charging session that can be in progress or finished.


## It is structured with 3 layers: Controller - Service - Repository.
    
  1-Controller --> exports the following REST Endpoints:

        Add new car charging session
            POST /chargingSessions HTTP/1.1
            Host: localhost:8080
            Content-Type: application/json
            Request-Body:
            {
                "stationId": "exampleA"
            }

        Stop an existing car charging session
            PUT /chargingSessions/{example-UUID} HTTP/1.1
            Host: localhost:8080

        Fetch all the car charging sessions
            GET /chargingSessions HTTP/1.1
            Host: localhost:8080

        Fetch a summary of submitted charging sessions
            GET /chargingSessions/summary HTTP/1.1
            Host: localhost:8080

   2-Service --> Implement the business code to deliver the required result to Controller

   3-Repository --> CRUD operations on in-memory data structure
    
    
### To run the server please navigate to project solution's folder and run via cmd the following command: 'mvn spring-boot:run'.   Server will start using embedded Tomcat on your localhost:8080. Important! First run 'mvn clean install' to build the project and run the tests.
