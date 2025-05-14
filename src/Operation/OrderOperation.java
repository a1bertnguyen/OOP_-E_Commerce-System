package Operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Model.Order;
import Model.OrderListResult;
import Model.Product;
import util.FileUtil;
import util.SimpleJsonParser;

public class OrderOperation {
    /**
    * Returns the single instance of OrderOperation.
    * @return OrderOperation instance
    */
    private static OrderOperation instance = null;
    private OrderOperation() {
        FileUtil.ensureDataFileExists();
    }

    public static OrderOperation getInstance() {
        if (instance == null) {
            instance = new OrderOperation();
        }
        return instance;
    }

    private Order parseOrder(Map<String, String> data) {
        if (data == null || data.isEmpty()) {
            return null;
        } 
        try {
            return new Order(
                data.get("order_id"),
                data.get("user_id"),
                data.get("pro_id"),
                data.get("order_time")
            );
        } catch (NumberFormatException e) {
            System.err.println("Parse error for Order data: " + data + " - " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("The error is not determined when parse order data: " + data + " - " + e.getMessage());
            return null;
        }
    }

    private List<Order> getAllOrdersFromFile() {
        List<String> allLines = FileUtil.readLines(FileUtil.DATA_FILE);
        List<Order> orders = new ArrayList<>();
        for (String line : allLines) {
            if (line.contains("\"order_id\":")) {
                Map<String, String> data = SimpleJsonParser.parse(line);
                Order order = parseOrder(data);
                if (order != null) {
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    /**
    * Generates and returns a 5-digit unique order id starting with "o_".
    * @return A string such as o_12345
    */      
    public String generateUniqueOrderId() {
        String orderId = "o_";
        int randomNum = (int) (Math.random() * 90000) + 10000; // Generates a number between 10000 and 99999
        orderId += randomNum;
        return orderId; 
    }
    /**
    * Creates a new order with a unique order id and saves it to the
    * data/orders.txt file.
    * @param customerId The ID of the customer making the order
    * @param productId The ID of the product being ordered
    * @param createTime The time of order creation (null for current time)
    * @return true if successful, false otherwise
    */
    public boolean createAnOrder(String customerId, String productId, String createTime) {
    
    }
    /**
    * Deletes the order from the data/orders.txt file based on order_id.
    * @param orderId The ID of the order to delete
    * @return true if successful, false otherwise
    */
    public boolean deleteOrder(String orderId) {
        
    }
    /**
    * Retrieves one page of orders from the database belonging to the
    * given customer. One page contains a maximum of 10 items.
    * @param customerId The ID of the customer
    * @param pageNumber The page number to retrieve
    * @return A list of Order objects, current page number, and total pages
    */
    public OrderListResult getOrderList(String customerId, int pageNumber) {
    // Implementation
    }
    /**
    * Automatically generates test data including customers and orders.
    * Creates 10 customers and randomly generates 50-200 orders for each.
    * Order times should be scattered across different months of the year.
    */
    public void generateTestOrderData() {
    // Implementation
    }
    /**
    * Generates a chart showing the consumption (sum of order prices)
    * across 12 different months for the given customer.
    * @param customerId The ID of the customer
    */
    public void generateSingleCustomerConsumptionFigure(String customerId) {
    // Implementation using Java charting library
    }
    /**
    * Generates a chart showing the consumption (sum of order prices)
    * across 12 different months for all customers.
    */
    public void generateAllCustomersConsumptionFigure() {
    // Implementation using Java charting library
    }
    /**
    * Generates a graph showing the top 10 best-selling products
    * sorted in descending order.
    */
    public void generateAllTop10BestSellersFigure() {
    // Implementation using Java charting library
    }
    /**
    * Removes all data in the data/orders.txt file.
    */
    public void deleteAllOrders() {
    // Implementation
    }
}
