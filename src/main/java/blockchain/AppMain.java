package blockchain;

public class AppMain {
    public static void main(String[] args) {
        Blockchain blockchain = new Blockchain();
        blockchain.addBlock();
        blockchain.addBlock();
        blockchain.addBlock();
        blockchain.addBlock();
        blockchain.addBlock();

        blockchain.isValidated();

        blockchain.printAllBlock();
    }
}
