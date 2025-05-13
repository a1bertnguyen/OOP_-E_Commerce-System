package Operation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import Model.Customer;

public class CustomerOperation {
    private static CustomerOperation instance = null;
    private static final String FILE = "src/Resources/data.txt";

    private UserOperation userOperation; //Dependecy on UserOperation

    private CustomerOperation() {
        this.userOperation = UserOperation.getInstance();
    }

    public static synchronized  CustomerOperation getInstance() {
        if (instance == null) {
            instance = new CustomerOperation();
        }
        return instance;
    }

    public boolean validateEmail(String userEmail) {
        if (userEmail == null || userEmail.trim().isEmpty()) {
            return false;
        }
        
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.matches(emailRegex, userEmail);
    }

    public boolean validateMobile(String userMobile) {
        if (userMobile == null || userMobile.trim().isEmpty()) {
            return false; 
        }
       
        String mobileRegex = "^(04|03)\\d{8}$";
        return Pattern.matches(mobileRegex, userMobile);
    }

    public boolean registerCustomer(String userName, String userPassword, 
    String userEmail, String userMobile) {
        if (!userOperation.validateUsername(userName)) {
            System.err.println("Sign up for failure: The username is invalid.");
            return (Boolean) null;
        }
        if (userOperation.checkUsernameExist(userName)) {
            System.err.println("Sign up for failure: Username '" + userName + "' exists.");
            return (Boolean) null;
        }
        if (!userOperation.validatePassword(userPassword)) {
            System.err.println("Sign up for failure: Valid password.");
            return (Boolean) null;
        }
        
        // Check email and phone
        if (!validateEmail(userEmail)) {
            System.err.println("Sign up for failure: invalid email.");
            return (Boolean) null;
        }
        if (!validateMobile(userMobile)) {
            System.err.println("Sign up for failure: invalid phone number.");
            return (Boolean) null;
        }

        
        String userId = userOperation.generateUniqueUserId();
        String encryptedPassword = userOperation.encryptPassword(userPassword);
        String registerTime = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").format(new Date());

        Customer newCustomer = new Customer(userId, userName, encryptedPassword, 
                        registerTime, "customer", userEmail, userMobile);

        userOperation.addUser(newCustomer);

        return newCustomer;
    }

    public boolean updateProfile(String attributeName, String value, Customer customerObject) {
        if (customerObject == null || attributeName == null || value == null) {
            System.err.println("Defeat update: Input data is invalid (NULL).");
            return false;
        }

        boolean isValidUpdate = false; 
        String originalUsername = customerObject.getUserName(); // BackupBackup

        
        switch (attributeName.toLowerCase()) { 
            case "username" -> {
                if (!value.equalsIgnoreCase(originalUsername) && userOperation.checkUsernameExist(value)) {
                    System.err.println("Update failure: Username'" + value + "' existed.");
                    return false; 
                }
               
                if (userOperation.validateUsername(value)) {
                    customerObject.setUserName(value);
                    isValidUpdate = true; 
                } else {
                    System.err.println("Failed update: The new username is invalid.");
                    return false;
                }
            }
            case "userpassword" -> {
                if (userOperation.validatePassword(value)) {
                    customerObject.setUserPassword(userOperation.encryptPassword(value));
                    isValidUpdate = true; 
                } else {
                    System.err.println("Failed update: The new password is invalid.");
                    return false; 
                }
            }
            case "useremail" -> {

                if (validateEmail(value)) {
                    customerObject.setUserEmail(value); 
                    isValidUpdate = true;
                } else {
                    System.err.println("Failed update: The new email is invalid.");
                    return false; 
                }
            }
            case "usermobile" -> {
                if (validateMobile(value)) {
                    customerObject.setUserMobile(value);
                    isValidUpdate = true; 
                } else {
                    System.err.println("Failed update: The new mobile phone is invalid.");
                    return false;
                }
            }
            default -> {
                System.err.println("Defeat update: attributes '" + attributeName + "' no support or no existence.");
                return false; 
            }
        }

        if (isValidUpdate) {
            return userOperation.updateUser(customerObject);
        }

        return false;
    }

    /**
    * Delete the customer from the data/users.txt file based on the
    * provided customer_id.
    * @param customerId The ID of the customer to delete
    * @return true if deleted, false if failed
    */
    public boolean deleteCustomer(String customerId) {
    // Implementation
    }

    /**
    * Retrieve one page of customers from the data/users.txt.
    * One page contains a maximum of 10 customers.
    * @param pageNumber The page number to retrieve
    * @return A List of Customer objects, the current page number, and total
    pages
    */
    public CustomerListResult getCustomerList(int pageNumber) {
    // Implementation
    }

    /**
    * Removes all the customers from the data/users.txt file.
    */
    public void deleteAllCustomers() {
    // Implementation
    }


}
