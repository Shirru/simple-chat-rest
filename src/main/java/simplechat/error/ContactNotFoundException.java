package simplechat.error;


public class ContactNotFoundException extends RuntimeException{

    private String phone;

    public ContactNotFoundException(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
}
