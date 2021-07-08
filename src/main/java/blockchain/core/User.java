package blockchain.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class User implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(User.class);

    private Blockchain blockchain;
    private String userName;

    public User(Blockchain blockchain, String userName) {
        this.blockchain = blockchain;
        this.userName = userName;
    }

    @Override
    public Integer call() throws Exception {
        int count = 0;
        while (count != 1000) {
            Message newMessage = new Message(userName, String.valueOf(LocalDateTime.now()));
            blockchain.getUserMsgDeque().offerLast(newMessage.toString());
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (Exception e) {
                logger.trace(String.format("Exception occurred during user \"%s\" going into sleep mode.", userName), e);
            }
            ++count;
        }
        return 0;
    }
}
