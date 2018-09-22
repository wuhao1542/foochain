package com.hao.noobchain.common;

import com.hao.noobchain.util.StringUtils;

public class Block {

    private String hash;
    private final String previousHash;
    private final String data;
    private final long timestamp;

    private long nonce = 0;

    public Block(String previousHash, String data) {
        this.previousHash = previousHash;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return StringUtils.applySha256(previousHash + timestamp + nonce + data);
    }

    public void mineBlock(int difficulty) {
        var target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
        while(!hash.substring(0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
