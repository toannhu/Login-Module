/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manager;

import java.lang.IllegalArgumentException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author toannd4
 */
public class PasswordManager {

    private String username;
    private String encryptedPassword;

    // Define the BCrypt workload to use when generating password hashes. 10-31 is a valid value.
    private static int workload = 12;
    private static PasswordManager INSTANCE = null;

    public static PasswordManager getInstance() {
        if (INSTANCE == null) {
            synchronized (PasswordManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PasswordManager();
                }
            }
        }
        return INSTANCE;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.encryptedPassword;
    }
    
    public void setPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public void clear() {
        this.username = null;
        this.encryptedPassword = null;
    }

    /**
     * This method can be used to generate a string representing an account
     * password suitable for storing in a database. It will be an OpenBSD-style
     * crypt(3) formatted hash string of length=60 The bcrypt workload is
     * specified in the above static variable, a value from 10 to 31. A workload
     * of 12 is a very reasonable safe default as of 2013. This automatically
     * handles secure 128-bit salt generation and storage within the hash.
     *
     * @param plainPassword The account's plaintext password as provided during
     * account creation, or when changing an account's password.
     * @return String - a string of length 60 that is the bcrypt hashed password
     * in crypt(3) format.
     */
    protected String encrypted(String plainPassword) {
        String salt = BCrypt.gensalt(workload);
        String hashedPassword = BCrypt.hashpw(plainPassword, salt);
        return hashedPassword;
    }

    /**
     * This method can be used to verify a computed hash from a plaintext (e.g.
     * during a login request) with that of a stored hash from a database. The
     * password hash from the database must be passed as the second variable.
     *
     * @param plainPassword The account's plaintext password, as provided during
     * a login request
     * @param storedHash The account's stored password hash, retrieved from the
     * authorization database
     * @return boolean - true if the password matches the password of the stored
     * hash, false otherwise
     */
    protected boolean verifyPassword(String plainPassword) {
        String storedHash = this.encryptedPassword;
        boolean passwordVerified = false;
        if (null == storedHash || !storedHash.startsWith("$2a$")) {
            throw new IllegalArgumentException("Invalid hash provided for comparison!");
        }
        passwordVerified = BCrypt.checkpw(plainPassword, storedHash);
        return passwordVerified;
    }

    public boolean validatePassword(String plainPassword) {
        Pattern p = Pattern.compile("^(?!.*[\\s])(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{6,}$");
        Matcher m = p.matcher(plainPassword);
        if (m.matches()) {
            return true;
        }
        return false;
    }

    public boolean setNewPassword(String plainPassword) {
        if (validatePassword(plainPassword)) {
            this.encryptedPassword = encrypted(plainPassword);
            if (this.encryptedPassword != null && !this.encryptedPassword.isEmpty()) {
                return true;
            }
        }
        return false;
    }

}
