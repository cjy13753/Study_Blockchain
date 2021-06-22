package blockchain;

import java.util.ArrayList;

public class Blockchain {
    private final ArrayList<Block> chain = new ArrayList<>();
    private static int idCount = 1;
    private static String latestHash = "0";

    public void addBlock() {
        Block newBlock = Block.createBlock(getIdCount(), getLatestHash());
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

        System.out.println("\n===== This blockchain is valid =====\n");
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

    public static int getIdCount() {
        return idCount;
    }

    public static String getLatestHash() {
        return latestHash;
    }

    private static void setIdCount(int idCount) {
        Blockchain.idCount = idCount;
    }

    private static void setLatestHash(String latestHash) {
        Blockchain.latestHash = latestHash;
    }
}
