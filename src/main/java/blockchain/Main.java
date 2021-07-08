package blockchain;

import blockchain.core.Block;
import blockchain.core.Blockchain;
import blockchain.core.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        String filePath = "./blockchain.txt";
        int numberOfNewBlocksToCreate = 3;
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

        /* First mining without message data */
        executeMining(blockchain, initialNumberOfBlocks, NUMBER_OF_PROCESSORS, 1);
        initialNumberOfBlocks += 1;

        /* Create user threads for sending messages to the blcokchain's message queue */
        ExecutorService userExecutorService = userExecutorService(blockchain, List.of("Jun", "Mike"));
        try {
            logger.trace("userExecutorService is being activated");
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (Exception e) {
            logger.trace("Exception Occurred while waiting for userExecutorService to be activated", e);
        }

        /* Mining continues */
        for (int count = 1; count <= numberOfNewBlocksToCreate; count++) {
            executeMining(blockchain, initialNumberOfBlocks, NUMBER_OF_PROCESSORS, count);
        }

        /* userExecutorService shutdown */
        userExecutorService.shutdownNow();
        try {
            boolean terminated = userExecutorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.debug("Exception occurred while awaiting executor service termination.", e);
        }
        logger.trace("userExecutorService has been shut down");
    }

    private static void executeMining(Blockchain blockchain, int initialNumberOfBlocks, int NUMBER_OF_PROCESSORS, int count) {
        ExecutorService es = Executors.newFixedThreadPool(NUMBER_OF_PROCESSORS);
        logger.trace("Executor Service has been initiated");

        int id = blockchain.getChain().size() + 1;
        String latestHash = blockchain.getLastBlockHash();
        int numOfZeros = blockchain.getNumOfZeros();
        Queue<String> tempMsgQueue = blockchain.getTemporaryMessagesQueue();

        StringBuilder sb = new StringBuilder();
        synchronized (tempMsgQueue) {
            while (tempMsgQueue.peek() != null) {
                sb.append("\n");
                sb.append(tempMsgQueue.poll());
            }
        }
        if (sb.toString().equals("")) {
            sb.append("no messages");
        }
        String blockData = sb.toString();

        Runnable blockMiner = () -> {
            try {
                logger.trace("Started processing the task");
                Block newBlock = Block.createBlock(id, latestHash, numOfZeros, blockData);
                blockchain.addBlock(newBlock);
            } catch (Exception e) {
                logger.error("Exception occurred while creating and adding a new block to the blockchain", e);
            }
        };

        for (int j = 0; j < NUMBER_OF_PROCESSORS; j++) {
            logger.trace("task submitted");
            es.submit(blockMiner);
        }

        while (blockchain.getChain().size() != count + initialNumberOfBlocks) {
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

    private static ExecutorService userExecutorService(Blockchain blockchain, List<String> userNameList) {
        logger.trace("userExecutorService starts getting activated");
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        userNameList.stream()
                .map(userName -> createNewUser(blockchain, userName))
                .forEach(executorService::submit);

        return executorService;
    }

    private static Runnable createNewUser(Blockchain blockchain, String userName) {
        return () -> {
            int count = 0;
            while (count != 100) {
                Message newMessage = new Message(userName, String.valueOf(Math.random()));
                blockchain.getTemporaryMessagesQueue().offer(newMessage.toString());
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (Exception e) {
                    logger.trace("Exception occurred during user going into sleep mode.", e);
                }
                ++count;
            }
        };
    }
}
