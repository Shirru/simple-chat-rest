package simplechat.domain;

import org.apache.commons.lang.builder.EqualsBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "message")
public class Message implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender")
    private Long sender;

    @Column(name = "receiver")
    private Long receiver;

    @Column(name = "mtext")
    private String mtext;

    @Column(name = "mdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date mdate;

    public Message() { }

    public Message(String mtext, Date mdate) {
        this(null, null, null, mtext, mdate);
    }

    public Message(Long sender, Long receiver, String mtext, Date mdate) {
        this(null, sender, receiver, mtext, mdate);
    }

    public Message(Long id, Long sender, Long receiver, String mtext, Date mdate) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.mtext = mtext;
        this.mdate = mdate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSender() {
        return sender;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public Long getReceiver() {
        return receiver;
    }

    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }

    public String getMtext() {
        return mtext;
    }

    public void setMtext(String mtext) {
        this.mtext = mtext;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    @Override
    public boolean equals(Object that) {
        String fields[] = {"id"};
        return EqualsBuilder.reflectionEquals(this, that, fields);
    }
}
