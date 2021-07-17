package blockchain.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Message implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(Message.class);

    private static final long serialVersionUID = 1L;

    private int id;
    private String writer;
    private String content;
    private String dataToSignAndVerify;
    private byte[] signature;
    private PublicKey publicKey;

    public Message(int id, String writer, String content, String publicKeyPath, String privateKeyPath) {
        this.id = id;
        this.writer = writer;
        this.content = content;
        this.dataToSignAndVerify = id + writer + content;

        try {
            this.signature = sign(privateKeyPath);
            this.publicKey = retrievePublicKey(publicKeyPath);
        } catch (Exception e) {
            logger.error("Exception Occurred during message creation.", e);
        }
    }

    private byte[] sign(String privateKeyPath) throws Exception {
        Signature rsa = Signature.getInstance("SHA1withRSA");
        rsa.initSign(retrievePrivateKey(privateKeyPath));
        rsa.update(dataToSignAndVerify.getBytes());
        return rsa.sign();
    }

    private PrivateKey retrievePrivateKey(String privateKeyPath) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(privateKeyPath).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private PublicKey retrievePublicKey(String publicKeyPath) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(publicKeyPath).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public boolean verifySignature() throws Exception {
        Signature rsa = Signature.getInstance("SHA1withRSA");
        rsa.initVerify(publicKey);
        rsa.update(dataToSignAndVerify.getBytes());

        return rsa.verify(signature);
    }

    public String toStringForHashGeneration() {
        return writer + content;
    }

    @Override
    public String toString() {
        return writer.length() == 0 && content.length() == 0 ? "no messages" : writer + ": created at " + content;
    }

    public int getId() {
        return id;
    }
}
