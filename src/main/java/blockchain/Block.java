package blockchain;

import blockchain.utility.StringUtil;

import java.util.Date;

public class Block {
    private final int id;
    private final long timestamp;
    private String hash;
    private final String previousHash;


    private Block(int id, String previousHash) {
        this.id = id;
        this.previousHash = previousHash;
        this.timestamp = new Date().getTime();
    }

    public static Block createBlock(int id, String previousHash) {
        Block block = new Block(id, previousHash);
        block.setHash(calculateHash(block));
        return block;
    }

    public static String calculateHash(Block block) {
        String allFieldsCombined = block.getId() + block.getTimestamp() + block.getPreviousHash();
        return StringUtil.applySha256(allFieldsCombined);
    }

    public void printBlock() {
        System.out.println("Block:");
        System.out.println("Id: " + getId());
        System.out.println("Timestamp: " + getTimestamp());
        System.out.println("Hash of the previous block: ");
        System.out.println(getPreviousHash());
        System.out.println("Hash of the block: ");
        System.out.println(getHash());
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

    private void setHash(String hash) {
        this.hash = hash;
    }
}
