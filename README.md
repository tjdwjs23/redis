# Template

Template project.

## Overview

This project is a web application that implements CRUD (Create, Read, Update, Delete) and Spring Security functionalities for a bulletin board system. It leverages AWS Elastic Cache with Redis for its database operations.

## Technology Stack
- Java 17
- Kotlin 1.9.22
- Spring WebFlux - used for building asynchronous project
- R2DBC (AWS RDS Maria DB)
- AWS EC2
- AWS Elastic Cache (Redis)
- AWS Elastic Container Registr (ECR)
- Docker

## TODO
- AWS Elastic Container Service (ECS)
- Coroutine
  
## CI/CD

The CI/CD is set up using GitHub Actions and is configured to automatically deploy to Docker and EC2 when a push to the Tag(V*) is made.

Notably, the CI/CD process includes building a JAR file, which is then transformed into a Docker image. This Docker image is deployed to AWS's ECR (Elastic Container Registry), and the service is subsequently deployed on EC2 based on this Docker image. All these steps are automated through GitHub Actions and Action Runner.

## Contributing

If you would like to contribute to this project, feel free to Fork, make improvements, and submit a Pull Request.

## License

This project is licensed under the MIT License. See the `LICENSE` file for more information.
