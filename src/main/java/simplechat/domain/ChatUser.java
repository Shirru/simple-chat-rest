package simplechat.domain;

import org.apache.commons.lang.builder.EqualsBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_user")
public class ChatUser implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "ustatus")
    @Enumerated(EnumType.STRING)
    private Status status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "contact", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "chat_contact")
    private List<Contact> contacts;

    ChatUser() {}

    public ChatUser(String username, String firstName, String lastName, String phone) {
        this(null, username, firstName, lastName, phone, Status.online, new ArrayList<Contact>());
    }

    public ChatUser(Long id, String username, String firstName, String lastName,
                    String phone, Status status, List<Contact> contacts) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.status = status;
        this.contacts = contacts;
    }

    public Contact addContact(Contact contact) {
        boolean isContactExist = false;
        for (Contact c : contacts) {
            if (c.getPhone().equals(contact.getPhone())) {
                isContactExist = true;
                break;
            }
        }

        if (!isContactExist) {
            contacts.add(contact);
            return contact;
        }
        return null;
    }

    public void removeContact(Contact contact) {contacts.remove(contact);}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public boolean equals(Object that) {
        String fields[] = {"id", "contacts"};
        return EqualsBuilder.reflectionEquals(this, that, fields);
    }

}
