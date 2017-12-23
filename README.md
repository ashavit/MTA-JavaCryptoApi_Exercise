# MTA-JavaCryptoApi_Exercise
Java CryptoAPI exercise project for MTA's "Building Secure Applications" class

Running Example:
```
encrypt -k KEYSTORE -p KEYSTORE_PASSWORD -a PRIVATE_KEY_ALIAS -s PRIVATE_KEY_PASS -r RECIVER_PUBLIC_KEY -t PLAINTEXT_FILE -e ENCRYPTED_FILE_OUTPUT
```

```
encrypt -k /home/yurir/IdeaProjects/MTA-JavaCryptoApi_Exercise/Keystores/keystore_yuri.jks -p ab123456 -a yuri_ritvin -s yuri24 -t plaintext.txt -e penc -r amir_imported
decrypt -k /home/yurir/IdeaProjects/MTA-JavaCryptoApi_Exercise/Keystores/keystore_amir.jks -p ab123456 -a amir_shavit -s amir135 -e penc.msg -c penc.xml -r yuri_imported
```

Command Line Information

```
Please use of of the follow modes:
    encrypt
    decrypt
    
Please use the follow arguments:


    -k = keystore to be used 
    -p = password for the key store
 
--Encryption Options

	 
    -t = plain text messageConfiguration.
    -e = enctypted file name.
    -a = private key alias for the signature.
    -s = private key password for the signature.
    -r = public key alias for key encryption.
	 
	 
--Decryption Options
	 
    -e = encrypted file path.
    -c = encrypted configuration file path.
    -a = private key alias for the signature.
    -s = private key password for key decryption.
    -r = public key alias for signature verify.`
    
```