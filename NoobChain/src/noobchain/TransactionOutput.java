package noobchain;

import java.security.PublicKey;

class TransactionOutput {
    String id;
    PublicKey recipient; //also known as the new owner of these coins.
    float value; //the amount of coins they own

    //Constructor
    TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        //the id of the transaction this output was created in
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(recipient)+ value +parentTransactionId);
    }

    //Check if coin belongs to you
    boolean isMine(PublicKey publicKey) {
        return (publicKey == recipient);
    }

}
