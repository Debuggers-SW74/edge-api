package com.edgeapi.service.iam.infrastructure.encryption.services;

import com.edgeapi.service.iam.infrastructure.encryption.EncryptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class EncryptionServiceImpl implements EncryptionService {
    private final String ALGORITHM = "AES";
    private final String TRANSFORMATION = "AES";

    @Value("${secret.key}")
    private String secretKey;

    @Override
    public String encrypt(String data) {
        try{
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKey key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            System.out.println("Error encrypting data: " + e.getMessage());
        }
        return  "";
    }

    @Override
    public String decrypt(String encryptedData) {
       try {
           Cipher cipher = Cipher.getInstance(TRANSFORMATION);
           SecretKey key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
           cipher.init(Cipher.DECRYPT_MODE, key);
           byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
           return new String(decryptedData);
       }catch (Exception e) {
           System.out.println("Error decrypting data: " + e.getMessage());
       }

       return "";
    }
}
