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
  * :file_folder: [**model/**](/src/main/java/in/eko/service/model/)  `POJO/Business Objects`
  * :file_folder: [**service/**](/src/main/java/in/eko/service/service/)  `Service provider integration business logic`
  * :file_folder: [**persistence/**](/src/main/java/in/eko/service/persistence/)  `Database Query Language`
  * :file_folder: [**requestView/**](/src/main/java/in/eko/service/requestView/)  `Parameters used in request`
  * :file_folder: [**responseView/**](/src/main/java/in/eko/service/responseView/)  `Parameters used in response`
  * :file_folder: [**factory/**](/src/main/java/in/eko/service/factory/)  `Factory design patterns, methods, objects, etc.`
  * :file_folder: [**resources/**](/src/main/java/in/eko/service/resources/)  `API endpoints`
  * :file_folder: [**exception/**](/src/main/java/in/eko/service/exception/)  `Custom exceptions`
  * :file_folder: [**tasks/**](/src/main/java/in/eko/service/tasks/)  `Batch jobs definition and its configurations`
  * :file_folder: [**util/**](/src/main/java/in/eko/service/util/)  `Constants and helper methods`
  * :file_folder: [**hibernate/**](/src/main/java/in/eko/service/hibernate/)  `Application connection management`
  * :file_folder: [**schema/**](/src/main/java/in/eko/service/schema/)  `Data Definition Language`

## Database Schema Path
/src/main/java/in/eko/service/schema/

## How to setup project

* Install Java 8 
  * https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html
* Database Setup
  * Go to path - /src/main/java/in/eko/service/schema/
  * Login to MySQL using console and execute below command <br \> ```source <schema file name>```
