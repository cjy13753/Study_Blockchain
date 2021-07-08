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
    private final String blockData;


    private Block(long minerID, int id, long timestamp, long magic, String previousHash, String hash, String blockData) {
        this.minerID = minerID;
        this.id = id;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.magic = magic;
        this.hash = hash;
        this.blockData = blockData;
    }

    public static Block createBlock(int id, String previousHash, int numOfZeros, String blockData) {

        long minerID = Thread.currentThread().getId();
        long timestamp = new Date().getTime();
        long magic;
        String hash;

        long startTime = new Date().getTime();
        Random rand = new Random();
        String allFieldsCombined;

        do {
            magic = Integer.toUnsignedLong(rand.nextInt());
            allFieldsCombined = Block.concatFields(minerID, id, timestamp, magic, previousHash, blockData);
            hash = StringUtil.applySha256(allFieldsCombined);
        } while (!hash.substring(0, numOfZeros).equals("0".repeat(numOfZeros)));

        Block block = new Block(minerID, id, timestamp, magic, previousHash, hash, blockData);

        long endTime = new Date().getTime();
        float elapsedTime = (float) (endTime - startTime) / 1000;
        block.setGenerationTime(elapsedTime);

        return block;
    }

    static String calculateHash(Block block) {
        String allFieldsCombined = Block.concatFields(block.minerID, block.id, block.timestamp, block.magic, block.previousHash, block.blockData);
        return StringUtil.applySha256(allFieldsCombined);
    }

    private static String concatFields(long minerID, int id, long timestamp, long magic, String previousHash, String blockData) {
        return minerID + id + timestamp + magic + previousHash + blockData ;
    }

    void printBlock() {
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

}
