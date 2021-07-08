package blockchain;

import blockchain.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    /* Configuration */
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String filePath = "./blockchain.txt";
    private static final int numberOfNewBlocksToCreate = 5;
    private static final File blockchainFile = new File(filePath);
    private static final int NUMBER_OF_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final boolean isBlockchainFileDeleted = true;

    public static void main(String[] args) throws InterruptedException{

        /* Mining starts */
        if (blockchainFile.isFile()) {
            Blockchain blockchain = Blockchain.readBlockchainFromFile(blockchainFile);
            if (blockchain.isLoadedChainValid()) {
                executeMiningCycle(blockchain);
            } else {
                logger.error("Your blockchain file has been contaminated. Please check if you chose the right file.");
            }
        } else {
            Blockchain blockchain = new Blockchain(0, filePath);
            executeMiningCycle(blockchain);
        }
    }

    private static void executeMiningCycle(Blockchain blockchain) throws InterruptedException {
        /* Mining the first block without messages from users */
        blockchain.getUserMsgDeque().offerLast("no messages");
        mineNewBlock(blockchain);

        /* Create user threads for sending messages to the blcokchain's message deque */
        ExecutorService userExecutorService = createUserExecutorService(blockchain, List.of("Jun", "Mike"));
        try {
            logger.trace("userExecutorService is waiting for users to be prepared to send messages.");
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (Exception e) {
            logger.info("Exception Occurred while waiting for userExecutorService to be activated", e);
        }

        /* Mining blocks with messages from users */
        while (blockchain.getUserMsgDeque().size() < 5) {
            TimeUnit.MILLISECONDS.sleep(100);
        }
        for (int i = 0; i < numberOfNewBlocksToCreate - 1; i++) {
            mineNewBlock(blockchain);
        }

        /* userExecutorService shutdown */
        userExecutorService.shutdownNow();

        /* Deleting blockchain.txt */
        if (isBlockchainFileDeleted) {
            blockchainFile.delete();
        }
    }

    private static void mineNewBlock(Blockchain blockchain) throws InterruptedException {
        int blockId = blockchain.getChain().size() + 1;
        String lastBlockHash = blockchain.getLastBlockHash();
        int numOfZeros = blockchain.getNumOfZeros();

        Deque<String> userMsgDeque = blockchain.getUserMsgDeque();
        Deque<String> userMsgRollbackStack = new LinkedList<>();

        StringBuilder sb = new StringBuilder();
        synchronized (userMsgDeque) {
            Stream.iterate(0, i -> i + 1)
                    .limit(5)
                    .forEach(i -> {
                        sb.append("\n");
                        userMsgRollbackStack.offerLast(userMsgDeque.peekFirst());
                        sb.append(userMsgDeque.pollFirst());
                    });
        }
        String blockData = sb.toString();

        ExecutorService miningExecutorService = Executors.newFixedThreadPool(NUMBER_OF_PROCESSORS);
        logger.trace("miningExecutorService has been initiated");

        Set<BlockMiner> minerSet = Stream.iterate(0, i -> i + 1)
                .limit(Main.NUMBER_OF_PROCESSORS)
                .map(i -> new BlockMiner(blockId, lastBlockHash, numOfZeros, blockData))
                .collect(Collectors.toSet());

        Block newlyCreatedBlock;
        try {
            newlyCreatedBlock = miningExecutorService.invokeAny(minerSet);
        } catch (Exception e) {
            while (userMsgRollbackStack.peekLast() != null) {
                userMsgDeque.offerFirst(userMsgRollbackStack.pollLast());
            }
            logger.debug("Exception occurred while calling invokeAny() method. Messages Rollback Done.", e);
            return;
        }
        blockchain.addBlock(newlyCreatedBlock);

        miningExecutorService.shutdownNow();
        try {
            miningExecutorService.awaitTermination(60, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.debug("Exception occurred while awaiting executor service termination.", e);
        }
        logger.trace("miningExecutorService has been shut down");
    }

    private static ExecutorService createUserExecutorService(Blockchain blockchain, List<String> userNameList) {
        logger.trace("userExecutorService starts getting activated");
        int numOfThreads = Math.min(userNameList.size(), 4);
        ExecutorService userExecutorService = Executors.newFixedThreadPool(numOfThreads);

        userNameList.stream()
                .map(userName -> new User(blockchain, userName))
                .collect(Collectors.toList())
                .forEach(userExecutorService::submit);

        return userExecutorService;
    }
}
