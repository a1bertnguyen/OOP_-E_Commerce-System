package Operation;


import Model.Admin;
import util.FileUtil;
import util.SimpleJsonParser;
import Model.Customer;
import Model.CustomerListResult;
import Model.User;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import java.util.List;

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
                System.err.println("Invalid username");
                return false;
            }

            if (userOperation.checkUsernameExist(userName)) {
                System.err.println(" Username '" + userName + "' already exists.");
                return false;
            }

            System.out.print("Enter admin password: ");

            if (!userOperation.validatePassword(userPassword)) {
                System.err.println(" Invalid password.");
                return false;
            }

            String userId = userOperation.generateUniqueUserId();
            String encryptedPassword = userOperation.encryptPassword(userPassword);
            String registerTime = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").format(new Date());

            Admin admin = new Admin(userId, userName, encryptedPassword, registerTime, "admin");

            boolean success = userOperation.addUser(admin);

            if (success) {
                System.out.println("Admin '" + userName + "' registered successfully.");
            } else {
                System.err.println("Failed to register admin.");
            }


        return success;
    }
}
