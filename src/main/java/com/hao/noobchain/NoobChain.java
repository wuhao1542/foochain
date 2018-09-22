package com.hao.noobchain;

import com.google.gson.GsonBuilder;
import com.hao.noobchain.common.Block;
import com.hao.noobchain.common.Transaction;
import com.hao.noobchain.common.TransactionOutput;
import com.hao.noobchain.common.Wallet;
import com.hao.noobchain.util.StringUtils;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoobChain {

    public static double minimumTransaction = 0.001;

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private static final List<Block> blockchain = new ArrayList<>();
    private static final int difficulty = 5;
    public static Map<String, TransactionOutput> UTXOs = new HashMap<>();
    private static final Wallet walletA = new Wallet();
    private static final Wallet walletB = new Wallet();


    public static void main(String... args) {


        //Test public and private keys
        System.out.println("Private and public keys:");
        System.out.println(StringUtils.getStringFromKey(walletA.getPrivateKey()));
        System.out.println(StringUtils.getStringFromKey(walletA.getPublicKey()));
        //Create a test transaction from WalletA to walletB
        var transaction = new Transaction(walletA.getPublicKey(), walletB.getPublicKey(), 5, null);
        transaction.generateSignature(walletA.getPrivateKey());
        //Verify the signature works and verify it from the public key
        System.out.print("Is signature verified? ");
        System.out.println(transaction.verifySignature());
    }

    public static Boolean isChainValid(List<Block> blockchain) {
        Block currentBlock;
        Block previousBlock;

        //loop through blockchain to check hashes:
        for(int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            //compare registered hash and calculated hash:
            if(!currentBlock.getHash().equals(currentBlock.calculateHash()) ){
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.getHash().equals(currentBlock.getPreviousHash()) ) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
        }
        return true;
    }

}
