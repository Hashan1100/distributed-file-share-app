package com.uom.dist.node.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Component
public class DownloadService {
    private final int LOW = 2;
    private final int HIGH = 10;
    private static final Logger logger = LoggerFactory.getLogger(DownloadService.class);

    @Autowired
    private FileService fileService;

    public int getRandomValue(){
        Random random = new Random();
        int randomNumber = random.nextInt(HIGH - LOW) + LOW;
        logger.debug("Generated random number: [{}]", randomNumber);
        return randomNumber;
    }

    public byte[] content(int randomValue, byte[] array) {
        logger.debug("Generating random data for file");
        Random random = new Random();
        random.setSeed(randomValue);
        random.nextBytes(array);
        return array;
    }

    public byte[] getFile(String fileName) throws Exception {
        if (fileService.isFileAvailable(fileName)) {
            int randomValue = getRandomValue();
            byte[] fixedFileSizeByteArray = new byte[1024 * 1024 * randomValue];
            byte[] fileContent = content(randomValue, fixedFileSizeByteArray);
            logger.debug("File context generated ...");
            return fileContent;
        } else {
            throw new Exception("File not available " + fileName);
        }
    }

    public ResponseEntity<Resource> getResponse(String fileName) {
        logger.debug("File download request arrived with file name [{}]", fileName);
        try {
            byte[] fileContent = getFile(fileName);
            byte[] sha1Byte = generateSha1Bytes(fileContent);
            String sha1Hex = DigestUtils.sha1Hex(sha1Byte);
            logger.debug("SHA-1 file [{}] data is [{}]", fileName, sha1Hex);
            Resource file = new ByteArrayResource(fileContent);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+ fileName)
                    .header("sha1", sha1Hex)
                    .body(file);
        } catch (Exception e) {
            logger.error("File not found sending error response", e);
            return ResponseEntity
                    .notFound()
                    .header("fileName", fileName)
                    .build();
        }
    }

    private byte[] generateSha1Bytes(byte[] fileContent) throws NoSuchAlgorithmException {
        logger.debug("Generating sha1 for file content");
        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(fileContent);
        return crypt.digest();
    }
}