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
        String allFieldsCombined = block.id + block.timestamp + block.previousHash;
        block.hash = StringUtil.applySha256(allFieldsCombined);
        return block;
    }

    public void printBlock() {
        System.out.println("Block:");
        System.out.println("Id: " + id);
        System.out.println("Timestamp: " + timestamp);
        System.out.println("Hash of the previous block: ");
        System.out.println(previousHash);
        System.out.println("Hash of the block: ");
        System.out.println(hash);
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }
}
