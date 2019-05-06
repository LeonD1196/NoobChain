package noobchain;

import java.security.*;
import java.util.ArrayList;

class Transaction {

    String transactionId; //Contains a hash of transaction*
    PublicKey sender; //Senders address/public key.
    PublicKey recipient; //Recipients address/public key.
    float value; //Contains the amount we wish to send to the recipient.
    private byte[] signature; //This is to prevent anybody else from spending funds in our wallet.

    ArrayList<TransactionInput> inputs;
    ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0; //A rough count of how many transactions have been generated

    // Constructor:
    Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    boolean processTransaction() {

        if (!verifySignature()) {//Gathers transaction inputs (Making sure they are unspent):
            for (TransactionInput i : inputs) {
                i.UTXO = NoobChain.UTXOs.get(i.transactionOutputId);
            }

            //Checks if transaction is valid:
            if (getInputsValue() < NoobChain.minimumTransaction) {
                System.out.println("noobchain.Transaction Inputs too small: " + getInputsValue());
                System.out.println("Please enter the amount greater than " + NoobChain.minimumTransaction);
                return false;
            }

            //Generate transaction outputs:
            float leftOver = getInputsValue() - value; //get value of inputs then the left over change:
            transactionId = calulateHash();
            outputs.add(new TransactionOutput(this.recipient, value, transactionId)); //send value to recipient
            outputs.add(new TransactionOutput(this.sender, leftOver, transactionId)); //send the left over 'change' back to sender

            //Add outputs to Unspent list
            for (TransactionOutput o : outputs) {
                NoobChain.UTXOs.put(o.id, o);
            }

            //Remove transaction inputs from UTXO lists as spent:
            for (TransactionInput i : inputs) {
                if (i.UTXO == null) continue; //if noobchain.Transaction can't be found skip it
                NoobChain.UTXOs.remove(i.UTXO.id);
            }

            return true;
        } else {
            System.out.println("#noobchain.Transaction Signature failed to verify");
            return false;
        }

    }

    float getInputsValue() {
        float total = 0;
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; //if noobchain.Transaction can't be found skip it, This behavior may not be optimal.
            total += i.UTXO.value;
        }
        return total;
    }

    void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + value;
        signature = StringUtil.applyECDSASig(privateKey,data);
    }

    boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + value;
        return !StringUtil.verifyECDSASig(sender, data, signature);
    }

    float getOutputsValue() {
        float total = 0;
        for(TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }

    private String calulateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient) +
                        value + sequence
        );
    }
}