/*
 * Created on Jun 29, 2005
 */
package org.jcommon.encryption;

import java.io.*;
import java.security.*;
import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.spec.*;

public class Cryptor {
    public static String algorithm = "AES";
    public static byte[] base = {(byte)0xa8, (byte)0x69, (byte)0xe2, (byte)0x6f, (byte)0x61, (byte)0x1a, (byte)0xa0, (byte)0xfa, (byte)0x80, (byte)0xed, (byte)0x4b, (byte)0xe0, (byte)0x97, (byte)0xdc, (byte)0x59, (byte)0x76};
    
    public static void main(String[] args) throws Exception {
        File file1 = new File("test2.jar");
        File file2 = new File("test2_encrypted.jar");
        File file3 = new File("test2_decrypted.jar");
        
        long time = System.currentTimeMillis();
        encrypt("testing", file1, file2);
        System.out.println("Encryption Took: " + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        decrypt("testing", file2, file3);
        System.out.println("Decryption Took: " + (System.currentTimeMillis() - time));
    }
    
    public static void crypt(String password, int type, DataInputStream dis, DataOutputStream dos) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        if (password == null) password = "";
        KeyGenerator kgen = KeyGenerator.getInstance(algorithm);
        kgen.init(base.length * 8);

        byte[] raw = (byte[])base.clone();
        byte[] pass = password.getBytes();
        int length = pass.length; 
        if (length > raw.length) length = raw.length;
        for (int i = 0; i < length; i++) {
            raw[i] = pass[i];
        }
        
        SecretKeySpec sks = new SecretKeySpec(raw, algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(type, sks);
        CipherInputStream cis = new CipherInputStream(dis, cipher);
        int i;
        while ((i = cis.read()) >= 0) {
            dos.write(i);
        }
        dos.flush();
    }

    public static void encrypt(String password, DataInputStream dis, DataOutputStream dos) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        crypt(password, Cipher.ENCRYPT_MODE, dis, dos);
    }
    
    public static void decrypt(String password, DataInputStream dis, DataOutputStream dos) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        crypt(password, Cipher.DECRYPT_MODE, dis, dos);
    }
    
    public static void encrypt(String password, File in, File out) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream(in));
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(out));
        encrypt(password, dis, dos);
    }
    
    public static void decrypt(String password, File in, File out) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream(in));
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(out));
        decrypt(password, dis, dos);
    }
}
