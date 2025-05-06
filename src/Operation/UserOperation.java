package Operation;

import Model.*;

import java.io.*;
import java.util.*;

public class UserOperation {

    /**
     * Returns the single instance of UserOperation.
     * 
     * @return UserOperation instance
     */
    private static UserOperation instance = null;
    private static final String FILE = "src/Resources/data.txt";

    // Singalton in JAVA
    public static UserOperation getInstance() {

        if (instance == null) {
            instance = new UserOperation();
        }

        return instance;
    }

    /**
     * Generates and returns a 10-digit unique user id starting with 'u_'
     * every time when a new user is registered.
     * 
     * @return A string value in the format 'u_10digits', e.g., 'u_1234567890'
     */

    // RES: https://www.geeksforgeeks.org/java-io-bufferedreader-class-java/
    public String generateUniqueUserId() {
        Random rand = new Random();
        String id;
        do {
            long number = 1000000000L + rand.nextLong(9000000000L);
            id = "u_" + number;
        } while (checkUserIdExist(id)); // true continue false break

        return id;

    }

    private boolean checkUserIdExist(String userId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) { // we use BufferedReader to read line
                                                                                 // instead of read per letters
            String line;
            while ((line = reader.readLine()) != null) { // read all line in the text
                if (line.contains("\"user_id\":\"" + userId + "\""))
                    return true;
            }
        } catch (IOException e) { // print the error
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Encode a user-provided password.
     * Encryption steps:
     * 
     * 1. Generate a random string with a length equal to two times
     * the length of the user-provided password. The random string
     * should consist of characters chosen from a-zA-Z0-9.
     * 
     * 2. Combine the random string and the input password text to
     * create an encrypted password, following the rule of selecting
     * two letters sequentially from the random string and
     * appending one letter from the input password. Repeat until all
     * characters in the password are encrypted. Finally, add "^^" at
     * the beginning and "$$" at the end of the encrypted password.
     *
     * @param userPassword The password to encrypt
     * @return Encrypted password
     */
    public String encryptPassword(String userPassword) {
        /*
         * ex:
         * user_password: abc
         * randompassword : Xp7Kq9 ( double lenght)
         * encrypted password: ^^Xpax7bKqc$$
         * description: Xp + a + 7K + b + q9 + c
         * 0 1 0^ 2 3 1^ 4 5 2^
         * ^: user password arrays
         */
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // random String
        Random rand = new Random();
        StringBuilder randomPassword = new StringBuilder();
        for (int i = 0; i < userPassword.length() * 2; i++) {
            randomPassword.append(chars.charAt(rand.nextInt(chars.length()))); // generate random password which each
                                                                               // letter user input have 2 random char

        }
        StringBuilder encrypted = new StringBuilder("^^");
        for (int i = 0; i < userPassword.length(); i++) {
            encrypted.append(randomPassword.charAt(i * 2));
            encrypted.append(randomPassword.charAt(i * 2 + 1));
            encrypted.append(userPassword.charAt(i));
        }
        encrypted.append("$$");
        return encrypted.toString();

    }

    /**
     * Decode the encrypted password with a similar rule as the encryption
     * method.
     * 
     * @param encryptedPassword The encrypted password to decrypt
     * @return Original user-provided password
     */

    public String decryptPassword(String encryptedPassword) {
        /*
         * encrypted password: ^^Xpax7bKqc$$
         * encrypted password: Xpax7bKqc
         * description: Xp + a + 7K + b + q9 + c
         * 0 1 0^ 2 3 1^ 4 5 2^
         * 0 1 2 3 4 5 6 7 8
         */
        if (!encryptedPassword.startsWith("^^") || !encryptedPassword.endsWith("$$"))
            return null;// verify the encrypted password
        encryptedPassword = encryptedPassword.substring(2, encryptedPassword.length() - 2); // subject ^^ and $$

        StringBuilder decrypted = new StringBuilder();
        for (int i = 2; i < encryptedPassword.length(); i += 3) { // we take the third arrays
            decrypted.append(encryptedPassword.charAt(i));
        }
        return decrypted.toString();
    }

    /**
     * Verify whether a user is already registered or exists in the system.
     * 
     * @param userName The username to check
     * @return true if exists, false otherwise
     */
    public boolean checkUsernameExist(String userName) {
        // similar with check UserID
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("\"user_name\":\"" + userName + "\""))
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Validate the user's name. The name should only contain letters or
     * underscores, and its length should be at least 5 characters.
     * 
     * @param userName The username to validate
     * @return true if valid, false otherwise
     */
    public boolean validateUsername(String userName) {
        if (userName == null || userName.length() < 5)
            return false;
        for (char c : userName.toCharArray()) {
            if (!(Character.isLetter(c))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validate the user's password. The password should contain at least
     * one letter (uppercase or lowercase) and one number. The length
     * must be greater than or equal to 5 characters.
     * 
     * @param userPassword The password to validate
     * @return true if valid, false otherwise
     */
    public boolean validatePassword(String userPassword) {
        if (userPassword == null || userPassword.length() < 5)
            return false;
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : userPassword.toCharArray()) {
            if (Character.isLetter(c))
                hasLetter = true;
            if (Character.isDigit(c))
                hasDigit = true;
        }
        return hasLetter && hasDigit;
    }

    /**
     * Verify the provided user's name and password combination against
     * stored user data to determine the authorization status.
     * 
     * @param userName     The username for login
     * @param userPassword The password for login
     * @return A User object (Customer or Admin) if successful, null otherwise
     */
    public User login(String userName, String userPassword) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("\"user_name\":\"" + userName + "\"")) // find correct user
                {
                    String encrypted = line.split("\"user_password\":\"")[1].split("\"")[0];
                    // take encrypted pass
                    // ex: "user_password":"^^a1b2c3$$", -> ^^a1b2c3$$
                    if (decryptPassword(encrypted).equals(userPassword)) { // decryptPassword
                        if (line.contains("\"user_role\":\"admin\"")) {
                            return parseAdmin(line);
                        } else {
                            return parseCustomer(line);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * {"user_id":"u_123","user_name":"minh"}
     */

    // CREATE THE ADMIN AND USER ROLE IN TXT.
    private Admin parseAdmin(String json) {
        String[] parts = json.replace("{", "").replace("}", "").split(",\s*");
        // parts = ["\"user_id\":\"u_123\"", "\"user_name\":\"minh\""]

        Map<String, String> map = new HashMap<>();
        for (String part : parts) {
            String[] kv = part.replace("\"", "") // "user_name:minh"
                    .split(":"); // ["user_name", "minh"]

            map.put(kv[0], kv[1]);
        }
        return new Admin(
                map.get("user_id"),
                map.get("user_name"),
                map.get("user_password"),
                map.get("user_register_time"),
                map.get("user_role"));
    }

    private Customer parseCustomer(String json) {
        String[] parts = json.replace("{", "").replace("}", "").split(",\s*");
        Map<String, String> map = new HashMap<>();
        for (String part : parts) {
            String[] kv = part.replace("\"", "").split(":");
            map.put(kv[0], kv[1]);
        }
        return new Customer(
                map.get("user_id"),
                map.get("user_name"),
                map.get("user_password"),
                map.get("user_register_time"),
                map.get("user_role"),
                map.get("user_email"),
                map.get("user_mobile"));
    }

}
