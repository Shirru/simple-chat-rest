package simplechat.error;


public class ChatUserNotFoundException extends RuntimeException{

    private Long chatUserId;

    public ChatUserNotFoundException(Long chatUserId) {
        this.chatUserId = chatUserId;
    }

    public Long getChatUserId() {
        return chatUserId;
    }
}
