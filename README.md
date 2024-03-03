# Template

Template project.

## Overview

This project is a web application that implements CRUD (Create, Read, Update, Delete) & spring security functionality for a bulletin board. It utilizes AWS Elastic Cache with Redis as the database.

## Tech Stack

- Java 17
- Kotlin 1.9.22
- Spring WebFlux - used for building asynchronous project
- R2DBC (AWS RDS Maria DB)
- AWS Elastic Cache (Redis) - caching
- AWS EC2
- AWS S3
- Docker

## TODO
- AWS Elastic Container Registr (ECR)
- AWS Elastic Container Service (ECS)
- Coroutine
  
## CI/CD

CI/CD is set up using GitHub Actions for deploying to Docker and EC2. It is configured to automatically deploy when pushing to Tag(V*).

## Contributing

If you would like to contribute to this project, feel free to Fork, make improvements, and submit a Pull Request.

## License

This project is licensed under the MIT License. See the `LICENSE` file for more information.
