package securetext.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.MessageDigest;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 *
 * Philip Rodriguez;
 * 
 * This project was started on 9/19/2017 at 12:41 and is intended to be finished
 * in literally an hour to an hour and a half. The end goal is an executable
 * jar file with a very simple GUI that asks for a filename upon running. If that
 * file exists, then it will ask for the decryption password. If it does not,
 * it will create the file and ask for the encryption password. Very simple.
 * It will have a save shortcut, and auto save on close, and undo.
 * 
 * This class specifically will handle encryption and file management stuff.
 */
public class SecureTextManager {
    private final File file;
    private final String hashedPassword;
    
    private long lastSave;
    
    public SecureTextManager(File file, String password) throws Exception
    {
        //Hash that password!
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(password.getBytes("UTF-8"));
        this.hashedPassword = String.format("%064x", new java.math.BigInteger(1, hashBytes));
        
        this.file = file;
        checkFile();
        
        //We "last saved" when we opened it in that the file content as is has
        //been saved to disk...
        lastSave = System.currentTimeMillis();
    }
    
    public File getFile()
    {
        return file;
    }
    
    public void updateLastSave()
    {
        this.lastSave = System.currentTimeMillis();
    }
    
    public long getLastSave()
    {
        return lastSave;
    }
    
    //Ensure that the file exists...
    private void checkFile() throws Exception
    {
        if (this.file.exists())
        {
            //Alright, ensure we have the correct password for it!
            if (this.file.length() == 0)
            {
                //It's empty, so just initialize it.
                saveContent("");
            }
            else
            {
                //We want an exception if the password is wrong
                loadContent();
            }
        }
        else
        {
            //Create it! Also, save nothing to it for setup...
            file.createNewFile();
            saveContent("");
        }
    }
    
    /*
        These two methods are pretty self explanatory. One returns the current
        file content assuming it can (correct password, etc) and one overwrites
        the file content with the current password applied to the content string
    */
    public String loadContent() throws Exception
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        StringBuilder fileContent = new StringBuilder();
        int nextChar;
        while((nextChar = br.read()) != -1)
        {
            fileContent.append((char)nextChar);
        }
        br.close();
        //Decrypt
        StandardPBEStringEncryptor ste = new StandardPBEStringEncryptor();
        ste.setPassword(hashedPassword);
        ste.setAlgorithm("PBEWITHSHA1ANDDESEDE");
        String decrypted = ste.decrypt(fileContent.toString());
        if (!decrypted.startsWith(hashedPassword))
            throw new Exception("Decryption check failed!");
        return decrypted.substring(hashedPassword.length());
    }
    
    public void saveContent(String content) throws FileNotFoundException
    {
        StandardPBEStringEncryptor ste = new StandardPBEStringEncryptor();
        ste.setPassword(hashedPassword);
        ste.setAlgorithm("PBEWITHSHA1ANDDESEDE");
        String save = ste.encrypt(hashedPassword + content);
        PrintWriter pw = new PrintWriter(file);
        pw.print(save);
        pw.close();
    }
}
