package blockchain;

import blockchain.core.*;
import blockchain.utility.MiningUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /* Configuration */
    private static final int numberOfNewBlocksToCreate = 3;
    private static final File blockchainFile = new File("./blockchain.txt");
    private static final int NUMBER_OF_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final boolean isBlockchainFileDeleted = false;
    private static final int startingNumberOfZeros = 0;

    public static void main(String[] args) throws InterruptedException{

        /* Mining starts */
        if (blockchainFile.exists()) {
            Blockchain blockchain = Blockchain.readBlockchainFromFile(blockchainFile);
            logger.info("Your blockchain file has been successfully loaded to memory.");
            if (blockchain.isLoadedChainValid()) {
                logger.info("The loaded blockchain is valid.");
                MiningUtility.executeMiningCycle(blockchain, NUMBER_OF_PROCESSORS, numberOfNewBlocksToCreate);
            } else {
                logger.error("Your blockchain file has been contaminated. Please check if you chose the right file.");
            }
        } else {
            Blockchain blockchain = new Blockchain(startingNumberOfZeros, blockchainFile);
            MiningUtility.executeMiningCycle(blockchain, NUMBER_OF_PROCESSORS, numberOfNewBlocksToCreate);
        }

        /* Deleting blockchain.txt */
        if (isBlockchainFileDeleted) {
            blockchainFile.delete();
            logger.trace("File successfully deleted.");
        }
    }
}