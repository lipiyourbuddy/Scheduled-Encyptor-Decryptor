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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.DirectoryStream;
import java.io.IOException;
import java.util.Iterator;

import org.springframework.boot.SpringApplication;

@Component
public class FileEncryptJob implements Job {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Path inputDir = Paths.get("C:/Users/C22684/eclipse-workspace/scheduled_encryptor/src/main/resources/static/data/input");
        Path outputDir = Paths.get("C:/Users/C22684/eclipse-workspace/scheduled_encryptor/src/main/resources/static/data/encrypted");

        try {
            Files.createDirectories(inputDir);
            Files.createDirectories(outputDir);

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(inputDir)) {
                Iterator<Path> iterator = stream.iterator();

                if (!iterator.hasNext()) {
                    System.out.println("No files found. Shutting down the application...");
                    int exitCode = SpringApplication.exit(applicationContext, () -> 0);
                    System.exit(exitCode);
                }

                while (iterator.hasNext()) {
                    Path file = iterator.next();

                    if (Files.isRegularFile(file)) {
                        byte[] data = Files.readAllBytes(file);
                        byte[] encryptedData = encrypt(data);

                        Path targetFile = outputDir.resolve(file.getFileName().toString() + ".enc");
                        Files.write(targetFile, encryptedData);

                        Path processedDir = Paths.get("C:/Users/C22684/eclipse-workspace/scheduled_encryptor/src/main/resources/static/data/processed");
                        Files.createDirectories(processedDir);

                        Path movedFile = processedDir.resolve(file.getFileName());
                        Files.move(file, movedFile);

                        System.out.println("Encrypted and moved file: " + file.getFileName());
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new JobExecutionException("File processing failed", e);
        }
    }

    private byte[] encrypt(byte[] data) throws IOException {
        try {
            SecretKey key = new SecretKeySpec("1234567890123456".getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new IOException("Encryption failed", e);
        }
    }
}
