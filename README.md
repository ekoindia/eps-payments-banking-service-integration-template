# EPS: Eko Platform Services
## Payments Layer - Banking Service Integration - Template
Internal template to integrate banking services into the EPS Payments layer.

[![GitHub issues](https://img.shields.io/github/issues/ekoindia/aeps-gateway-lib)](https://github.com/ekoindia/aeps-gateway-lib/issues)  <a href="https://eko.in" target="_blank">![Eko.in](https://img.shields.io/badge/Develop%20with-Eko.in-brightgreen)</a>
<a href="https://twitter.com/intent/tweet?text=Wow:&url=https%3A%2F%2Fgithub.com%2Fekoindia%2Faeps-gateway-lib" target="_blank"><img alt="Twitter" src="https://img.shields.io/twitter/url?style=social&url=https%3A%2F%2Fgithub.com%2Fekoindia%2Faeps-gateway-lib"></a>
<a href="https://twitter.com/intent/follow?screen_name=ekospeaks" target="_blank">![Twitter Follow](https://img.shields.io/twitter/follow/ekospeaks?label=Follow&style=social)</a>

---

## Introduction
_WIP_


## Technology Stack
![Java 1.8](https://img.shields.io/badge/Java-1.8-blue)  ![Jersey](https://img.shields.io/badge/Jersey-2.22-green)  ![Maven 4](https://img.shields.io/badge/Maven-4-blueviolet)  ![MySQL 5.1+](https://img.shields.io/badge/MySQL-5.1+-yellowgreen)


## Project Structure
* :open_file_folder: [**/src/main/java/in/eko/service/**](/src/main/java/in/eko/service/)
  * :file_folder: [**model/**](/src/main/java/in/eko/service/model/)  `contains POJO/business objects.`
  * :file_folder: [**service/**](/src/main/java/in/eko/service/service/)  contains service provider integration business logic.
  * :file_folder: [**persistence/**](/src/main/java/in/eko/service/persistence/)  contains database query language.
  * :file_folder: [**requestView/**](/src/main/java/in/eko/service/requestView/)  contains generic parameter used in request.
  * :file_folder: [**responseView/**](/src/main/java/in/eko/service/responseView/)  contains generic parameters used in response.
  * :file_folder: [**factory/**](/src/main/java/in/eko/service/factory/)  contains generic methods used in the service.
  * :file_folder: [**resources/**](/src/main/java/in/eko/service/resources/)  contains incoming API endpoints.
  * :file_folder: [**exception/**](/src/main/java/in/eko/service/exception/)  contains custom exceptions.
  * :file_folder: [**tasks/**](/src/main/java/in/eko/service/tasks/)  contains schedular/batch jobs.
  * :file_folder: [**util/**](/src/main/java/in/eko/service/util/)  contains constants and helper methods.
  * :file_folder: [**hibernate/**](/src/main/java/in/eko/service/hibernate/)  contains hibernate connection management to the database.
  * :file_folder: [**schema/**](/src/main/java/in/eko/service/schema/)  contains database schema.

## DB Schema Path
/src/main/java/in/eko/service/schema


## Project Configuration
* DB configuration
* POM configuration
