package Operation;

import Model.Customer;
import Model.CustomerListResult;
import Model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class CustomerOperation {
    private static CustomerOperation instance = null;

    private final UserOperation userOperation;

    private CustomerOperation() {
        this.userOperation = UserOperation.getInstance();
    }

    public static synchronized CustomerOperation getInstance() {
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
        String originalUsername = customerObject.getUserName();

        switch (attributeName.toLowerCase()) {
            case "username":
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
                break;

            case "userpassword":
                if (userOperation.validatePassword(value)) {
                    customerObject.setUserPassword(userOperation.encryptPassword(value));
                    isValidUpdate = true;
                } else {
                    System.err.println("Failed update: The new password is invalid.");
                    return false;
                }
                break;

            case "useremail":
                if (validateEmail(value)) {
                    customerObject.setUserEmail(value);
                    isValidUpdate = true;
                } else {
                    System.err.println("Failed update: The new email is invalid.");
                    return false;
                }
                break;

            case "usermobile":
                if (validateMobile(value)) {
                    customerObject.setUserMobile(value);
                    isValidUpdate = true;
                } else {
                    System.err.println("Failed update: The new mobile phone is invalid.");
                    return false;
                }
                break;

            default:
                System.err.println("Defeat update: attributes '" + attributeName + "' no support or no existence.");
                return false;
        }

        if (isValidUpdate) {
            return userOperation.updateUser(customerObject);
        }

        return false;
    }

    public boolean deleteCustomer(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            System.err.println("Deletion failed: Invalid customer ID provided.");
            return false;
        }
        String trimmedId = customerId.trim();

        User userToDelete = null;
        List<User> allUsers = userOperation.getAllUsers();
        for (User u : allUsers) {
            if (u.getUserId().equals(trimmedId)) {
                userToDelete = u;
                break;
            }
        }

        if (userToDelete == null) {
            System.err.println("Deletion failed: User with ID '" + trimmedId + "' not found.");
            return false;
        }

        if (!(userToDelete instanceof Customer)) {
            System.err.println("Deletion failed: User with ID '" + trimmedId + "' is not a customer.");
            return false;
        }

        boolean success = userOperation.deleteUser(trimmedId);
        if (!success) {
            System.err.println("Deletion failed: Could not remove customer '" + trimmedId + "' from data file.");
        } else {
            System.out.println("Customer with ID '" + trimmedId + "' deleted successfully.");
        }
        return success;
    }

    public CustomerListResult getCustomerList(int pageNumber) {
        final int PAGE_SIZE = 10;
        List<User> allUsers = userOperation.getAllUsers();
        List<Customer> allCustomers = new ArrayList<>();

        for (User user : allUsers) {
            if (user instanceof Customer) {
                allCustomers.add((Customer) user);
            }
        }

        if (allCustomers.isEmpty()) {
            return new CustomerListResult(new ArrayList<>(), 0, 0);
        }

        int totalCustomers = allCustomers.size();
        int totalPages = (int) Math.ceil((double) totalCustomers / PAGE_SIZE);

        if (pageNumber < 1 || pageNumber > totalPages) {
            return new CustomerListResult(new ArrayList<>(), pageNumber, totalPages);
        }

        int startIndex = (pageNumber - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalCustomers);

        List<Customer> pageOfCustomers = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            pageOfCustomers.add(allCustomers.get(i));
        }

        return new CustomerListResult(pageOfCustomers, pageNumber, totalPages);
    }

    public void deleteAllCustomers() {
        List<User> allUsers = userOperation.getAllUsers();
        List<Customer> customersToDelete = new ArrayList<>();

        for (User user : allUsers) {
            if (user instanceof Customer) {
                customersToDelete.add((Customer) user);
            }
        }

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
