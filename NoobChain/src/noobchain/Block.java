package noobchain;

import java.util.ArrayList;
import java.util.Date;

class Block {

    String hash;
    String previousHash;
    ArrayList<Transaction> transactions = new ArrayList<>(); //our data will be a simple message.
    private String merkleRoot;
    private long timeStamp; //as number of milliseconds since 1/1/1970.
    private int nonce;

    //Noobchain.Block Constructor.
    Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();

        this.hash = calculateHash(); //Making sure we do this after we set the other values.
    }

    //Calculate new hash based on blocks contents
    String calculateHash() {
        String calculatedhash;
        calculatedhash = StringUtil.applySha256(
                previousHash +
                        timeStamp +
                        nonce +
                        merkleRoot
        );
        return calculatedhash;
    }

    //Increases nonce value until hash target is reached.
    void mineBlock(int difficulty) {
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target = StringUtil.getDifficultyString(difficulty); //Create a string with difficulty * "0"
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateHash();
        }
        System.out.println("Noobchain.Block Mined!!! : " + hash);
    }

    //Add transactions to this block
    boolean addTransaction(Transaction transaction) {
        //process transaction and check if valid, unless block is genesis block then ignore.
        if(transaction == null)
            return false;
        if((!"0".equals(previousHash))) {
            if(!transaction.processTransaction()) {
                System.out.println("Noobchain.Transaction failed to process. Discarded.");
                return false;
            }
        }

        transactions.add(transaction);
        System.out.println("Noobchain.Transaction Successfully added to Noobchain.Block");
        return true;
    }

}