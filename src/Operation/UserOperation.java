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

    // Trong UserOperation.java
//    public List<User> getAllUsers() {
//        List<String> allLines = loadAllLinesFromFile();
//        System.err.println("DEBUG getAllUsers: Number of lines loaded from file by loadAllLinesFromFile(): " + allLines.size()); // Thêm DEBUG
//
//        List<User> users = new ArrayList<>();
//        if (allLines.isEmpty()) {
//            System.err.println("DEBUG getAllUsers: allLines is empty. No data to parse."); // Thêm DEBUG
//            return users; // Trả về danh sách rỗng nếu không có dòng nào để đọc
//        }
//
//        int linesProcessed = 0;
//        int usersParsed = 0;
//
//        for (String line : allLines) {
//            linesProcessed++;
//            System.err.println("DEBUG getAllUsers: Processing line " + linesProcessed + ": [" + line + "]"); // Thêm DEBUG
//
//            if (line.trim().isEmpty()) { // Bỏ qua dòng trống
//                System.err.println("DEBUG getAllUsers: Skipping empty line.");
//                continue;
//            }
//
//            // Điều kiện xác định dòng user
//            boolean isUserLineCandidate = line.contains("\"user_id\":") && line.contains("\"user_name\":");
//            System.err.println("DEBUG getAllUsers: Is user line candidate? " + isUserLineCandidate); // Thêm DEBUG
//
//            if (isUserLineCandidate) {
//                Map<String, String> data = SimpleJsonParser.parse(line);
//                System.err.println("DEBUG getAllUsers: Parsed JSON-like data: " + data); // Thêm DEBUG
//
//                if (!data.isEmpty()) {
//                    String role = data.getOrDefault("user_role", "customer").toLowerCase();
//                    System.err.println("DEBUG getAllUsers: Detected role: " + role); // Thêm DEBUG
//                    try {
//                        User parsedUser = null;
//                        if ("admin".equals(role)) {
//                            parsedUser = parseAdmin(data);
//                        } else {
//                            parsedUser = parseCustomer(data);
//                        }
//
//                        if (parsedUser != null) {
//                            users.add(parsedUser);
//                            usersParsed++;
//                            System.err.println("DEBUG getAllUsers: Successfully parsed and added user: " + parsedUser.getUserName()); // Thêm DEBUG
//                        } else {
//                            System.err.println("DEBUG getAllUsers: parseAdmin/parseCustomer returned null for data: " + data); // Thêm DEBUG
//                        }
//                    } catch (IllegalArgumentException e) {
//                        System.err.println("DEBUG getAllUsers: IllegalArgumentException parsing user line: " + line + " - Error: " + e.getMessage());
//                    } catch (Exception e) {
//                        System.err.println("DEBUG getAllUsers: Unexpected Exception parsing user line: " + line + " - Error: " + e.getMessage());
//                        e.printStackTrace(); // In stack trace cho lỗi không mong muốn
//                    }
//                } else {
//                    System.err.println("DEBUG getAllUsers: SimpleJsonParser.parse(line) returned empty map for line: " + line); // Thêm DEBUG
//                }
//            }
//        }
//        System.err.println("DEBUG getAllUsers: Finished processing. Total lines processed: " + linesProcessed + ". Total users parsed and added: " + usersParsed); // Thêm DEBUG
//        if (users.isEmpty() && linesProcessed > 0) {
//            System.err.println("DEBUG getAllUsers: Processed " + linesProcessed + " lines but no users were successfully parsed and added to the list!");
//        }
//        return users;
//    }
    
    // RES: https://www.geeksforgeeks.org/java-io-bufferedreader-class-java/
    public String generateUniqueUserId() {
        List<User> existingUsers = getAllUsers(); 
        Set<String> existingIds = existingUsers.stream()
                                                .map(User::getUserId)
                                                .collect(Collectors.toSet());
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
        if (userId == null || userId.trim().isEmpty()) return false;
        return getAllUsers().stream()
                            .anyMatch(user -> user.getUserId().equals(userId.trim()));
    }

    public boolean checkUsernameExist(String userName) {
        if (userName == null || userName.trim().isEmpty()) return false;
        return getAllUsers().stream() // Joint in Python
                            .anyMatch(user -> user.getUserName().equalsIgnoreCase(userName.trim())); //Lambda in Python
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
         if (userName == null || userPassword == null) return null;
         List<User> users = getAllUsers();
         for (User user : users) {
             if (user.getUserName().equalsIgnoreCase(userName)) {
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

//    public User login(String userName, String userPassword) {
//        // Đề xuất kiểm tra đầu vào nghiêm ngặt hơn
//        if (userName == null || userName.trim().isEmpty() ||
//            userPassword == null || userPassword.trim().isEmpty()) { // Kiểm tra cả userPassword rỗng
//            System.err.println("DEBUG: Login attempt with empty username or password.");
//            return null;
//        }
//
//        List<User> users = getAllUsers();
//        if (users.isEmpty()) {
//            System.err.println("DEBUG: No users loaded from file.");
//            return null;
//        }
//
//        for (User user : users) {
//            // Nên trim() userName lấy từ file nếu có khả năng nó có khoảng trắng thừa
//            if (user.getUserName().equalsIgnoreCase(userName.trim())) { // Trim userName từ input
//                String storedEncryptedPassword = user.getUserPassword();
//                String storedDecryptedPassword = decryptPassword(storedEncryptedPassword);
//
//                // Debugging output
//                System.err.println("DEBUG: Attempting login for: " + userName.trim());
//                System.err.println("DEBUG: User from file: " + user.getUserName());
//                System.err.println("DEBUG: Input password: [" + userPassword + "]"); // userPassword ở đây là mật khẩu gốc người dùng nhập
//                System.err.println("DEBUG: Stored encrypted: [" + storedEncryptedPassword + "]");
//                System.err.println("DEBUG: Stored decrypted: [" + storedDecryptedPassword + "]");
//
//                if (storedDecryptedPassword != null && storedDecryptedPassword.equals(userPassword)) {
//                    System.err.println("DEBUG: Password match! Login successful for " + userName.trim());
//                    return user;
//                } else {
//                    // LỖI LOGIC TIỀM NĂNG Ở ĐÂY:
//                    // Nếu mật khẩu không khớp cho user đầu tiên có username trùng,
//                    // bạn trả về null ngay lập tức mà không kiểm tra các user khác
//                    // (dù trường hợp có nhiều user cùng username là không nên có).
//                    // Tuy nhiên, nếu chỉ có một user với username đó, thì return null là đúng.
//                    // Nếu bạn đảm bảo username là duy nhất, thì logic này là ổn.
//                    System.err.println("DEBUG: Password mismatch for " + userName.trim());
//                    // return null; // BỎ return null ở đây nếu bạn muốn nó tiếp tục vòng lặp (dù ít có khả năng)
//                                // Giữ lại nếu username là duy nhất.
//                }
//            }
//        }
//        // Chỉ trả về null sau khi đã duyệt qua tất cả user mà không tìm thấy khớp username VÀ password
//        System.err.println("DEBUG: User " + userName.trim() + " not found or password did not match.");
//        return null;
//    }

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

        // Check duplicate username
         if (checkUsernameExist(userToUpdate.getUserName())) {
             User existingUserWithSameName = getAllUsers().stream()
                 .filter(u -> u.getUserName().equalsIgnoreCase(userToUpdate.getUserName()))
                 .findFirst().orElse(null);
             if (existingUserWithSameName != null && !existingUserWithSameName.getUserId().equals(userToUpdate.getUserId())) {
                 usernameConflict = true;
                 System.err.println("Failed Update: Username '" + userToUpdate.getUserName() + "' has been used by another user.");
             }
         }

         if(usernameConflict){
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
                 outputLines.add(line); // Keep itit
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
