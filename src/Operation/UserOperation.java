package Operation;

import Model.*;
import java.util.*;
import java.util.stream.Collectors;
import util.FileUtil; 
import util.SimpleJsonParser;

public class UserOperation {
    private static UserOperation instance = null;
    private final Random random = new Random();

    private UserOperation() {
        FileUtil.ensureDataFileExists();
    }

    // Singalton in JAVA
    public static synchronized UserOperation getInstance() {
        if (instance == null) {
            instance = new UserOperation();
        }

        return instance;
    }

    // Read with no parse
    private List<String> loadAllLinesFromFile() {
        return FileUtil.readLines(FileUtil.DATA_FILE);
    }

//     Take list Users
    public List<User> getAllUsers() {
        List<String> allLines = loadAllLinesFromFile();
        List<User> users = new ArrayList<>();
        for (String line : allLines) {
            // Identify the line is User (must have user_id and user_name)
            if (line.contains("\"user_id\":") && line.contains("\"user_name\":")) {
                Map<String, String> data = SimpleJsonParser.parse(line);
                if (!data.isEmpty()) {
                    String role = data.getOrDefault("user_role", "customer").toLowerCase();
                    try {
                        if ("admin".equals(role)) {
                            users.add(parseAdmin(data));
                        } else {
                            users.add(parseCustomer(data));
                        }
                    } catch (Exception e) {
                        System.err.println("Ignore the user line parse error: " + line + " Error: " + e.getMessage());
                    }
                }
            }
        }
        return users;
    }

    // RES: https://www.geeksforgeeks.org/java-io-bufferedreader-class-java/
    public String generateUniqueUserId() {
        List<User> existingUsers = getAllUsers();
        Set<String> existingIds = new HashSet<>();

        for (User user : existingUsers) {
            existingIds.add(user.getUserId());
        }

        String newId;
        do {
            long number = 1_000_000_000L + random.nextLong(9_000_000_000L);
            newId = "u_" + number;
        } while (existingIds.contains(newId));
        return newId;

    }

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

         // [a-zA-z_][0-9]
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

    // Convert to string and check with the value import
    private boolean checkUserIdExist(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return false;
        }

        String trimmedUserId = userId.trim();

        List<User> users = getAllUsers();

        for (User user : users) {
            if (user.getUserId().equals(trimmedUserId)) {
                return true;
            }
        }

