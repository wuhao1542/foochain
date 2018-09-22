package com.hao.noobchain.common;

import com.hao.noobchain.NoobChain;
import com.hao.noobchain.util.StringUtils;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.jooq.lambda.Seq.*;

public class Transaction {

    private String transactionId;
    private final PublicKey sender;
    private final PublicKey recipient;
    private final double value;
    private byte[] signature;

    private final List<TransactionInput> inputs;
    private List<TransactionOutput> outputs = new ArrayList<>();

    private static AtomicLong sequence = new AtomicLong(0);

    public Transaction(PublicKey from, PublicKey to, double value, List<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    private String calculateHash() {
        return StringUtils.applySha256(
                StringUtils.getStringFromKey(sender) +
                    StringUtils.getStringFromKey(recipient) +
                    Double.toString(value) +
                    sequence.incrementAndGet()
        );
    }

    //Signs all the data we don't wish to be tampered with.
    public void generateSignature(PrivateKey privateKey) {
        var data = StringUtils.getStringFromKey(sender) + StringUtils.getStringFromKey(recipient) + Double.toString(value);
        this.signature = StringUtils.applyECDSASign(privateKey, data);
    }

    //Verifies the data we signed hasn't been tampered with
    public boolean verifySignature() {
        var data = StringUtils.getStringFromKey(sender) + StringUtils.getStringFromKey(recipient) + Double.toString(value);
        return StringUtils.verifyECDSASign(sender, data, signature);
    }


    //Returns true if new transaction could be created.
    public boolean processTransaction() {
        if(!verifySignature()) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        //gather transaction inputs (Make sure they are unspent):
        for(TransactionInput i : inputs) {
            i.setUTXO(NoobChain.UTXOs.get(i.getTransactionOutputId()));
        }

        //check if transaction is valid:
        if(getInputsValue() < NoobChain.minimumTransaction) {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        //generate transaction outputs:
        double leftOver = getInputsValue() - value; //get value of inputs then the left over change:
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.recipient, value, transactionId)); //send value to recipient
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId)); //send the left over 'change' back to sender

        //add outputs to Unspent list
        for(TransactionOutput o : outputs) {
            NoobChain.UTXOs.put(o.getId(), o);
        }

        //remove transaction inputs from UTXO lists as spent:
        seq(inputs).filter(i -> i.getUTXO() != null)
                .map(i -> i.getUTXO().getId())
                .forEach(NoobChain.UTXOs::remove);

        return true;
    }

    //returns sum of inputs(UTXOs) values
    public double getInputsValue() {
        return seq(inputs).filter(i -> i.getUTXO() != null)
                .map(i -> i.getUTXO().getValue())
                .sum()
                .orElse(0d);
    }

    //returns sum of outputs:
    public double getOutputsValue() {
        return seq(outputs).map(TransactionOutput::getValue)
                    .sum()
                    .orElse(0d);
    }

    public String getTransactionId() {
        return transactionId;
    }
}
