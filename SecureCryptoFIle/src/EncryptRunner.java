import javax.crypto.*;
import javax.xml.bind.JAXB;
import java.io.*;
import java.security.*;
import java.util.Scanner;

/**
 * Created by amirshavit on 12/16/17.
 */
public class EncryptRunner extends BaseRunner{

    private final String plainInputFileName;
    private final String encryptedOutputFileName;

    private final String senderPrivateKeyAlias;
    private final String senderPrivateKeyPass;
    private final String recevierPublicKeyAlias;

    private SecretKey randomKey;
    private KeyGenerator keyGen;

    public EncryptRunner(String keyStoreFile,
                         String keyStorePass,
                         String plainTextFile,
                         String encryptedOutput,
                         String senderPrivateKeyAlias,
                         String senderPrivateKeyPass,
                         String recevierPublicKeyAlias) throws Exception {


        loadKeyStore(keyStoreFile, keyStorePass);

        this.senderPrivateKeyAlias = senderPrivateKeyAlias;
        this.senderPrivateKeyPass = senderPrivateKeyPass;
        this.recevierPublicKeyAlias = recevierPublicKeyAlias;

        this.plainInputFileName = plainTextFile;
        this.encryptedOutputFileName = encryptedOutput;

        this.setMessageConfiguration(new MessageConfiguration());

        this.keyGen = KeyGenerator.getInstance("AES");
        this.randomKey = keyGen.generateKey();

    }

    private void signaturePlainText() throws InvalidKeyException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, IOException, SignatureException {
        //  Side A, Sign's the plaintext with her private key
        Signature messageSign = Signature.getInstance("SHA1withRSA");
        PrivateKey priv = (PrivateKey) getKeystore().getKey(senderPrivateKeyAlias, senderPrivateKeyPass.toCharArray());
        messageSign.initSign(priv);

        FileInputStream datafis = new FileInputStream(plainInputFileName);
        BufferedInputStream bufin = new BufferedInputStream(datafis);

        byte[] buffer = new byte[1024];
        int len;
        while (bufin.available() != 0) {
            len = bufin.read(buffer);
            messageSign.update(buffer, 0, len);
        }
        bufin.close();

        messageConfiguration.messageSign = messageSign.sign();
    }

    private void signatureEncryptionKey() throws KeyStoreException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnrecoverableKeyException, SignatureException {
        // Calculating Signature for the symmetric key we generate.
        Signature encryptedKeySign = Signature.getInstance("SHA1withRSA");
        PrivateKey priv = (PrivateKey) keystore.getKey(senderPrivateKeyAlias, senderPrivateKeyPass.toCharArray());
        encryptedKeySign.initSign(priv);
        encryptedKeySign.update(randomKey.getEncoded());
        messageConfiguration.encryptedKeySign = encryptedKeySign.sign();
    }


    private void encryptPlainText() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IOException {
        // using AES - CBC encryption mode with random generated IV
        Cipher myCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        myCipher.init(Cipher.ENCRYPT_MODE, this.randomKey);

        System.out.println("KeyGenerator: algo = " + keyGen.getAlgorithm() + ", provider = " + keyGen.getProvider());
        System.out.println("AES Key: algo = " + randomKey.getAlgorithm() + ", format = " + randomKey.getFormat());
        System.out.println("Cipher: algo = " + myCipher.getAlgorithm() +
                ", provider = " + myCipher.getProvider() +
                ", iv = " + myCipher.getIV() +
                ", params = " + myCipher.getParameters() +
                ", blockSize = " + myCipher.getBlockSize());

        FileInputStream fis;
        FileOutputStream fos;
        CipherInputStream cis;
        fis = new FileInputStream(plainInputFileName);
        cis = new CipherInputStream(fis, myCipher);
        fos = new FileOutputStream(encryptedOutputFileName+ ".msg");

        byte[] b = new byte[8];
        int i = cis.read(b);
        while (i != -1) {
            fos.write(b, 0, i);
            i = cis.read(b);
        }

        fos.close();
        cis.close();
        fis.close();

        AlgorithmParameters algParams = myCipher.getParameters();
        messageConfiguration.provider = algParams.getProvider().getName();
        messageConfiguration.iv = myCipher.getIV();
        messageConfiguration.algorithm = algParams.getAlgorithm();
        messageConfiguration.encodedAlgParams = algParams.getEncoded();
    }

    private void encryptKey() throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, KeyStoreException, BadPaddingException, IllegalBlockSizeException {
        // Alice encrypt symmetric key K (getEncoded) with bob's asymetric public key (another cipher engine)
        PublicKey publicKey = keystore.getCertificate(recevierPublicKeyAlias).getPublicKey();
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        messageConfiguration.encryptedKey = cipher.doFinal(randomKey.getEncoded());
    }

    private void writeXml() throws IOException {
        FileOutputStream fos;
        fos = new FileOutputStream(encryptedOutputFileName + ".xml");
        JAXB.marshal(messageConfiguration, fos);
        fos.close();
    }

    public void start() throws InvalidKeyException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, UnrecoverableKeyException, KeyStoreException, SignatureException, BadPaddingException, IllegalBlockSizeException {

        System.out.println("Plain Text Message is:");
        scanFile(plainInputFileName);

        System.out.println("Sign the plain text message...");
        signaturePlainText();

        System.out.println("Encrypting the plain text message...");
        encryptPlainText();

        System.out.println("Sign the symmetric key...");
        signatureEncryptionKey();

        System.out.println("Encrypting the symmetric key...");
        encryptKey();

        System.out.println("Writing to xml configuration file...");
        writeXml();

        System.out.println("Done.");
    }

    private void scanFile(String aFileName) {
        File file = new File(aFileName);

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println(line);
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
