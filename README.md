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
Java 1.8, Jersey, Maven v4, MySQL v5.1 or above


## Project Structure
* 📂 **/src/main/java/in/eko/service/**
  * 📁 **model/**
    * Contains POJO/Business Objects
  * 📁 **service/**
    * Contains service provider integration business logic
  * 📁 **persistence/**
    * Contains database query language
  * 📁 **requestView/**
    * Contains generic parameters used in request
  * 📁 **responseView/**
    * Contains generic parameters used in response
  * 📁 **factory/**
    * Contains generic methods used in service
  * 📁 **resources/**
    * Contains incoming API endpoints
  * 📁 **exception/**
    * Contains custom exceptions
  * 📁 **tasks/**
    * Contains schedular/batchjobs
  * 📁 **util/**
    * Contains constants and helper methods
  * 📁 **hibernate/**
    * Contains hibernate connection management
  * 📁 **schema/**
    * Contains database schema

## DB Schema Path
/src/main/java/in/eko/service/schema


## Project Configuration
* DB configuration
* POM configuration
