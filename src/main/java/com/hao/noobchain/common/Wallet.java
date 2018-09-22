package com.hao.noobchain.common;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class Wallet {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public Wallet() {
        generateKeyPair();
    }

    public void generateKeyPair() {
        try {
            var keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            var random = SecureRandom.getInstance("SHA1PRNG");
            var ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random);   //256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

}
