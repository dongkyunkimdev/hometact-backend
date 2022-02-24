# [홈택트](https://www.hometact.ml/)[![Build Status](https://app.travis-ci.com/dongkyunkimdev/hometact-backend.svg?branch=master)](https://app.travis-ci.com/dongkyunkimdev/hometact-backend)
## 1. 개요
비대면 모임 커뮤니티 '홈택트' Back-end

## 2. 프로젝트 환경
- IDE : IntelliJ
- Framework : Spring Boot 3.0.0
- ORM : Spring Data JPA 2.6.3
- CI/CD : Travis CI, AWS S3, CodeDeploy
- Auth : JWT 0.11.2
- DB : MariaDB 10.5.13
- Server : AWS EC2 Amazon Linux

## 3. 디렉토리 구조
    ├── src
    │   ├── main
    |   |   ├── java
    |   |   |   └── kdk.hometact
    |   |   |       ├── comment
    |   |   |       ├── error
    |   |   |       |   └── exception
    |   |   |       ├── post
    |   |   |       ├── postcategory
    |   |   |       ├── postlike
    |   |   |       ├── security
    |   |   |       |   └── jwt
    |   |   |       ├── swagger
    |   |   |       └── user
    |   |   |           └── auth
    |   |   └── resources
    |   |       ├── application.yml
    |   |       ├── application-local.yml
    |   |       ├── application-prod.yml
    │   └── test
    |       └── java
    |           └── kdk.hometact
    |               ├── comment
    |               ├── post
    |               ├── postlike
    |               ├── security
    |               └── user
    ├── .travis.yml
    ├── appspec.yml
    ├── build.gradle
    └── README.md

## 4. Wiki
[Wiki](https://github.com/dongkyunkimdev/hometact-backend/wiki)

## 5. API 명세
[API 명세서](https://app.swaggerhub.com/apis-docs/dongkyunkimdev/Hometact-API/1.0.0)
