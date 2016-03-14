package app.psiteportal.com.model;

/**
 * Created by fmpdroid on 3/11/2016.
 */
public class Member {

    private String name;
    private String email;
    private String status;

    public Member(String name, String email, String status) {
        this.name = name;
        this.email = email;
        this.status = status;
    }
    public Member(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
