package simplechat.error;

public class ContactAlreadyExistException extends RuntimeException{
    private String phone;

    public ContactAlreadyExistException(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
}
