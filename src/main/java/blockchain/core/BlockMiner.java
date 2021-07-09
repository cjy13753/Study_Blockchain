package blockchain.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;

public class BlockMiner implements Callable<Block> {

    private static final Logger logger = LoggerFactory.getLogger(BlockMiner.class);

    private int blockId;
    private String lastBlockHash;
    private int numOfZeros;
    private List<Message> messageList;

    public BlockMiner(int blockId, String lastBlockHash, int numOfZeros, List<Message> messageList) {
        this.blockId = blockId;
        this.lastBlockHash = lastBlockHash;
        this.numOfZeros = numOfZeros;
        this.messageList = messageList;
    }

    @Override
    public Block call() throws Exception {
        logger.trace("Starts mining");
        return Block.createBlock(blockId, lastBlockHash, numOfZeros, messageList);
    }
}
