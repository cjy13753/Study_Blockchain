package blockchain;

import java.util.Scanner;

public class AppMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numOfZeros = scanner.nextInt();
        System.out.println("Enter how many zeros the hash must start with: " + numOfZeros + "\n");

        Blockchain blockchain = new Blockchain(numOfZeros);
        blockchain.addBlock();
        blockchain.addBlock();
        blockchain.addBlock();
        blockchain.addBlock();
        blockchain.addBlock();

        blockchain.isValidated();

        blockchain.printAllBlock();
    }
}
