package blockchain;

import java.util.ArrayList;

public class Blockchain {
    private final ArrayList<Block> chain = new ArrayList<>();
    private static int idCount = 1;
    private static String latestHash = "0";

    public void addBlock() {
        Block newBlock = Block.createBlock(idCount, latestHash);
        chain.add(newBlock);
        idCount += 1;
        latestHash = newBlock.getHash();
    }

    public void isValidated() {
        for (int i = 1; i < chain.size(); i++) {
            String previousHash = chain.get(i).getPreviousHash();
            String hashOfPreviousBlock = chain.get(i - 1).getHash();
            if (!previousHash.equals(hashOfPreviousBlock)) {
                throw new RuntimeException("This blockchain is contaminated.");
            }
        }
    }

    public void printAllBlock() {
        for (int i = 0; i < chain.size() - 1; i++) {
            chain.get(i).printBlock();
            System.out.println();
        }
        chain.get(chain.size() - 1).printBlock();
    }
}
