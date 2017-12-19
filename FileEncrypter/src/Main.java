import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOpt;

public class Main {

    public static void main(String[] args) throws Exception {

        GetOpt getOpt = new GetOpt(args, "k:p:t:e:");

        String keyStoreFile = null;
        String keyStorePass = null;
        String plainTextFile = null;
        String encryptedOutput = null;

        int c;
        try {
            while ((c = getOpt.getNextOption()) != -1)
            {
                switch(c)
                {
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
                    default:
                        throw new IllegalArgumentException("Unknown argument: -" + c);
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        if (isNullOrEmpty(keyStoreFile) || isNullOrEmpty(keyStorePass) ||
                isNullOrEmpty(plainTextFile) || isNullOrEmpty(encryptedOutput)) {
            throw new IllegalArgumentException("One or more of the arguments are bad or missing");
        }

        new EncryptRunner(keyStoreFile, keyStorePass, plainTextFile, encryptedOutput).start();
    }

    private static boolean isNullOrEmpty(String str) {
        return (null == str || str.isEmpty());
    }

}
