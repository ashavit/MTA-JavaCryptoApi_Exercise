import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

public class BaseRunner {
    public KeyStore getKeystore() {
        return keystore;
    }

    public void setKeystore(KeyStore keystore) {
        this.keystore = keystore;
    }

    public MessageConfiguration getMessageConfiguration() {
        return messageConfiguration;
    }

    public void setMessageConfiguration(MessageConfiguration messageConfiguration) {
        this.messageConfiguration = messageConfiguration;
    }

    public KeyStore keystore;
    public MessageConfiguration messageConfiguration;

    protected void loadKeyStore(String keyStoreFile, String keyStorePass) throws Exception {
        FileInputStream ksInputStream = null;
        try {
            ksInputStream = new FileInputStream(keyStoreFile);
            /// TODO: Choose store provider
            keystore = KeyStore.getInstance("JKS", "SUN");
            keystore.load(ksInputStream, keyStorePass.toCharArray());
            ksInputStream.close();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (NoSuchAlgorithmException | NoSuchProviderException | KeyStoreException | CertificateException e) {
            throw new Exception(e.getMessage());
        }
    }
}
