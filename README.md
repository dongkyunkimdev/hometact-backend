# [홈택트](https://www.hometact.ml/)[![Build Status](https://app.travis-ci.com/dongkyunkimdev/hometact-backend.svg?branch=master)](https://app.travis-ci.com/dongkyunkimdev/hometact-backend)
## 개요
비대면 모임 빌딩 플랫폼 '홈택트' Back-end  
[Front-end 링크](https://github.com/dongkyunkimdev/hometact-frontend)

## 목표
비즈니스 요구사항을 충족하는 커뮤니티 Back-end API 개발

## 비즈니스 요구사항
- 이메일, 닉네임은 중복 불가능  
- 게시글 조회를 제외한 모든 기능은 인증된 사용자만 가능  
- 게시글/댓글 수정, 삭제는 해당 글을 작성한 사용자 혹은 관리자만 가능  
- 한 사용자는 한 게시글에 하나의 관심만 등록 가능  

## 개발 프로세스
- RDB 설계  
- Entity, DTO, VO 작성  
- 비즈니스 로직 작성  
- 단위 테스트 코드 작성 및 테스트  
- JWT 토큰 발급 및 검증 로직 작성  
- 요구사항에 맞는 인증, 인가 적용  
- API 문서 작성  
- AWS EC2 배포  
- Travis CI, AWS S3, CodeDeploy를 사용한 테스트/빌드/배포 자동화  

## 구현 내용
- Spring Security, Jwt 인증 구현
- API 개발 및 테스트 코드 작성
- 계층형 Exception 설계 및 Handler 구현
- AWS, Travis CI를 통한 CI/CD 구성

## 프로젝트 환경
- IDE : IntelliJ
- Framework : Spring Boot 3.0.0
- ORM : Spring Data JPA 2.6.3
- CI/CD : Travis CI, AWS S3, CodeDeploy
- Auth : JWT 0.11.2
- DB : MariaDB 10.5.13
- Server : AWS EC2 Amazon Linux

## 디렉토리 구조
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

## API 명세
[API 명세서](https://app.swaggerhub.com/apis-docs/dongkyunkimdev/Hometact-API/1.0.0)
