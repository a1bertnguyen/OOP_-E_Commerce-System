import java.util.List;
import java.util.Scanner;

public class  IOInterface {
    private static IOInterface instance;
    private Scanner scanner;

    private IOInterface() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns the single instance of IOInterface.
     * @return IOInterface instance
     */
    public static IOInterface getInstance() {
        if (instance == null) {
            instance = new IOInterface();
        }
        return instance;
    }

    /**
     * Accept user input.
     * @param message The message to display for input prompt
     * @param numOfArgs The number of arguments expected
     * @return An array of strings containing the arguments
     */
    public String[] getUserInput(String message, int numOfArgs) {
        System.out.print(message + " ");
        String inputLine = scanner.nextLine().trim();
        String[] inputs = inputLine.split("\\s+");
        String[] result = new String[numOfArgs];

        for (int i = 0; i < numOfArgs; i++) {
            result[i] = (i < inputs.length) ? inputs[i] : "";
        }
        return result;
    }

    /**
     * Display the login menu with options: (1) Login, (2) Register, (3) Quit.
     * The admin account cannot be registered.
     */
    public void mainMenu() {
        System.out.println("====== E-Commerce System ======");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Quit");
        System.out.println("===============================");
    }

    /**
     * Display the admin menu with options:
     * (1) Show products
     * (2) Add customers
     * (3) Show customers
     * (4) Show orders
     * (5) Generate test data
     * (6) Generate all statistical figures
     * (7) Delete all data
     * (8) Logout
     */
    public void adminMenu() {
        System.out.println("====== Admin Menu ======");
        System.out.println("1. Show products");
        System.out.println("2. Add customers");
        System.out.println("3. Show customers");
        System.out.println("4. Show orders");
        System.out.println("5. Generate test data");
        System.out.println("6. Generate all statistical figures");
        System.out.println("7. Delete all data");
        System.out.println("8. Logout");
        System.out.println("========================");
    }

    /**
     * Display the customer menu with options:
     * (1) Show profile
     * (2) Update profile
     * (3) Show products (user input could be "3 keyword" or "3")
     * (4) Show history orders
     * (5) Generate all consumption figures
     * (6) Logout
     */
    public void customerMenu() {
        System.out.println("====== Customer Menu ======");
        System.out.println("1. Show profile");
        System.out.println("2. Update profile");
        System.out.println("3. Show products (you can input \"3 keyword\" or \"3\")");
        System.out.println("4. Show history orders");
        System.out.println("5. Generate all consumption figures");
        System.out.println("6. Logout");
        System.out.println("===========================");
    }

    /**
     * Prints out different types of lists (Customer, Product, Order).
     * Shows row number, page number, and total page number.
     * @param userRole The role of the current user
     * @param listType The type of list to display
     * @param objectList The list of objects to display
     * @param pageNumber The current page number
     * @param totalPages The total number of pages
     */
    // In IOInterface.java
    public void showList(String userRole, String listType, List<?> objectList,
                        int pageNumber, int totalPages) {
        System.out.printf("====== %s List (Page %d/%d) ======\n", listType, pageNumber, totalPages);
        
        // Handle cases where there's no data or the current page is invalid/empty
        if (totalPages == 0) { // Implies no data at all for this list type
            System.out.println("No " + listType.toLowerCase() + " data found in the system.");
        } else if (objectList.isEmpty()) {
            System.out.println("No " + listType.toLowerCase() + " to display on this page.");
            if (pageNumber > totalPages || pageNumber < 1 && totalPages > 0) {
                System.out.println("Tip: Page number might be out of valid range (1-" + totalPages + ").");
            }
        } else {
            final int PAGE_SIZE = 10; // This should ideally be a shared constant
            int baseItemNumber = (pageNumber - 1) * PAGE_SIZE;
            for (int i = 0; i < objectList.size(); i++) {
                System.out.printf("%d. %s\n", baseItemNumber + i + 1, objectList.get(i).toString());
            }
        }
        System.out.println("==========================================");
    }

    /**
     * Prints out an error message and shows where the error occurred.
     * @param errorSource The source of the error
     * @param errorMessage The error message
     */
    public void printErrorMessage(String errorSource, String errorMessage) {
        System.err.printf("Error in %s: %s\n", errorSource, errorMessage);
    }

    /**
     * Print out the given message.
     * @param message The message to print
     */
    public void printMessage(String message) {
        System.out.println(message);
    }

    /**
     * Print out the object using the toString() method.
     * @param targetObject The object to print
     */
    public void printObject(Object targetObject) {
        if (targetObject == null) {
            System.out.println("null");
        } else {
            System.out.println(targetObject.toString());
        }
    }
}
