package ro.tuc.ds2020.dtos;

public class MessageReadRequest {
    private String senderId;
    private String receiverId;

    // Getter for senderId
    public String getSenderId() {
        return senderId;
    }

    // Setter for senderId
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    // Getter for receiverId
    public String getReceiverId() {
        return receiverId;
    }

    // Setter for receiverId
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}

