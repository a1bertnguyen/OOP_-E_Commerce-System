package Model;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Constructs a user object.
 * 
 * @param userId           Must be unique, format: u_10 digits, such as
 *                         u_1234567890
 * @param userName         The user's name
 * @param userPassword     The user's password
 * @param userRegisterTime Format: "DD-MM-YYYY_HH:MM:SS"
 * @param userRole         Default value: "customer"
 */
public abstract class User {
    protected String userId;
    protected String userName;
    protected String userPassword;
    protected String userRegisterTime;
    protected String userRole;

    public User(String userId, String userName, String userPassword,
            String userRegisterTime, String userRole) {
        this.userId = userId;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userRegisterTime = userRegisterTime;
        this.userRole = userRole;
    }

    /**
     * Default constructor
     */
    public User() {
        this.userId = "u_0000000000";
        this.userName = "default";
        this.userPassword = "123456";
        this.userRegisterTime = getCurrentTime();
        this.userRole = "customer";
    }

    private String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
        return formatter.format(new Date());
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserRegisterTime() {
        return userRegisterTime;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setUserRegisterTime(String userRegisterTime) {
        this.userRegisterTime = userRegisterTime;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    /**
     * Returns the user Information as a formatted string.
     * 
     * @return String in JSON-like format
     */
    @Override
    public String toString() {
        // Return in format: {"user_id":"u_1234567890", "user_name":"xxx",
        // "user_password":"xxx", "user_register_time":"xxx",
        // "user_role":"customer"}
        return String.format(
                "{\"user_id\":\"%s\", \"user_name\":\"%s\", \"user_password\":\"%s\", \"user_register_time\":\"%s\", \"user_role\":\"%s\"}",
                userId, userName, userPassword, userRegisterTime, userRole);
    }
}
