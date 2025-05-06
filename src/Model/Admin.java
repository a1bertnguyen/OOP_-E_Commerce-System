package Model;

public class Admin extends User {
    /**
     * Constructs an admin object.
     * 
     * @param userId           Must be unique, format: u_10 digits, such as
     *                         u_1234567890
     * @param userName         The user's name
     * @param userPassword     The user's password
     * @param userRegisterTime Format: "DD-MM-YYYY_HH:MM:SS"
     * @param userRole         Default value: "admin"
     */
    public Admin(String userId, String userName, String userPassword,
            String userRegisterTime, String userRole) {
        super(userId, userName, userPassword, userRegisterTime, userRole);

    }

    /**
     * Default constructor
     */
    public Admin() {
        super("u_0000000001", "admin", "admin123", "01-01-2000_00:00:00", "admin");
    }

    /**
     * Returns the admin's attributes as a formatted string.
     * 
     * @return String in JSON-like format
     */
    @Override
    public String toString() {
        // Return in format: {"user_id":"u_1234567890", "user_name":"xxx",
        // "user_password":"xxx", "user_register_time":"xxx",
        // "user_role":"admin"}
        return String.format("{\"user_id\":\"%s\", \"user_name\":\"%s\", \"user_password\":\"%s\", " +
                "\"user_register_time\":\"%s\", \"user_role\":\"%s\"}",
                userId, userName, userPassword, userRegisterTime, userRole);
    }
}
