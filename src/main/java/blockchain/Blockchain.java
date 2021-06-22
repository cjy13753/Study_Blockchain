package blockchain;

import java.util.ArrayList;

public class Blockchain {
    private final ArrayList<Block> chain = new ArrayList<>();
    private int idCount = 1;
    private String latestHash = "0";
    private int numOfZeros;

    public Blockchain(int numOfZeros) {
        this.numOfZeros = numOfZeros;
    }

    public void addBlock() {
        Block newBlock = Block.createBlock(getIdCount(), getLatestHash(), getNumOfZeros());
        getChain().add(newBlock);
        setIdCount(getIdCount() + 1);
        setLatestHash(newBlock.getHash());
    }

    public void isValidated() {
        for (int i = 1; i < getChain().size(); i++) {
            String previousHash = getChain().get(i).getPreviousHash();
            String hashOfPreviousBlock = Block.calculateHash(getChain().get(i - 1));
            if (!previousHash.equals(hashOfPreviousBlock)) {
                throw new RuntimeException("This blockchain is contaminated.");
            }
        }
    }

    public void printAllBlock() {
        for (int i = 0; i < getChain().size() - 1; i++) {
            getChain().get(i).printBlock();
            System.out.println();
        }
        getChain().get(getChain().size() - 1).printBlock();
    }

    public ArrayList<Block> getChain() {
        return chain;
    }

    public int getIdCount() {
        return idCount;
    }

    public String getLatestHash() {
        return latestHash;
    }

    private void setIdCount(int idCount) {
        this.idCount = idCount;
    }

    private void setLatestHash(String latestHash) {
        this.latestHash = latestHash;
    }

    public int getNumOfZeros() {
        return numOfZeros;
    }
}
