import Operation.*;
import Model.*;
import java.util.List;

public class ECommerceSystem {
    private static IOInterface ioInterface;
    private static UserOperation userOperation;
    private static AdminOperation adminOperation;
    private static CustomerOperation customerOperation;
    private static ProductOperation productOperation;
    private static OrderOperation orderOperation;
    
    private static User currentUser = null;

    public static void main(String[] args) {
        // Initialize all operations
        ioInterface = IOInterface.getInstance();
        userOperation = UserOperation.getInstance();
        adminOperation = AdminOperation.getInstance();
        customerOperation = CustomerOperation.getInstance();
        productOperation = ProductOperation.getInstance();
        orderOperation = OrderOperation.getInstance();
        
        // Main application loop
        boolean running = true;
        while (running) {
            ioInterface.mainMenu();
            String[] choice = ioInterface.getUserInput("Enter your choice:", 1);
            
            switch (choice[0]) {
                case "1":
                    handleLogin();
                    break;
                case "2":
                    handleRegister();
                    break;
                case "3":
                    running = false;
                    ioInterface.printMessage("Thank you for using E-Commerce System!");
                    break;
                default:
                    ioInterface.printErrorMessage("Main Menu", "Invalid choice. Please try again.");
            }
        }
    }
    
    private static void handleLogin() {
        String[] credentials = ioInterface.getUserInput("Enter username and password:", 2);
        String username = credentials[0];
        String password = credentials[1];
        
        currentUser = userOperation.login(username, password);
        
        if (currentUser != null) {
            ioInterface.printMessage("Login successful. Welcome, " + currentUser.getUserName() + "!");
            
            if (currentUser instanceof Admin) {
                handleAdminMenu();
            } else if (currentUser instanceof Customer) {
                handleCustomerMenu();
            }
        } else {
            ioInterface.printErrorMessage("Login", "Invalid username or password.");
        }
    }
    
    private static void handleRegister() {
        String[] userInfo = ioInterface.getUserInput("Enter username, password, email, and mobile:", 4);
        String username = userInfo[0];
        String password = userInfo[1];
        String email = userInfo[2];
        String mobile = userInfo[3];
        
        boolean success = customerOperation.registerCustomer(username, password, email, mobile);
        if (success) {
            ioInterface.printMessage("Registration successful!");
        }
    }
    
    private static void handleAdminMenu() {
        boolean adminRunning = true;
        while (adminRunning) {
            ioInterface.adminMenu();
            String[] choice = ioInterface.getUserInput("Enter your choice:", 1);
            
            switch (choice[0]) {
                case "1":
                    showProductsPaginated();
                    break;
                case "2":
                    addCustomer();
                    break;
                case "3":
                    showCustomersPaginated();
                    break;
                case "4":
                    showOrdersPaginated();
                    break;
                case "5":
                    generateTestData();
                    break;
                case "6":
                    generateAllStatisticalFigures();
                    break;
                case "7":
                    deleteAllData();
                    break;
                case "8":
                    adminRunning = false;
                    currentUser = null;
                    ioInterface.printMessage("Logged out successfully.");
                    break;
                default:
                    ioInterface.printErrorMessage("Admin Menu", "Invalid choice. Please try again.");
            }
        }
    }
    
    private static void handleCustomerMenu() {
        boolean customerRunning = true;
        while (customerRunning) {
            ioInterface.customerMenu();
            String[] input = ioInterface.getUserInput("Enter your choice:", 2);
            String choice = input[0];
            
            switch (choice) {
                case "1":
                    showProfile();
                    break;
                case "2":
                    updateProfile();
                    break;
                case "3":
                    if (input.length > 1 && !input[1].isEmpty()) {
                        showProductsByKeyword(input[1]);
                    } else {
                        showProductsPaginated();
                    }
                    break;
                case "4":
                    showCustomerOrders();
                    break;
                case "5":
                    generateCustomerConsumptionFigures();
                    break;
                case "6":
                    customerRunning = false;
                    currentUser = null;
                    ioInterface.printMessage("Logged out successfully.");
                    break;
                default:
                    ioInterface.printErrorMessage("Customer Menu", "Invalid choice. Please try again.");
            }
        }
    }
    
    private static void showProductsPaginated() {
        int currentPage = 1;
        boolean viewing = true;
        
        while (viewing) {
            ProductListResult result = productOperation.getProductList(currentPage);
            ioInterface.showList(currentUser.getUserRole(), "Product", result.getProducts(), 
                              result.getCurrentPage(), result.getTotalPages());
            
            ioInterface.printMessage("Page " + result.getCurrentPage() + " of " + result.getTotalPages());
            String[] input = ioInterface.getUserInput("Enter 'n' for next page, 'p' for previous page, or 'b' to go back:", 1);
            
            switch (input[0].toLowerCase()) {
                case "n":
                    if (currentPage < result.getTotalPages()) {
                        currentPage++;
                    } else {
                        ioInterface.printMessage("Already on last page.");
                    }
                    break;
                case "p":
                    if (currentPage > 1) {
                        currentPage--;
                    } else {
                        ioInterface.printMessage("Already on first page.");
                    }
                    break;
                case "b":
                    viewing = false;
                    break;
                default:
                    ioInterface.printMessage("Invalid option.");
            }
        }
    }
    