        return false;
    }

    public boolean checkUsernameExist(String userName) {

        if (userName == null || userName.trim().isEmpty()) {
            return false;
        }

        String trimmedUserName = userName.trim();


        List<User> users = getAllUsers();

        for (User user : users) {
            if (user.getUserName().equalsIgnoreCase(trimmedUserName)) {
                return true;
            }
        }

        return false;
    }

    public boolean validateUsername(String userName) {
        if (userName == null || userName.length() < 5) return false;
        return userName.matches("^[a-zA-Z_]{5,}$");
    }

    public boolean validatePassword(String userPassword) {
        if (userPassword == null || userPassword.length() < 5) return false;
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : userPassword.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (hasLetter && hasDigit) return true;
        }
        return false;
    }

    public User login(String userName, String userPassword) {

        if (userName == null || userName.trim().isEmpty() || userPassword == null) {
            return null;
        }

        String normalizedUserName = userName.trim();

        for (User user : getAllUsers()) {
            if (user.getUserName().equalsIgnoreCase(normalizedUserName)) {
                String storedDecryptedPassword = decryptPassword(user.getUserPassword());
                if (storedDecryptedPassword != null && storedDecryptedPassword.equals(userPassword)) {
                    return user;
                } else {
                    return null;
                }
            }
        }


        return null;
    }


    /*
     * {"user_id":"u_123","user_name":"minh"}
     */

    // CREATE THE ADMIN AND USER ROLE.
     private Admin parseAdmin(Map<String, String> data) {
        String userId = data.get("user_id");
        String userName = data.get("user_name");
        String password = data.get("user_password");
        String regTime = data.get("user_register_time");
        String role = data.getOrDefault("user_role", "admin");
        if (userId == null || userName == null || password == null || regTime == null) {
            throw new IllegalArgumentException("Missing information for Admin: " + data);
        }
        return new Admin(userId, userName, password, regTime, role);
    }

    private Customer parseCustomer(Map<String, String> data) {
        String userId = data.get("user_id");
        String userName = data.get("user_name");
        String password = data.get("user_password");
        String regTime = data.get("user_register_time");
        String role = data.getOrDefault("user_role", "customer");
        String email = data.getOrDefault("user_email", "");
        String mobile = data.getOrDefault("user_mobile", "");
         if (userId == null || userName == null || password == null || regTime == null) {
            throw new IllegalArgumentException("Missing information for Customer: " + data);
        }
        return new Customer(userId, userName, password, regTime, role, email, mobile);
    }

    
    public boolean addUser(User user) {
        if (user == null) return false;

        if (checkUserIdExist(user.getUserId()) || checkUsernameExist(user.getUserName())) {
             System.err.println("Add user: ID or username existed.");
             return false;
        }
        String userLine = user.toString(); 
        FileUtil.writeLines(FileUtil.DATA_FILE, Collections.singletonList(userLine), true); // true = append
        return true; 
    }

    // Read -> filter -> Write all file
    public boolean updateUser(User userToUpdate) {
        if (userToUpdate == null) return false;

        List<String> allLines = loadAllLinesFromFile();
        List<String> outputLines = new ArrayList<>();
        boolean foundAndReplaced = false;
        boolean usernameConflict = false;


        if (checkUsernameExist(userToUpdate.getUserName())) {

            User existingUserWithSameName = null;
            List<User> allUsers = getAllUsers();

            for (User u : allUsers) {
                if (u.getUserName().equalsIgnoreCase(userToUpdate.getUserName())) {
                    existingUserWithSameName = u;
                    break;
                }
            }


            if (existingUserWithSameName != null &&
                    !existingUserWithSameName.getUserId().equals(userToUpdate.getUserId())) {
                usernameConflict = true;
                System.err.println("Failed Update: Username '" + userToUpdate.getUserName() +
                        "' has been used by another user.");
            }
        }

        if (usernameConflict) {
            return false;
        }

        for (String line : allLines) {
            if (line.contains("\"user_id\":") && line.contains("\"user_name\":")) {
                Map<String, String> data = SimpleJsonParser.parse(line);
                String currentUserId = data.get("user_id");
                if (currentUserId != null && currentUserId.equals(userToUpdate.getUserId())) {
                    outputLines.add(userToUpdate.toString());
                    foundAndReplaced = true;
                } else {
                    outputLines.add(line);
                }
            } else {
                outputLines.add(line);
            }
        }

        if (foundAndReplaced) {
            FileUtil.writeLines(FileUtil.DATA_FILE, outputLines, false); // false = overwrite
        } else {
            System.err.println("Update error: No user ID found: " + userToUpdate.getUserId());
        }
        return foundAndReplaced;
    }

    public boolean deleteUser(String userIdToDelete) {
        if (userIdToDelete == null || userIdToDelete.trim().isEmpty()) return false;

        List<String> allLines = loadAllLinesFromFile();
        List<String> outputLines = new ArrayList<>();
        boolean foundAndDeleted = false;

        for (String line : allLines) {
            boolean shouldKeepLine = true;
            if (line.contains("\"user_id\":") && line.contains("\"user_name\":")) {
                Map<String, String> data = SimpleJsonParser.parse(line);
                String currentUserId = data.get("user_id");

                if (currentUserId != null && currentUserId.equals(userIdToDelete)) {
                    shouldKeepLine = false; //Mark to do not keep this line
                    foundAndDeleted = true;
                }
            }
            if (shouldKeepLine) {
                outputLines.add(line); // Keep it
            }
        }

        if (foundAndDeleted) {
            FileUtil.writeLines(FileUtil.DATA_FILE, outputLines, false); // false = overwrite
        } else {
            System.err.println("Error deletion: No user ID found:" + userIdToDelete);
        }
        return foundAndDeleted;
    }
}


