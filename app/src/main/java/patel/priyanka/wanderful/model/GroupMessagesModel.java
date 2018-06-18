package patel.priyanka.wanderful.model;

import java.util.Date;

public class GroupMessagesModel {

    private String messageUser;
    private String messageText;
    private long messageTime;

    public GroupMessagesModel(String messageUser, String messageText) {
        this.messageUser = messageUser;
        this.messageText = messageText;

        messageTime = new Date().getTime();
    }

    public GroupMessagesModel() {
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
