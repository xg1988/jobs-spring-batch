package com.chosu.springbatchbasic;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JasyptTest {

    @Value("${jasypt.encryptor.password}")
    private String PASSWORD;

    @Autowired
    private StringEncryptor jasyptStringEncryptor;

    @BeforeAll
    static void beforeAll(){
        System.getProperties().setProperty("jasypt.encryptor.password", "chosuservice");
    }

    @DisplayName("1. 암복호화 테스트")
    @Test
    void test_1(){
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setPoolSize(2);
        encryptor.setPassword("chosuservice");
        encryptor.setAlgorithm("PBEWithSHA256And128BitAES-CBC-BC");


        String plainText = "t2nkp0eIZ82dXtQ/clmf6p6hK30UiA5ydEw9gaFJNBINy/l/ixhlTfVMKfxFMsl7qpmn+B6yD9I=";
        String decryptedText = jasyptStringEncryptor.decrypt(plainText);
        String encryptedText = jasyptStringEncryptor.encrypt(decryptedText);
        System.out.println("Enc = " + encryptedText);
        System.out.println("Dec = " + decryptedText);

        String url = "jdbc:mysql://210.114.19.29:3306/jobsservice";
        String username = "jobs01";
        String password = "l2p7w4!!";
        System.out.println("url: ENC(" + jasyptStringEncryptor.encrypt(url) + ")");
        System.out.println("username: ENC(" + jasyptStringEncryptor.encrypt(username)+ ")");
        System.out.println("password: ENC(" + jasyptStringEncryptor.encrypt(password)+ ")");


        url = "jdbc:mysql://jobs-db-dev.cmuxldknvx4w.ap-northeast-2.rds.amazonaws.com:3306/jobsservice";
        username = "jobs01";
        password = "l2p7w4!!";
        System.out.println("url: ENC(" + jasyptStringEncryptor.encrypt(url) + ")");
        System.out.println("username: ENC(" + jasyptStringEncryptor.encrypt(username)+ ")");
        System.out.println("password: ENC(" + jasyptStringEncryptor.encrypt(password)+ ")");
    }
}
