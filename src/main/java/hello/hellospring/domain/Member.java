package hello.hellospring.domain;

public class Member {
    private Long id;
    private String name;
    // 사용자 로그인 ID (추가 요구사항)
    private String loginId;
    // 비밀번호 (추가 요구사항)
    private String password;
    // 전화번호 (추가 요구사항)
    private String phoneNumber;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

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
