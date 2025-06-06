package com.example.scheduled_encryptor.job;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.example.scheduled_encryptor.ScheduledEncryptorApplication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.DirectoryStream;
import java.io.IOException;



public class FileEncryptJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Path inputDir = Paths.get("C:/Users/C22684/eclipse-workspace/scheduled_encryptor/src/main/resources/static/data/input");
        Path outputDir = Paths.get("C:/Users/C22684/eclipse-workspace/scheduled_encryptor/src/main/resources/static/data/encrypted");

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(inputDir)) {
            for (Path file : stream) {
            	if(file!=null) {
	                if (Files.isRegularFile(file)) {
	                    
	                    byte[] data = Files.readAllBytes(file);
	                    byte[] encryptedData = encrypt(data);
	
	                    Path targetFile = outputDir.resolve(file.getFileName().toString() + ".enc");
	                    Files.write(targetFile, encryptedData);
	
	                    //Files.delete(file); 
	                    Path processedDir = Paths.get("C:/Users/C22684/eclipse-workspace/scheduled_encryptor/src/main/resources/static/data/processed");
	                    Files.createDirectories(processedDir);
	
	                    Path movedFile = processedDir.resolve(file.getFileName());
	                    Files.move(file, movedFile);
	
	
	                    System.out.println("Encrypted one file: " + file.getFileName());
	                    break; 
	                }
            	}
            	else {
            		inputDir = Paths.get("C:/Users/C22684/eclipse-workspace/scheduled_encryptor/src/main/resources/static/data/encrypted");
                    outputDir = Paths.get("C:/Users/C22684/eclipse-workspace/scheduled_encryptor/src/main/resources/static/data/input");
                    
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
