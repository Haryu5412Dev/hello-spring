# Spring Boot 회원 관리 프로젝트

## 1) 프로젝트 개요

본 프로젝트는 Spring Boot 기반의 기본 회원 관리 예제를 확장하여, 실사용에 가까운 사용자 계정 관리 기능을 구현한 프로젝트입니다.

기존 예제가 `name` 기반의 단순 회원 등록만 제공하던 한계를 보완하고, 실제 서비스에서 요구되는 `loginId`, `password`, `phoneNumber` 기반 기능 및 **직접 비밀번호 변경 기능**을 추가 구현했습니다.

## 2) 구현 기능

### 회원 정보 구조

- id
- 이름(name)
- 사용자 ID(loginId)
- 비밀번호(password)
- 전화번호(phoneNumber)

### 제공 기능

- 회원 등록 (중복 `loginId` 검사 포함)
- 회원 목록 조회
- 사용자 ID 찾기 (이름 + 전화번호 기반)
- 중복 사용자 ID 가입 불가 처리 (오류 페이지 표시)
- 비밀번호 재설정 (임시 비밀번호 발급)
- **새 비밀번호 직접 설정 기능 (추가 구현)**
    
    → 사용자가 입력한 새로운 비밀번호를 저장소에 영구 반영
    

## 3) 개발 환경

- Java 17
- Spring Boot
- Thymeleaf
- IntelliJ IDEA / VS Code

## 4) 실행 방법

### 1) Gradle로 실행

```powershell
./gradlew clean bootRun
```

### 2) JAR 패키징

```powershell
./gradlew clean build
```

### 3) 접속

브라우저에서 `http://localhost:8080` 접속

## 5) 기능 설명

- **홈 화면:** `templates/home.html`
    - 회원가입/회원목록으로 이동
- **로그인:** `templates/login.html`
    - 로그인 성공 시 `loginSuccess.html`
- **회원가입:**
    - `GET /members/new` → 회원가입 폼
    - 중복 `loginId` 시 `joinError.html`로 안내
- **아이디 찾기:**
    - `GET /find-login-id`, `POST /find-login-id`
- **비밀번호 재설정:**
    - `GET /reset-password`, `POST /reset-password`
    - 임시 비밀번호 발급 또는 **새 비밀번호 직접 설정 흐름** 지원

## 6) 핵심 코드 구조

본 프로젝트에서 핵심이 되는 부분은 **회원 정보를 파일에 영구 저장**하도록 구현하였다.

기존 메모리 기반 저장 방식은 프로그램 종료 시 데이터가 사라지는 한계가 있었기 때문에, 실제 사용자 관리 흐름에 가까운 형태로 저장 구조를 확장하였다.

### MemoryMemberRepository – 파일 기반 저장 구조

`MemoryMemberRepository`는 회원 데이터를 Map에 저장한 뒤, 변경이 발생할 때마다 전체 회원 정보를 `members.txt` 파일로 기록하도록 구성하였다. 이를 통해 프로그램을 재실행하더라도 기존 회원 정보가 유지되도록 하였다.

구현 내용은 다음과 같다.

- `save(Member member)`
    신규 회원 저장 후 전체 데이터를 파일로 기록.

- `update(Member member)`
    비밀번호 변경 등 수정된 내용을 반영한 뒤 파일에 다시 저장.

- `storeToFile()`
    현재 저장소(Map)의 모든 회원 정보를 순서대로 파일에 덮어쓰는 공통 저장 로직.

이 구조를 통해 기본 예제보다 한 단계 발전된 형태의 사용자 관리 흐름을 구현하였다.

---

## 7) 구현 시 어려웠던 점 & 해결 방법

### 1) 기존 구조 확장 문제

- **문제:** 예제는 name 기반 단순 구조여서 loginId, 비밀번호 관리, 전화번호 검증을 반영하기 어려웠다.
- **해결:** 인터넷의 Spring 회원 관리 예제를 참고해 도메인·저장소·서비스·컨트롤러 구조를 확장했다. 이를 기반으로 실제 로그인/조회/검증 시나리오를 구현할 수 있도록 구조를 재정비했다.

### 2) loginId 중복 처리

- **문제:** 중복된 loginId 가입 시 사용자 안내가 필요했다.
- **해결:** 블로그·문서에서 자주 사용되는 “서비스 중복 체크 → 컨트롤러 예외 처리” 패턴을 참고해 구현했다. 중복 시 전용 오류 페이지로 이동하도록 구성했다.

### 3) 아이디 찾기 및 비밀번호 재설정

- **문제:** 이름+전화번호 기반 검증, 임시 비밀번호 생성이 필요했다.
- **해결:** 랜덤 문자열 기반 임시 비밀번호 생성 방식을 참고해 구현했다.
    
    이후 검증 성공 시 `Member.password`를 업데이트하도록 구성했다.
    

### 4) 비밀번호 직접 변경 기능 구현(직접 설계·구현)

- **문제:** 초기 예제는 임시 비밀번호 발급까지는 가능하지만, “사용자가 원하는 비밀번호 입력 → 영구 저장” 흐름이 존재하지 않았다.
- **해결:**
    - Repository에 `update(Member member)` 메서드를 직접 추가
    - MemoryMemberRepository에서 전체 저장소 업데이트 + 파일 저장 로직 구현
    - Service에 `updatePassword(loginId, phoneNumber, newPassword)` 추가
    - Controller에서 newPassword를 입력하는 폼과 처리 라우트를 구성
    - Templates에 새 비밀번호 입력 UI 및 결과 페이지 추가
        
        이러한 흐름은 인터넷 예제에 없던 기능으로, 보고서 요구에 따라 직접 설계해 구현했다.
        

### 5) 템플릿 충돌 및 공통 UI 구성

- **문제:** 정적 리소스 우선순위 때문에 홈 화면이 표시되지 않는 문제 발생.
- **해결:** 정적 index.html 제거 후 템플릿 렌더링 정상화. CSS 통합으로 화면 일관성 확보.

### 6) 라우트 충돌 문제

- **문제:** `/members/new`가 여러 컨트롤러에 존재해 ambiguous mapping 오류 발생.
- **해결:** 해당 매핑을 단일 컨트롤러로 통합해 문제 해결.

---

## 8) 참고 자료

- https://globalman96.tistory.com/28
- https://sasca37.tistory.com/160