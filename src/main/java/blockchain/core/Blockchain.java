package blockchain.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Blockchain implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(Blockchain.class);

    private final ArrayList<Block> chain = new ArrayList<>();
    private int numOfZeros;
    private final String filePath;
    private final Queue<String> temporaryMessagesQueue = new ConcurrentLinkedQueue<>();

    public Blockchain(int numOfZeros, String filePath) {
        this.numOfZeros = numOfZeros;
        this.filePath = filePath;
    }

    public synchronized void addBlock(Block newBlock) {
        try {
            if (isNewBlockValid(newBlock)) {
                getChain().add(newBlock);

                newBlock.printBlock();

                if (newBlock.getGenerationTime() > 3.0f) {
                    if (getNumOfZeros() > 0) {
                        setNumOfZeros(getNumOfZeros() - 1);
                        System.out.println("N was decreased by 1");
                    }
                } else if (newBlock.getGenerationTime() > 1.5f) {
                    System.out.println("N stays the same");
                } else {
                    setNumOfZeros(getNumOfZeros() + 1);
                    System.out.println("N was increased to " + getNumOfZeros());
                }
                System.out.println();

                writeBlockchainToFile(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLastBlockHash() {
        if (getChain().size() == 0) {
            return "0";
        } else {
            return getChain().get(getChain().size() - 1).getHash();
        }
    }

    private void writeBlockchainToFile(Blockchain blockchain) {
        try {
            FileOutputStream fis = new FileOutputStream(getFilePath());
            BufferedOutputStream bos = new BufferedOutputStream(fis);
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            oos.writeObject(blockchain);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Blockchain readBlockchainFromFile(File file) {
        Blockchain blockchain = null;

        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(bis);
            blockchain = (Blockchain) ois.readObject();

            ois.close();
        } catch (Exception e) {
            logger.error("Exception occurred during loading existing blockchain.", e);
            System.exit(1);
        }

        return blockchain;
    }

    public boolean isNewBlockValid(Block newBlock) {
        if (!newBlock.getPreviousHash().equals(getLastBlockHash())) {
            logger.trace("The newly created block's previousHash field value is not equal to the chain's last block hash value");
            return false;
        }

        if (!newBlock.getHash().substring(0, getNumOfZeros()).equals("0".repeat(numOfZeros))) {
            logger.trace("The number-of-zeros requirement is not met");
            return false;
        }

        return true;
    }

    public boolean isLoadedChainValid() {
        ArrayList<Block> chainArray = getChain();
        for (int i = 1; i < chainArray.size(); i++) {
            String hashOfPreviousBlock = Block.calculateHash(chainArray.get(i - 1));
            String previousHash = chainArray.get(i).getPreviousHash();
            if (!previousHash.equals(hashOfPreviousBlock)) {
                logger.error("This blockchain is contaminated at block index # {}.", i - 1);
                return false;
            }
        }
        return true;
    }

    public void printAllBlock() {
        ArrayList<Block> chain = getChain();
        for (int i = 0; i < chain.size(); i++) {
            chain.get(i).printBlock();
        }
    }

    public ArrayList<Block> getChain() {
        return chain;
    }

    public int getNumOfZeros() {
        return numOfZeros;
    }

    public void setNumOfZeros(int numOfZeros) {
        this.numOfZeros = numOfZeros;
    }

    public String getFilePath() {
        return filePath;
    }

    public Queue<String> getTemporaryMessagesQueue() {
        return temporaryMessagesQueue;
    }
}
