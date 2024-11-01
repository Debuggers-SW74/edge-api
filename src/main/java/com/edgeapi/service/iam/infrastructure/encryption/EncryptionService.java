package com.edgeapi.service.iam.infrastructure.encryption;

public interface EncryptionService {
    String encrypt(String data);
    String decrypt(String encryptedData);
}
