package hello.hellospring.controller;

public class MemberForm {
    private String name;
    private String loginId; // 추가: 사용자 로그인 ID
    private String password; // 추가: 비밀번호
    private String phoneNumber; // 추가: 전화번호

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
