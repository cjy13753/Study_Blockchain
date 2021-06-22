package blockchain;

import blockchain.utility.StringUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

public class Block implements Serializable {
    private static final long serialVersionUID = 1234L;

    private final int id;
    private final long timestamp;
    private final String hash;
    private final String previousHash;
    private final long magic;
    private float generationTime;


    private Block(int id, long timestamp, String previousHash, long magic, String hash) {
        this.id = id;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.magic = magic;
        this.hash = hash;
    }

    public static Block createBlock(int id, String previousHash, int numOfZeros) {
        long startTime = new Date().getTime();
        Random rand = new Random();

        long timestamp = new Date().getTime();
        long magic;
        String hash;

        String allFieldsCombined;

        do {
            magic = 0xffffffffL & rand.nextInt();
            allFieldsCombined = Block.concatFields(id, timestamp, previousHash, magic);
            hash = StringUtil.applySha256(allFieldsCombined);
        } while (!hash.substring(0, numOfZeros).equals("0".repeat(numOfZeros)));

        Block block = new Block(id, timestamp, previousHash, magic, hash);

        long endTime = new Date().getTime();
        float elapsedTime = (float) (endTime - startTime) / 1000;
        block.setGenerationTime(elapsedTime);
        return block;
    }

    public static String calculateHash(Block block) {
        String allFieldsCombined = Block.concatFields(block.getId(), block.getTimestamp(), block.getPreviousHash(), block.getMagic());
        return StringUtil.applySha256(allFieldsCombined);
    }

    private static String concatFields(int id, long timestamp, String previousHash, long magic) {
        return id + timestamp + previousHash + magic;
    }

    public void printBlock() {
        System.out.println("Block:");
        System.out.println("Id: " + getId());
        System.out.println("Timestamp: " + getTimestamp());
        System.out.println("Magic number: " + getMagic());
        System.out.println("Hash of the previous block: ");
        System.out.println(getPreviousHash());
        System.out.println("Hash of the block: ");
        System.out.println(getHash());
        System.out.printf("Block was generating for %.1f seconds\n", getGenerationTime());
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
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

    public float getGenerationTime() {
        return generationTime;
    }

    private void setGenerationTime(float generationTime) {
        this.generationTime = generationTime;
    }
}
