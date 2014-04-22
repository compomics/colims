package com.compomics.colims.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Davy
 */
public class SecurityUtils {

    public static byte[] md5DigestFasta(File fastaFile) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        if (fastaFile != null && fastaFile.exists()) {
            digest.update(Files.readAllBytes(Paths.get(fastaFile.getAbsolutePath())));
            return digest.digest();
        } else {
            throw new IOException("file does not exist");
        }
    }

    public static byte[] md5DigestFasta(String fastaFile) throws NoSuchAlgorithmException, IOException {
        if (fastaFile != null) {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(fastaFile.getBytes());
            return digest.digest();
        } else {
            throw new IOException("string could not be digested");
        }
    }

    public static byte[] md5DigestFasta(ByteBuffer input) throws NoSuchAlgorithmException, IOException {
        if (input != null) {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input);
            return digest.digest();

        } else {
            throw new IOException("could not be digested");
        }
    }

    public static byte[] sha1DigestFasta(File fastaFile) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        if (fastaFile != null && fastaFile.exists()) {
            digest.update(Files.readAllBytes(Paths.get(fastaFile.getAbsolutePath())));
            return digest.digest();
        } else {
            throw new IOException("file does not exist");
        }
    }

    public static byte[] sha1DigestFasta(String fastaFile) throws NoSuchAlgorithmException, IOException {
        if (fastaFile != null) {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            digest.update(fastaFile.getBytes());
            return digest.digest();
        } else {
            throw new IOException("string could not be digested");
        }

    }

    public static byte[] sha1DigestFasta(ByteBuffer input) throws NoSuchAlgorithmException, IOException {
        if (input != null) {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            digest.update(input);
            return digest.digest();

        } else {
            throw new IOException("could not be digested");
        }
    }

    public static DigestInputStream md5DigestInputStream(InputStream aStream) throws NoSuchAlgorithmException {
        return new DigestInputStream(aStream, MessageDigest.getInstance("MD5"));
    }

    public static DigestInputStream sha1DigestInputStream(InputStream aStream) throws NoSuchAlgorithmException {
        return new DigestInputStream(aStream, MessageDigest.getInstance("SHA1"));
    }
}
