package blockchain.core;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String writer;
    private String content;

    public Message(String writer, String content) {
        this.writer = writer;
        this.content = content;
    }

    public String toStringForHashGeneration() {
        return writer + content;
    }

    @Override
    public String toString() {
        return writer.length() == 0 && content.length() == 0 ? "no messages" : writer + ": created at " + content;
    }
}
