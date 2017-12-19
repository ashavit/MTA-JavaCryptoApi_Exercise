import javax.crypto.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Scanner;

/**
 * Created by amirshavit on 12/16/17.
 */
public class EncryptRunner {

    private final String fileName;
    private KeyStore keystore;

    public EncryptRunner(String keyStoreFile, String keyStorePass, String plainTextFile, String encryptedOutput) throws Exception {
        this.fileName = plainTextFile;
        loadKeyStore(keyStoreFile, keyStorePass);
    }

    public void start() throws InvalidKeyException, IOException, NoSuchAlgorithmException, NoSuchPaddingException {

        scanFile(fileName);

    }


    private void loadKeyStore(String keyStoreFile, String keyStorePass) throws Exception {
        FileInputStream ksInputStream = null;
        try {
            ksInputStream = new FileInputStream(keyStoreFile);
            /// TODO: Choose store provider
            keystore = KeyStore.getInstance("JKS", "SUN");
            keystore.load(ksInputStream, keyStorePass.toCharArray());
            ksInputStream.close();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (NoSuchAlgorithmException | NoSuchProviderException | KeyStoreException | CertificateException e ) {
            throw new Exception(e.getMessage());
        }
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
