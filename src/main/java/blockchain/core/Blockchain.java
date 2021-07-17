package blockchain.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Blockchain implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(Blockchain.class);

    private final ArrayList<Block> chain = new ArrayList<>();
    private int numOfZeros;
    private final File blockChainFile;
    private final Deque<Message> userMsgDeque = new ConcurrentLinkedDeque<>();
    private int messageId = 0;

    private int maxMsgIdOfBlockUnderConstruction = -1;

    public Blockchain(int numOfZeros, File blockChainFile) {
        this.numOfZeros = numOfZeros;
        this.blockChainFile = blockChainFile;
    }

    public void addBlock(Block newBlock) {
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
            FileOutputStream fis = new FileOutputStream(blockChainFile);
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

    // [Refactoring Needed] This method assumes that the first block has no messages.
    public boolean isLoadedChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            /* Message Id and signature verification*/
            List<Message> prevBlockMsgList = chain.get(i - 1).getMessageList();
            int prevBlockMaxId = prevBlockMsgList.isEmpty() ? -1 : prevBlockMsgList.get(prevBlockMsgList.size() - 1).getId();

            List<Message> currBlockMsgList = chain.get(i).getMessageList();
            for (Message message : currBlockMsgList) {
                if (message.getId() <= prevBlockMaxId) return false;
                try {
                    if (!message.verifySignature()) return false;
                } catch (Exception e) {
                    logger.error("Exception occurred verifying message signature while loading blockchain file.", e);
                    System.exit(1);
                }
            }

            /* Hash verification */
            String hashOfPreviousBlock = Block.calculateHash(chain.get(i - 1));
            String previousHash = chain.get(i).getPreviousHash();
            if (!previousHash.equals(hashOfPreviousBlock)) {
                logger.error("This blockchain is contaminated at block index # {}.", i - 1);
                return false;
            }
        }
        return true;
    }

    public synchronized int retrieveMessageId() {
        return messageId++;
    }

    public boolean pushMessage(Message newMessage) {
        if (verifyMessage(newMessage)) {
            userMsgDeque.offerLast(newMessage);
            return true;
        } else {
            return false;
        }
    }

    private boolean verifyMessage(Message newMessage) {
        try {
            if (!newMessage.verifySignature()) {
                logger.info("Signature verification failed.");
                return false;
            }
        } catch (Exception e) {
            logger.error("Exception Occurred while verifying new messages being pushed to the blockchain.", e);
        }
        if (!(newMessage.getId() > maxMsgIdOfBlockUnderConstruction)) {
            logger.info("Message id requirement is unmet.");
            return false;
        }
        return true;
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

    public Deque<Message> getUserMsgDeque() {
        return userMsgDeque;
    }

    public int getMaxMsgIdOfBlockUnderConstruction() {
        return maxMsgIdOfBlockUnderConstruction;
    }

    public void setMaxMsgIdOfBlockUnderConstruction(int maxMsgIdOfBlockUnderConstruction) {
        this.maxMsgIdOfBlockUnderConstruction = maxMsgIdOfBlockUnderConstruction;
    }
}