package blockchain;

import java.io.*;
import java.util.Scanner;

public class AppMain {
    public static void main(String[] args) {

        Blockchain blockchain = null;

        File file = new File("./blockchain.txt");

        if (file.isFile()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);
                ObjectInputStream ois = new ObjectInputStream(bis);
                blockchain = (Blockchain) ois.readObject();

                ois.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error occurred during loading existing blockchain.");
                System.exit(-1);
            }
            blockchain.isValid();

            blockchain.addBlock();
            blockchain.addBlock();
            blockchain.addBlock();
            blockchain.addBlock();
            blockchain.addBlock();

            blockchain.printAllBlock();

            return;
        }

        Scanner scanner = new Scanner(System.in);
        int numOfZeros = scanner.nextInt();
        System.out.println("Enter how many zeros the hash must start with: " + numOfZeros + "\n");

        blockchain = new Blockchain(numOfZeros);
        blockchain.addBlock();
        blockchain.addBlock();
        blockchain.addBlock();

        blockchain.printAllBlock();

    }
}
