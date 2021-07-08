package blockchain.core;

public class Message {
    private String writer;
    private String content;

    public Message(String writer, String content) {
        this.writer = writer;
        this.content = content;
    }

    @Override
    public String toString() {
        return writer + ": created at " + content;
    }




}
