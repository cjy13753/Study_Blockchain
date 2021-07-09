package blockchain.core;

import blockchain.Main;
import blockchain.utility.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Block implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(Block.class);
    private static final long serialVersionUID = 1L;

    private final long minerID;
    private final int id;
    private final long timestamp;
    private final String hash;
    private final String previousHash;
    private final long magic;
    private float generationTime;
    private final List<Message> messageList;


    private Block(long minerID, int id, long timestamp, long magic, String previousHash, String hash, List<Message> messageList) {
        this.minerID = minerID;
        this.id = id;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.magic = magic;
        this.hash = hash;
        this.messageList = messageList;
    }

    public static Block createBlock(int id, String previousHash, int numOfZeros, List<Message> messageList) {

        long minerID = Thread.currentThread().getId();
        long timestamp = new Date().getTime();
        long magic;
        String hash;

        long startTime = new Date().getTime();
        Random rand = new Random();
        String allFieldsCombined;

        do {
            magic = Integer.toUnsignedLong(rand.nextInt());
            allFieldsCombined = Block.concatFields(minerID, id, timestamp, magic, previousHash, messageList);
            hash = StringUtil.applySha256(allFieldsCombined);
        } while (!hash.substring(0, numOfZeros).equals("0".repeat(numOfZeros)));

        Block block = new Block(minerID, id, timestamp, magic, previousHash, hash, messageList);

        long endTime = new Date().getTime();
        float elapsedTime = (float) (endTime - startTime) / 1000;
        block.setGenerationTime(elapsedTime);

        return block;
    }

    static String calculateHash(Block block) {
        String allFieldsCombined = Block.concatFields(
                block.getMinerID(),
                block.getId(),
                block.getTimestamp(),
                block.getMagic(),
                block.getPreviousHash(),
                block.getMessageList());
        return StringUtil.applySha256(allFieldsCombined);
    }

    private static String concatFields(long minerID, int id, long timestamp, long magic, String previousHash, List<Message> messageList) {
        String blockData = messageList.stream()
                .map(Message::toStringForHashGeneration)
                .collect(Collectors.joining());
        return minerID + id + timestamp + magic + previousHash + blockData;
    }

    void printBlock() {
        String blockData = messageList.stream()
                .map(Message::toString)
                .collect(Collectors.joining("\n"));
        blockData = blockData.equals("") ? "no messages" : "\n" + blockData;

        StringBuilder sb = new StringBuilder();
        sb.append(
                "Block:\n"
                + "Created by miner # " + minerID + "\n"
                + "Id: " + id + "\n"
                + "Timestamp: " + timestamp + "\n"
                + "Magic number: " + magic + "\n"
                + "Hash of the previous block: \n"
                + previousHash + "\n"
                + "Hash of the block: \n"
                + hash + "\n"
                + "Block data: "
                + blockData
        );
        sb.append(String.format("\nBlock was generating for %.1f seconds", generationTime));
        System.out.println(sb.toString());
    }

    private void setGenerationTime(float generationTime) {
        this.generationTime = generationTime;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public float getGenerationTime() {
        return generationTime;
    }

    public long getMinerID() {
        return minerID;
    }

    public int getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getMagic() {
        return magic;
    }

    public List<Message> getMessageList() {
        return messageList;
    }
}
