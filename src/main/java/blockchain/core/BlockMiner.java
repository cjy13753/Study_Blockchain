package blockchain.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class BlockMiner implements Callable<Block> {

    private static final Logger logger = LoggerFactory.getLogger(BlockMiner.class);

    private int blockId;
    private String lastBlockHash;
    private int numOfZeros;
    private String blockData;

    public BlockMiner(int blockId, String lastBlockHash, int numOfZeros, String blockData) {
        this.blockId = blockId;
        this.lastBlockHash = lastBlockHash;
        this.numOfZeros = numOfZeros;
        this.blockData = blockData;
    }

    @Override
    public Block call() throws Exception {
        logger.trace("Starts mining");
        return Block.createBlock(blockId, lastBlockHash, numOfZeros, blockData);
    }
}
