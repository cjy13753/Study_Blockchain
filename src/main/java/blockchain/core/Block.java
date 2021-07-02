package blockchain.core;

import blockchain.utility.StringUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

public class Block implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long minerID;
    private final int id;
    private final long timestamp;
    private final String hash;
    private final String previousHash;
    private final long magic;
    private float generationTime;


    private Block(long minerID, int id, long timestamp, long magic, String previousHash, String hash) {
        this.minerID = minerID;
        this.id = id;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.magic = magic;
        this.hash = hash;
    }

    public static Block createBlock(int id, String previousHash, int numOfZeros) {

        long minerID = Thread.currentThread().getId();
        long timestamp = new Date().getTime();
        long magic;
        String hash;

        long startTime = new Date().getTime();
        Random rand = new Random();
        String allFieldsCombined;

        do {
            magic = Integer.toUnsignedLong(rand.nextInt());
            allFieldsCombined = Block.concatFields(minerID, id, timestamp, magic, previousHash);
            hash = StringUtil.applySha256(allFieldsCombined);
        } while (!hash.substring(0, numOfZeros).equals("0".repeat(numOfZeros)));

        Block block = new Block(minerID, id, timestamp, magic, previousHash, hash);

        long endTime = new Date().getTime();
        float elapsedTime = (float) (endTime - startTime) / 1000;
        block.setGenerationTime(elapsedTime);

        return block;
    }

    static String calculateHash(Block block) {
        String allFieldsCombined = Block.concatFields(block.getMinerID(), block.getId(), block.getTimestamp(), block.getMagic(), block.getPreviousHash());
        return StringUtil.applySha256(allFieldsCombined);
    }

    private static String concatFields(long minerID, int id, long timestamp, long magic, String previousHash) {
        return minerID + id + timestamp + magic + previousHash ;
    }

    void printBlock() {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "Block:\n"
                + "Created by miner # " + getMinerID() + "\n"
                + "Id: " + getId() + "\n"
                + "Timestamp: " + getTimestamp() + "\n"
                + "Magic number: " + getMagic() + "\n"
                + "Hash of the previous block: \n"
                + getPreviousHash() + "\n"
                + "Hash of the block: \n"
                + getHash() + "\n"
        );
        sb.append(String.format("Block was generating for %.1f seconds", getGenerationTime()));
        System.out.println(sb.toString());
    }

    String getHash() {
        return hash;
    }

    String getPreviousHash() {
        return previousHash;
    }

    int getId() {
        return id;
    }

    long getTimestamp() {
        return timestamp;
    }

    long getMagic() {
        return magic;
    }

    float getGenerationTime() {
        return generationTime;
    }

    private void setGenerationTime(float generationTime) {
        this.generationTime = generationTime;
    }

    long getMinerID() {
        return minerID;
    }
}
