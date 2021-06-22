package blockchain;

import java.io.*;
import java.util.ArrayList;

public class Blockchain implements Serializable {
    private static final long serialVersionUID = 1234L;

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

        writeBlockchainToFile(this);
    }

    private void writeBlockchainToFile(Blockchain blockchain) {
        try {
            FileOutputStream fis = new FileOutputStream("./blockchain.txt");
            BufferedOutputStream bos = new BufferedOutputStream(fis);
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            oos.writeObject(blockchain);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void isValid() {
        for (int i = 1; i < getChain().size(); i++) {
            String previousHash = getChain().get(i).getPreviousHash();
            String hashOfPreviousBlock = Block.calculateHash(getChain().get(i - 1));
            if (!previousHash.equals(hashOfPreviousBlock)) {
                System.out.println("This blockchain is contaminated.");
                System.exit(-1);
            }
        }
    }

    public void printAllBlock() {
        for (int i = 0; i < getChain().size(); i++) {
            getChain().get(i).printBlock();
            System.out.println();
        }
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
