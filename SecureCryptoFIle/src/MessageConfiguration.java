import javax.xml.bind.JAXB;
import java.io.File;

public class MessageConfiguration {
    byte[] messageSign = null;
    byte[] encryptedKey = null;
    byte[] encryptedKeySign = null;
    byte[] encodedAlgParams = null;
    String publicKeyAlias = null;
    public String algorithm;
    public String provider;
    public byte[] iv;

    public byte[] getEncryptedKeySign() {
        return encryptedKeySign;
    }

    public void setEncryptedKeySign(byte[] encryptedKeySign) {
        this.encryptedKeySign = encryptedKeySign;
    }

    public byte[] getEncodedAlgParams() {
        return encodedAlgParams;
    }

    public void setEncodedAlgParams(byte[] encodedAlgParams) {
        this.encodedAlgParams = encodedAlgParams;
    }


    public MessageConfiguration() {
    }

    public static MessageConfiguration fromFile(String fileName) {
        File file = new File(fileName);
        return JAXB.unmarshal(file, MessageConfiguration.class);
    }

    public byte[] getMessageSign() {
        return messageSign;
    }

    public void setMessageSign(byte[] messageSign) {
        this.messageSign = messageSign;
    }

    public byte[] getEncryptedKey() {
        return encryptedKey;
    }

    public void setEncryptedKey(byte[] encryptedKey) {
        this.encryptedKey = encryptedKey;
    }

    public String getPublicKeyAlias() {
        return publicKeyAlias;
    }

    public void setPublicKeyAlias(String publicKeyAlias) {
        this.publicKeyAlias = publicKeyAlias;
    }
}
