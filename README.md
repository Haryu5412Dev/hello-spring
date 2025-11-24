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

### Member 도메인

- `loginId`, `password`, `phoneNumber` 필드 추가

### Repository

- `findByLoginId(String loginId)`
- `findByNameAndPhone(String name, String phoneNumber)`
- **`update(Member member)` 메서드 추가 (비밀번호 변경 저장용)**

### Repository 구현 (MemoryMemberRepository)

- 회원 정보 업데이트 메서드 구현
- 업데이트 후 `members.txt` 파일에 저장하여 영구 반영

### Service

- 회원가입 시 중복 `loginId` 검사
- 로그인 인증(`authenticate`)
- 아이디 찾기
- 비밀번호 재설정(임시 비밀번호 발급)
- **updatePassword(loginId, phoneNumber, newPassword)**
    - 전화번호 검증 후 새로운 비밀번호로 갱신
    - repository.update() 호출로 파일에 저장

### Controller

- 회원가입, 로그인, 아이디 찾기, 비밀번호 재설정 라우트 관리
- **POST /reset-password**에서 새 비밀번호를 받아 `updatePassword()` 호출
- 라우트 충돌 제거(`/members/new`)

## 최근 UI 및 기능 업데이트

- **공통 스타일 적용:**
    `static/css/style.css` 추가 — 카드형 레이아웃, 버튼, 입력 폼 통일

- **글꼴 적용:**
    `static/font/neodgm.ttf`을 기본 폰트로 등록

- **루트 경로 충돌 해결:**
    `static/index.html` 삭제 → `home.html` 정상 노출

- **템플릿 일관성 강화:**
    모든 템플릿에 공통 레이아웃 적용

- **회원 목록 확장:**
    `loginId`, `phoneNumber` 컬럼 추가

- **로그인 기능 구현:**
    `POST /login`, `loginSuccess.html` 연동

- **비밀번호 직접 변경 UI 추가:**
    `resetPasswordForm.html`에 새 비밀번호 입력 필드 추가
    `resetPasswordResult.html`에 결과 메시지 표시

- **라우팅 안정화:**
    중복 매핑 제거

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