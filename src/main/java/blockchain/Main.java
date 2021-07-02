package blockchain;

import blockchain.core.Block;
import blockchain.core.Blockchain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        String filePath = "./blockchain.txt";
        int numberOfNewBlocksToCreate = 5;
        File blockchainFile = new File(filePath);

        if (blockchainFile.isFile()) {
            Blockchain blockchain = Blockchain.readBlockchainFromFile(blockchainFile);
            if (blockchain.isLoadedChainValid()) {
                demo(blockchain, numberOfNewBlocksToCreate, blockchain.getChain().size());
            } else {
                System.out.println("Your blockchain file is contaminated. Please check if you chose the right file.");
            }
        } else {
            Blockchain blockchain = new Blockchain(0, filePath);
            demo(blockchain, numberOfNewBlocksToCreate, 0);
        }
    }

    public static void demo(Blockchain blockchain, int numberOfNewBlocksToCreate, int initialNumberOfBlocks) {

        final int NUMBER_OF_PROCESSORS = Runtime.getRuntime().availableProcessors();

        for (int i = 1; i <= numberOfNewBlocksToCreate; i++) {
            ExecutorService es = Executors.newFixedThreadPool(NUMBER_OF_PROCESSORS);
            logger.trace("Executor Service has been initiated");

            int id = blockchain.getChain().size() + 1;
            String latestHash = blockchain.getLastBlockHash();
            int numOfZeros = blockchain.getNumOfZeros();

            Runnable blockMiner = () -> {
                try {
                    logger.trace("Started processing the task");
                    Block newBlock = Block.createBlock(id, latestHash, numOfZeros);
                    blockchain.addBlock(newBlock);
                } catch (Exception e) {
                    logger.error("Exception occurred while creating and adding a new block to the blockchain", e);
                }
            };

            for (int j = 0; j < NUMBER_OF_PROCESSORS; j++) {
                logger.trace("task submitted");
                es.submit(blockMiner);
            }

            while (blockchain.getChain().size() != i + initialNumberOfBlocks) {
                try {
                    logger.trace("Going into sleep while execution threads are finding the magic number");
                    Thread.sleep(3000L);
                } catch (Exception e) {
                    logger.debug("Exception occurred while the main thread goes to sleep", e);
                }
            }

            es.shutdownNow();
            try {
                boolean terminated;
                do {
                    terminated = es.awaitTermination(60, TimeUnit.MINUTES);
                } while (!terminated);
            } catch (Exception e) {
                logger.debug("Exception occurred while awaiting executor service termination.", e);
            }
            logger.trace("Executor Service has been shut down");
        }
    }
}
