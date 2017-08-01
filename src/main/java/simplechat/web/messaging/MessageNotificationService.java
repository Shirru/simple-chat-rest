package simplechat.web.messaging;


import simplechat.domain.Message;

public interface MessageNotificationService {
    void notifyReceiver(Message message);
}
