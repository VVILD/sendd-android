package co.sendd.gettersandsetters;

/**
 * Created by harshkaranpuria on 9/24/15.
 */
public class NameEmailObject {
    private String name;

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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    private String email;
    private String user;
}
