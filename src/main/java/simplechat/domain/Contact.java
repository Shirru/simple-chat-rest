package simplechat.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Contact {

    private String username;
    private String phone;

    public Contact(){}

    public Contact(String username, String phone) {
        this.username = username;
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

