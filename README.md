# Spring Boot 회원 관리 프로젝트

## 1) 프로젝트 개요

기존 수업시간에 만들던 예제에서 단순히 name만으로 회원을 등록하던 구조에서 요구 사항인 loginId, password, phoneNumber 기반의 회원 관리 기능을 추가했으며, 
사용자가 스스로 비밀번호를 변경할 수 있는 기능까지 포함하도록 개선했습니다.

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
- 비밀번호 재설정(**새 비밀번호 직접 설정 기능**)
    -> 사용자가 입력한 새로운 비밀번호를 저장소에 영구 반영
    

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
    - `/members/new` -> 회원가입 폼
    - 중복 `loginId` 시 `joinError.html`로 안내
- **아이디 찾기:**
    - `/find-login-id`
- **비밀번호 재설정:**
    - `/reset-password`, `/reset-password`
    - **새 비밀번호 직접 설정** 지원

## 6) 핵심 코드 구조

회원 정보가 프로그램 종료 후에도 유지되도록 하기 위해서, `MemoryMemberRepository`에 파일 기반 저장 기능을 직접 구현하였다.

회원 정보가 추가되거나 수정될 때마다 전체 데이터를 `members.txt`에 즉시 반영하는 방식으로 하였다.

---

### `save(Member member)`

```java
@Override
public Member save(Member member) {
    member.setId(++sequence);
    store.put(member.getId(), member);
    storeToFile();
    return member;
}
```

신규 회원을 저장한 후, 현재 저장소(Map)에 있는 모든 회원 정보를 파일로 다시 기록한다.
이 과정을 통해 새로운 가입이 발생할 때마다 파일 내용이 최신 상태로 유지되도록 하였다.

---

### `update(Member member)`

```java
@Override
public void update(Member member) {
    store.put(member.getId(), member);
    storeToFile();
}
```

비밀번호 변경과 같은 회원 정보 수정 시 사용된다.
수정된 Member 객체를 저장소에 반영하고, 수정된 전체 데이터를 파일에 다시 저장하도록 구성하였다.

---

### `storeToFile()`

```java
private void storeToFile() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("members.txt"))) {
        for (Member member : store.values()) {
            writer.write(member.getId() + "," +
                         member.getLoginId() + "," +
                         member.getName() + "," +
                         member.getPhoneNumber() + "," +
                         member.getPassword());
            writer.newLine();
        }
    } catch (IOException e) {
        throw new RuntimeException("파일 저장 중 오류 발생", e);
    }
}
```

현재 저장된 모든 회원 정보를 한 줄씩 파일에 기록하는 기능을 담당한다.
파일을 새로 생성하는 방식으로 작성하여, 저장·수정 작업 이후에도 데이터가 항상 일관되게 유지되도록 하였다.

---

## 7) 구현 시 어려웠던 점 & 해결 방법

### 1) 기존 구조 확장 문제

- **문제:** 예제는 name 기반 단순 구조여서 loginId, 비밀번호 관리, 전화번호 검증을 반영하기 어려웠다.
- **해결:** 인터넷의 회원 관리 예제를 참고해 도메인, 저장소, 서비스, 컨트롤러 구조를 확장했다. 이를 기반으로 실제 로그인/조회/검증 시나리오를 구현할 수 있도록 구조를 재정비했다.

### 2) loginId 중복 처리

- **문제:** 중복된 loginId 가입 시 사용자 안내가 필요했다.
- **해결:** 블로그 예제에서 자주 사용되는 “서비스 중복 체크 -> 컨트롤러 예외 처리” 패턴을 참고해 구현했다. 중복 시 전용 오류 페이지로 이동하도록 구성했다.

### 3) 아이디 찾기 및 비밀번호 재설정

- **문제:** 이름 + 전화번호 기반 검증 아이디 찾기와 ID + 전화번호 비밀번호 재설정이 필요했다.
- **해결:** 검증 성공 시 `Member.password`와 `Member.id`를 업데이트 하도록 구성했다.

### 4) 비밀번호 직접 변경 기능 구현

- **문제:** 초기가 만든 코드에서는 임시 비밀번호 발급까지는 가능하지만, “사용자가 원하는 비밀번호 입력 -> 영구 저장” 흐름이 존재하지 않았다.
- **해결:**
    - Repository에 `update(Member member)` 메서드를 직접 추가
    - MemoryMemberRepository에서 전체 저장소 업데이트 + 파일 저장 로직 구현
    - Service에 `updatePassword(loginId, phoneNumber, newPassword)` 추가
    - Controller에서 newPassword를 입력하는 폼과 처리 라우트를 구성
    - Templates에 새 비밀번호 입력 UI 및 결과 페이지 추가 

### 5) 템플릿 충돌 및 공통 UI 구성

- **문제:** 정적 리소스 우선순위 때문에 홈 화면이 표시되지 않는 문제 발생.
- **해결:** 정적 index.html 제거 후 템플릿 렌더링 정상화. CSS 통합으로 화면 일관성 확보.

### 6) 라우트 충돌 문제

- **문제:** `/members/new`가 여러 컨트롤러에 존재해 ambiguous mapping(어떤 입력이 어떤 출력과 연결되는지 불명확한 상황) 오류 발생.
- **해결:** 해당 매핑을 단일 컨트롤러로 통합해 문제 해결.

---

## 8) 참고 자료

- https://globalman96.tistory.com/28
- https://sasca37.tistory.com/160