    private static void showCustomersPaginated() {
        int currentPage = 1;
        boolean viewing = true;
        
        while (viewing) {
            CustomerListResult result = customerOperation.getCustomerList(currentPage);
            ioInterface.showList(currentUser.getUserRole(), "Customer", result.getCustomers(), 
                              result.getCurrentPage(), result.getTotalPages());
            
            ioInterface.printMessage("Page " + result.getCurrentPage() + " of " + result.getTotalPages());
            String[] input = ioInterface.getUserInput("Enter 'n' for next page, 'p' for previous page, 'b' to go back, or a customer ID to view details:", 1);
            
            switch (input[0].toLowerCase()) {
                case "n":
                    if (currentPage < result.getTotalPages()) {
                        currentPage++;
                    } else {
                        ioInterface.printMessage("Already on last page.");
                    }
                    break;
                case "p":
                    if (currentPage > 1) {
                        currentPage--;
                    } else {
                        ioInterface.printMessage("Already on first page.");
                    }
                    break;
                case "b":
                    viewing = false;
                    break;
                default:
                    // Handle customer ID input for details
                    ioInterface.printMessage("Customer details feature not implemented yet.");
            }
        }
    }
    
    private static void showOrdersPaginated() {
        // For admin, show all orders - this would need to be implemented in OrderOperation
        ioInterface.printMessage("Admin order viewing not fully implemented yet.");
    }
    
    private static void addCustomer() {
        String[] userInfo = ioInterface.getUserInput("Enter username, password, email, and mobile:", 4);
        String username = userInfo[0];
        String password = userInfo[1];
        String email = userInfo[2];
        String mobile = userInfo[3];
        
        boolean success = customerOperation.registerCustomer(username, password, email, mobile);
        if (success) {
            ioInterface.printMessage("Customer added successfully!");
        }
    }
    
    private static void generateTestData() {
        ioInterface.printMessage("Generating test data...");
        orderOperation.generateTestOrderData();
        ioInterface.printMessage("Test data generated successfully!");
    }
    
    private static void generateAllStatisticalFigures() {
        ioInterface.printMessage("Generating all statistical figures...");
        
        // Product figures
        productOperation.generateCategoryFigure();
        productOperation.generateDiscountFigure();
        productOperation.generateLikesCountFigure();
        productOperation.generateDiscountLikesCountFigure();
        
        // Order figures
        orderOperation.generateAllCustomersConsumptionFigure();
        orderOperation.generateAllTop10BestSellersFigure();
        
        ioInterface.printMessage("All statistical figures generated successfully!");
    }
    
    private static void deleteAllData() {
        String[] confirmation = ioInterface.getUserInput("Are you sure you want to delete all data? (yes/no):", 1);
        if ("yes".equalsIgnoreCase(confirmation[0])) {
            orderOperation.deleteAllOrders();
            productOperation.deleteAllProducts();
            customerOperation.deleteAllCustomers();
            ioInterface.printMessage("All data deleted successfully!");
        } else {
            ioInterface.printMessage("Operation cancelled.");
        }
    }
    
    private static void showProfile() {
        ioInterface.printObject(currentUser);
    }
    
    private static void updateProfile() {
        String[] input = ioInterface.getUserInput("Enter attribute name and new value:", 2);
        String attribute = input[0];
        String value = input[1];
        
        if (currentUser instanceof Customer) {
            Customer customer = (Customer) currentUser;
            boolean success = customerOperation.updateProfile(attribute, value, customer);
            if (success) {
                ioInterface.printMessage("Profile updated successfully!");
                // Refresh current user
                currentUser = userOperation.login(currentUser.getUserName(), 
                    userOperation.decryptPassword(currentUser.getUserPassword()));
            }
        }
    }
    
    private static void showProductsByKeyword(String keyword) {
        List<Product> products = productOperation.getProductListByKeyword(keyword);
        ioInterface.printMessage("=== Products containing \"" + keyword + "\" ===");
        for (int i = 0; i < products.size(); i++) {
            ioInterface.printMessage((i + 1) + ". " + products.get(i).toString());
        }
        if (products.isEmpty()) {
            ioInterface.printMessage("No products found containing \"" + keyword + "\"");
        }
    }
    
    private static void showCustomerOrders() {
        int currentPage = 1;
        boolean viewing = true;
        
        while (viewing) {
            OrderListResult result = orderOperation.getOrderList(currentUser.getUserId(), currentPage);
            ioInterface.showList(currentUser.getUserRole(), "Order", result.getOrders(), 
                              result.getCurrentPage(), result.getTotalPages());
            
            if (result.getTotalPages() > 0) {
                ioInterface.printMessage("Page " + result.getCurrentPage() + " of " + result.getTotalPages());
                String[] input = ioInterface.getUserInput("Enter 'n' for next page, 'p' for previous page, or 'b' to go back:", 1);
                
                switch (input[0].toLowerCase()) {
                    case "n":
                        if (currentPage < result.getTotalPages()) {
                            currentPage++;
                        } else {
                            ioInterface.printMessage("Already on last page.");
                        }
                        break;
                    case "p":
                        if (currentPage > 1) {
                            currentPage--;
                        } else {
                            ioInterface.printMessage("Already on first page.");
                        }
                        break;
                    case "b":
                        viewing = false;
                        break;
                    default:
                        ioInterface.printMessage("Invalid option.");
                }
            } else {
                viewing = false;
            }
        }
    }
    
    private static void generateCustomerConsumptionFigures() {
        ioInterface.printMessage("Generating customer consumption figures...");
        orderOperation.generateSingleCustomerConsumptionFigure(currentUser.getUserId());
        ioInterface.printMessage("Customer consumption figures generated successfully!");
    }
}