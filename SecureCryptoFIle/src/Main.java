import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOpt;

import java.util.Arrays;
import java.util.Objects;


public class Main {


    public static void runDecryptor(String[] args) throws Exception {
        GetOpt getOpt = new GetOpt(args, "k:p:e:c:a:s:r:");

        String keyStoreFile = null;
        String keyStorePass = null;
        String encryptedFile = null;
        String encryptedFileConf = null;
        String privateKeyAlias = null;
        String privateKeyPass = null;
        String publicKeyAlias = null;

        int c;
        try {
            while ((c = getOpt.getNextOption()) != -1) {
                switch (c) {
                    case 'k': // Keystore file
                        keyStoreFile = getOpt.getOptionArg();
                        break;
                    case 'p': // Keystore password
                        keyStorePass = getOpt.getOptionArg();
                        break;
                    case 's': // get keyPass for private key
                        privateKeyPass = getOpt.getOptionArg();
                        break;
                    case 'a': // get alias to the key
                        privateKeyAlias = getOpt.getOptionArg();
                        break;
                    case 'e': // plain text file to encrypt
                        encryptedFile = getOpt.getOptionArg();
                        break;
                    case 'c': // encrypted file to output
                        encryptedFileConf = getOpt.getOptionArg();
                        break;
                    case 'r':
                        publicKeyAlias = getOpt.getOptionArg();
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown argument: -" + c);
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        if (isNullOrEmpty(keyStoreFile) || isNullOrEmpty(keyStorePass) ||
                isNullOrEmpty(privateKeyPass) || isNullOrEmpty(privateKeyAlias) ||
                isNullOrEmpty(encryptedFile) || isNullOrEmpty(encryptedFileConf)) {
            throw new IllegalArgumentException("One or more of the arguments are bad or missing");
        }


        System.out.println("Starting decryption...");
        new DecryptRunner(keyStoreFile, keyStorePass, encryptedFile, encryptedFileConf, privateKeyAlias, privateKeyPass, publicKeyAlias).start();
    }

    public static void runEncryptor(String[] args) throws Exception {
        GetOpt getOpt = new GetOpt(args, "k:p:s:a:e:c:t:r:");
        String keyStoreFile = null;
        String keyStorePass = null;

        String plainTextFile = null;
        String encryptedOutput = null;

        String senderPrivateKeyAlias = null;
        String senderPrivateKeyPass = null;
        String receiverPublicKey = null;

        int c;
        try {
            while ((c = getOpt.getNextOption()) != -1) {
                switch (c) {
                    case 'k': // Keystore file
                        keyStoreFile = getOpt.getOptionArg();
                        break;
                    case 'p': // Keystore password
                        keyStorePass = getOpt.getOptionArg();
                        break;
                    case 't': // plain text file to encrypt
                        plainTextFile = getOpt.getOptionArg();
                        break;
                    case 'e': // encrypted file to output
                        encryptedOutput = getOpt.getOptionArg();
                        break;
                    case 'a':
                        senderPrivateKeyAlias = getOpt.getOptionArg();
                        break;
                    case 's': // encrypted file to output
                        senderPrivateKeyPass = getOpt.getOptionArg();
                        break;
                    case 'r':
                        receiverPublicKey = getOpt.getOptionArg();
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown argument: -" + c);
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        if (isNullOrEmpty(keyStoreFile) || isNullOrEmpty(keyStorePass) ||
                isNullOrEmpty(plainTextFile) || isNullOrEmpty(encryptedOutput) ||
                isNullOrEmpty(senderPrivateKeyAlias) || isNullOrEmpty(senderPrivateKeyPass)) {
            throw new IllegalArgumentException("One or more of the arguments are bad or missing");
        }

        System.out.println("Starting encryption...");
        new EncryptRunner(keyStoreFile, keyStorePass, plainTextFile, encryptedOutput, senderPrivateKeyAlias, senderPrivateKeyPass, receiverPublicKey).start();
    }

    public static void main(String[] args) throws Exception {
        String mode = args[0];

        try {
            args = Arrays.copyOfRange(args, 1, args.length);
            if (Objects.equals(mode, "encrypt")) {
                runEncryptor(args);
            } else if (Objects.equals(mode, "decrypt")) {
                runDecryptor(args);
            } else {
                System.out.println("Please enter the required mode to be used encrypt or decrypt");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.toString());

            System.out.println("Please use of of the follow modes:");
            System.out.println("\t encrypt");
            System.out.println("\t decrypt");

            System.out.println("Please use the follow arguments:");
            System.out.println("\t -k = keystore to be used");
            System.out.println("\t -p = password for the key store");

            System.out.println("--Encryption Options");
            System.out.println("\t -t = plain text messageConfiguration.");
            System.out.println("\t -e = enctypted file name.");
            System.out.println("\t -a = private key alias for the signature.");
            System.out.println("\t -s = private key password for the signature.");
            System.out.println("\t -r = public key alias for key encryption.");


            System.out.println("--Decryption Options");
            System.out.println("\t -e = encrypted file path.");
            System.out.println("\t -c = encrypted configuration file path.");
            System.out.println("\t -a = private key alias for the signature.");
            System.out.println("\t -s = private key password for key decryption.");
            System.out.println("\t -r = public key alias for signature verify.");

        }

    }

    private static boolean isNullOrEmpty(String str) {
        return (null == str || str.isEmpty());
    }

}
