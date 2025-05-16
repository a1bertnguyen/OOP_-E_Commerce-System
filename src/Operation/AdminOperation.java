package Operation;


import Model.Admin;
import java.text.SimpleDateFormat;
import java.util.*;

public class AdminOperation {

    private static AdminOperation instance =null;
    private final UserOperation userOperation;

    private AdminOperation() {
        this.userOperation = UserOperation.getInstance();
    }

    /**
     * Returns the single instance of AdminOperation.
     * @return AdminOperation instance
     */
    public static AdminOperation getInstance() {
        if (instance == null) {
            instance = new AdminOperation();
        }
        return instance;
    }

    /**
     * Creates an admin account. This function should be called when
     * the system starts. The same admin account should not be
     * registered multiple times.
     */

    
    public boolean registerAdmin(String userName, String userPassword) {
        if (!userOperation.validateUsername(userName)) {
            System.err.println("Admin registration failed: Invalid username format.");
            return false;
        }
        if (userOperation.checkUsernameExist(userName)) {
            System.err.println("Admin registration failed: Username '" + userName + "' already exists.");
            return false;
        }
        // System.out.print("Enter admin password: "); // REMOVE THIS LINE
        if (!userOperation.validatePassword(userPassword)) {
            System.err.println("Admin registration failed: Invalid password format.");
            return false;
        }
        // ... rest of the method
        String userId = userOperation.generateUniqueUserId();
        String encryptedPassword = userOperation.encryptPassword(userPassword);
        String registerTime = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").format(new Date());

        Admin admin = new Admin(userId, userName, encryptedPassword, registerTime, "admin");
        boolean success = userOperation.addUser(admin);

        if (success) {
            System.out.println("Admin '" + userName + "' registered successfully with ID: " + admin.getUserId());
        } else {
            System.err.println("Failed to add admin '" + userName + "' to data file.");
        }
        return success;
    }
}
