package com.example.scheduled_encryptor.job;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.boot.SpringApplication;

import java.io.IOException;
import java.nio.file.*;

@Component
public class FileEncryptJob implements Job {

    @Autowired
    private ApplicationContext applicationContext;

    private static final String KEY = "1234567890123456";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Path inputDir = Paths.get("src/main/resources/static/data/input");
        Path outputDir = Paths.get("src/main/resources/static/data/output");

        try {
            Files.createDirectories(inputDir);
            Files.createDirectories(outputDir);

            boolean encrypted = encryptNextFile(inputDir, outputDir);
            if (encrypted) return;

            System.out.println("No files to encrypt. Checking encrypted folder for decryption...");

            boolean decrypted = decryptNextFile(outputDir, inputDir);
            if (decrypted) return;

            System.out.println("No files to decrypt. Shutting down...");
            int exitCode = SpringApplication.exit(applicationContext, () -> 0);
            System.exit(exitCode);
        } catch (IOException e) {
            throw new JobExecutionException("File processing failed", e);
        }
    }

    private boolean encryptNextFile(Path inputDir, Path outputDir) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(inputDir)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)
                        && !file.getFileName().toString().endsWith(".enc")
                        && !file.getFileName().toString().endsWith(".done")) {

                    
                    Path doneMarker = inputDir.resolve(file.getFileName().toString() + ".done");
                    if (Files.exists(doneMarker)) continue; 

                    byte[] data = Files.readAllBytes(file);
                    byte[] encryptedData = encrypt(data);

                    Path targetFile = outputDir.resolve(file.getFileName().toString() + ".enc");
                    Files.write(targetFile, encryptedData);
                    Files.delete(file);

                    System.out.println("Encrypted file: " + file.getFileName());
                    return true; 
                }
            }
        }
        return false;
    }


    private boolean decryptNextFile(Path outputDir, Path inputDir) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(outputDir)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file) && file.getFileName().toString().endsWith(".enc")) {
                    byte[] data = Files.readAllBytes(file);
                    byte[] decryptedData = decrypt(data);

                    String originalName = file.getFileName().toString().replaceFirst("\\.enc$", "");
                    Path targetFile = inputDir.resolve(originalName);
                    Files.write(targetFile, decryptedData);

                    
                    Path doneMarker = inputDir.resolve(originalName + ".done");
                    Files.createFile(doneMarker);

                    Files.delete(file);

                    System.out.println("Decrypted file: " + file.getFileName());
                    return true;
                }
            }
        }
        return false;
    }

    
    
    
    private byte[] encrypt(byte[] data) throws IOException {
        try {
            SecretKey key = new SecretKeySpec(KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new IOException("Encryption failed", e);
        }
    }

    private byte[] decrypt(byte[] data) throws IOException {
        try {
            SecretKey key = new SecretKeySpec(KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new IOException("Decryption failed", e);
        }
    }
}
