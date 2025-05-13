package Operation;

import Model.Customer;
import Model.CustomerListResult;
import Model.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
            return false;
        }
        if (userOperation.checkUsernameExist(userName)) {
            System.err.println("Sign up for failure: Username '" + userName + "' exists.");
            return false;
        }
        if (!userOperation.validatePassword(userPassword)) {
            System.err.println("Sign up for failure: Valid password.");
            return false;
        }
        
        // Check email and phone
        if (!validateEmail(userEmail)) {
            System.err.println("Sign up for failure: invalid email.");
            return false;
        }
        if (!validateMobile(userMobile)) {
            System.err.println("Sign up for failure: invalid phone number.");
            return false;
        }

        
        String userId = userOperation.generateUniqueUserId();
        String encryptedPassword = userOperation.encryptPassword(userPassword);
        String registerTime = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").format(new Date());

        Customer newCustomer = new Customer(userId, userName, encryptedPassword, 
                        registerTime, "customer", userEmail, userMobile);

        boolean success = userOperation.addUser(newCustomer);
        if (success) {
            System.out.println("Customer '" + userName + "' registered successfully.");
        } else {
            System.err.println("Registration failed: Could not add customer to data file.");
        }
        return success;
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
        if (customerId == null || customerId.trim().isEmpty()) {
            System.err.println("Deletion failed: Invalid customer ID provided.");
            return false;
        }
        String trimmedId = customerId.trim();

        // Verify the user exists and is actually a Customer before deleting
        User userToDelete = userOperation.getAllUsers().stream()
                .filter(u -> u.getUserId().equals(trimmedId))
                .findFirst()
                .orElse(null);

        if (userToDelete == null) {
            System.err.println("Deletion failed: User with ID '" + trimmedId + "' not found.");
            return false;
        }

        if (!(userToDelete instanceof Customer)) {
            System.err.println("Deletion failed: User with ID '" + trimmedId + "' is not a customer.");
            return false;
        }

        // Delegate the file deletion (read-filter-overwrite) to UserOperation
        boolean success = userOperation.deleteUser(trimmedId);
         if (!success) {
             // UserOperation's deleteUser already prints an error if not found during its process
              System.err.println("Deletion failed: Could not remove customer '" + trimmedId + "' from data file.");
         } else {
              System.out.println("Customer with ID '" + trimmedId + "' deleted successfully.");
         }
        return success;
    }

    /**
    * Retrieve one page of customers from the data/users.txt.
    * One page contains a maximum of 10 customers.
    * @param pageNumber The page number to retrieve
    * @return A List of Customer objects, the current page number, and total
    pages
    */
    public CustomerListResult getCustomerList(int pageNumber) {
        final int PAGE_SIZE = 10;

        // Get all users from UserOperation 
        List<User> allUsers = userOperation.getAllUsers();

        // Filter to get only Customer objects
        List<Customer> allCustomers = allUsers.stream()
                .filter(user -> user instanceof Customer)
                .map(user -> (Customer) user)
                .collect(Collectors.toList());

        // Handle empty customer list
        if (allCustomers.isEmpty()) {
            return new CustomerListResult(new ArrayList<>(), 0, 0);
        }

        // Calculate pagination details
        int totalCustomers = allCustomers.size();
        int totalPages = (int) Math.ceil((double) totalCustomers / PAGE_SIZE);

        // Validate pageNumber
        if (pageNumber < 1 || pageNumber > totalPages) {
            // Return result indicating invalid page, but provide total pages info
            return new CustomerListResult(new ArrayList<>(), pageNumber, totalPages);
        }

        // Calculate indices for the sublist
        int startIndex = (pageNumber - 1) * PAGE_SIZE;
        // endIndex is exclusive, ensure it doesn't exceed list size
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalCustomers);

        // Get the sublist for the requested page
        List<Customer> pageOfCustomers = allCustomers.subList(startIndex, endIndex);

        // Return the result object
        return new CustomerListResult(pageOfCustomers, pageNumber, totalPages);
    }

    /**
    * Removes all the customers from the data/users.txt file.
    */
    public void deleteAllCustomers() {
        List<User> allUsers = userOperation.getAllUsers();
        List<Customer> customersToDelete = allUsers.stream()
                .filter(user -> user instanceof Customer)
                .map(user -> (Customer) user)
                .collect(Collectors.toList());

        if (customersToDelete.isEmpty()) {
            System.out.println("No customers found to delete.");
            return;
        }

        System.out.println("Attempting to delete " + customersToDelete.size() + " customers...");
        int successfulDeletions = 0;

        for (Customer customer : customersToDelete) {
            if (userOperation.deleteUser(customer.getUserId())) {
                successfulDeletions++;
            }
        }

        System.out.println("Successfully deleted " + successfulDeletions + " out of " + customersToDelete.size() + " customers.");
    }

}
