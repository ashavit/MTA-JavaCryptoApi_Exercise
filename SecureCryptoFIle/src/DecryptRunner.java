import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;

/**
 * Created by amirshavit on 12/16/17.
 */
public class DecryptRunner extends BaseRunner{

    private final String encryptedFile;

    private final String privateKeyAlias;
    private final String privateKeyPass;
    private final String publicKeyAlias;

    private SecretKey randomKey;
    private KeyGenerator keyGen;

    public DecryptRunner(String keyStoreFile,
                         String keyStorePass,
                         String encryptedFile,
                         String encryptedFileConf,
                         String privateKeyAlias,
                         String privateKeyPass,
                         String publicKeyAlias) throws Exception {


        loadKeyStore(keyStoreFile, keyStorePass);
        loadConfigurationFile(encryptedFileConf);

        this.privateKeyAlias = privateKeyAlias;
        this.privateKeyPass = privateKeyPass;
        this.publicKeyAlias = publicKeyAlias;

        this.encryptedFile = encryptedFile;

        this.keyGen = KeyGenerator.getInstance("AES");
        this.randomKey = keyGen.generateKey();

    }

    private void loadConfigurationFile(String encryptedFileConf) throws IOException {
        this.setMessageConfiguration(MessageConfiguration.fromFile(encryptedFileConf));
    }

    private byte[] decryptMessage() throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, UnrecoverableKeyException, IllegalBlockSizeException, InvalidSignature, KeyStoreException, SignatureException, InvalidKeyException, IOException, InvalidAlgorithmParameterException {
        // decrypt the messageConfiguration
        Cipher myCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        myCipher.init(Cipher.DECRYPT_MODE, this.decryptKey(), new IvParameterSpec(getMessageConfiguration().iv));
        /*
            System.out.println("Cipher: algo = " + myCipher.getAlgorithm() +
                ", provider = " + myCipher.getProvider() +
                ", iv = " + myCipher.getIV() +
                ", params = " + myCipher.getParameters() +
                ", blockSize = " + myCipher.getBlockSize());
        */
        // Reading encrypted messageConfiguration using CipherInputStream
        FileInputStream encryptedFis = new FileInputStream(new File(encryptedFile));
        CipherInputStream encryptedCis = new CipherInputStream(encryptedFis, myCipher);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;

        len = encryptedCis.read(buffer);
        while (len != -1) {
            baos.write(buffer, 0, len);
            len = encryptedCis.read(buffer);
        }


        encryptedCis.close();
        encryptedFis.close();
        return baos.toByteArray();
    }

    private SecretKeySpec decryptKey() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnrecoverableKeyException, KeyStoreException, SignatureException, InvalidSignature {
        // Decrypt the key
        Cipher decrypt = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        PrivateKey privateKey = (PrivateKey) getKeystore().getKey(privateKeyAlias, privateKeyPass.toCharArray());
        decrypt.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] aesKey = decrypt.doFinal(getMessageConfiguration().encryptedKey);
        System.out.println("Verifying Encryption Key Signature ...");
        verifyKey(aesKey);
        System.out.println("Successfully Verified Encryption Key Signature ...");

        return new SecretKeySpec(aesKey, "AES");
    }

    private void verifyMessage(byte[] msg) throws NoSuchAlgorithmException, KeyStoreException, SignatureException, InvalidKeyException, InvalidSignature, IOException {
        // Initial Signature object
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(getKeystore().getCertificate(publicKeyAlias));
        sig.update(msg);

        FileOutputStream fos = new FileOutputStream("dycrypted.txt");

        if (!sig.verify(getMessageConfiguration().messageSign)) {
            fos.write("Error: The encrypted messageConfiguration been compromised".getBytes());
            fos.close();
            throw new InvalidSignature("The encrypted messageConfiguration been compromised");
        }

        fos.write(msg);
        fos.close();

        System.out.println("Successfully Verified Message Signature ...");
    }

    private void verifyKey(byte[] aesKey) throws SignatureException, KeyStoreException, NoSuchAlgorithmException, InvalidKeyException, InvalidSignature {
        // Initial Signature object
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(getKeystore().getCertificate(publicKeyAlias));
        sig.update(aesKey);

        if (!sig.verify(getMessageConfiguration().encryptedKeySign)) {
            System.out.println("The encrypted key been compromised, Signature been changed.");
            throw new InvalidSignature("The encrypted messageConfiguration been compromised");
        }

    }

    public void start() throws InvalidKeyException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, UnrecoverableKeyException, KeyStoreException, SignatureException, BadPaddingException, IllegalBlockSizeException, InvalidSignature, InvalidAlgorithmParameterException {
        System.out.println("Decrypting Message...");
        byte[] plainMessage = decryptMessage();

        System.out.println("Verifying Message Signature ...");
        verifyMessage(plainMessage);

        System.out.println("Decryption Was finished successfully.");

        System.out.println("-------------------------------------");
        System.out.println(new String(plainMessage));
        System.out.println("-------------------------------------");
    }

}
