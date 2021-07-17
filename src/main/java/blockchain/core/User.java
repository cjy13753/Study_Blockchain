package blockchain.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class User implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(User.class);

    private Blockchain blockchain;
    private String userName;
    private String privateKeyPath;
    private String publicKeyPath;

    public User(Blockchain blockchain, String userName) {
        this.blockchain = blockchain;
        this.userName = userName;
        this.privateKeyPath = "keypair/privateKey_" + userName;
        this.publicKeyPath = "keypair/publicKey_" + userName;

        if (!new File(privateKeyPath).exists() || !new File(publicKeyPath).exists()) {
            try {
                createKeyFiles();
            } catch (Exception e) {
                logger.error("Exception occurred during creating user key files.", e);
                System.exit(1);
            }
        }
    }

    private void createKeyFiles() throws IOException, NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);

        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();

        writeToFile(privateKeyPath, privateKey.getEncoded());
        writeToFile(publicKeyPath, publicKey.getEncoded());
    }

    public void writeToFile(String keyPath, byte[] key) throws IOException {
        File f = new File(keyPath);
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }

    @Override
    public Integer call() throws Exception {
        int count = 0;
        while (count != 1000) {
            int id = blockchain.retrieveMessageId();
            Message newMessage = new Message(id, userName, String.valueOf(LocalDateTime.now()), publicKeyPath, privateKeyPath);

            if (!blockchain.pushMessage(newMessage)) {
                logger.error(String.format("message sent by %s rejected by the blockchain.", userName));
            }

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
