package Operation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import Model.Order;
import Model.OrderListResult;
import Model.Product;
import util.FileUtil;
import util.SimpleJsonParser;

import java.time.Month;


public class OrderOperation {
    /**
    * Returns the single instance of OrderOperation.
    * @return OrderOperation instance
    */
    private static OrderOperation instance = null;

    private List<Order> orders;
    private List<Product> products;

    private OrderOperation() {
        FileUtil.ensureDataFileExists();
        reloadOrdersFromFile();
        this.products = new ArrayList<>();
    }

    private void reloadOrdersFromFile() {
        this.orders = getAllOrdersFromFile();
    }

    public static OrderOperation getInstance() {
        if (instance == null) {
            instance = new OrderOperation();
        }
        return instance;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
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
        String orderId = generateUniqueOrderId();
        String time = createTime != null ? createTime : java.time.LocalDateTime.now().toString();
        List<String> updatedLines = new ArrayList<>();

        Order newOrder = new Order(orderId, customerId, productId, time);
        
        updatedLines.add(newOrder.toString());
        List<String> linesToAdd = new ArrayList<>();
        linesToAdd.add(newOrder.toString());
        try {
            FileUtil.writeLines(FileUtil.DATA_FILE, linesToAdd, true);
            reloadOrdersFromFile();
            return true;
        } catch (Exception e) {
            System.err.println("Failed to create order: " + e.getMessage());
            return false;
        }
    }

    /**
    * Deletes the order from the data/orders.txt file based on order_id.
    * @param orderId The ID of the order to delete
    * @return true if successful, false otherwise
    */
    public boolean deleteOrder(String orderId) {
        List<String> allLines = FileUtil.readLines(FileUtil.DATA_FILE);
        boolean deleted = false;
        List<String> updatedLines = new ArrayList<>();

        for (String line : allLines) {
            if (line.contains("\"order_id\":\"" + orderId + "\"")) {
                deleted = true;
            } else {
                updatedLines.add(line);
            }
        }

        if (deleted) {
            FileUtil.writeLines(FileUtil.DATA_FILE, updatedLines, false);
            reloadOrdersFromFile();
        }

        return deleted;
    }

    /**
    * Retrieves one page of orders from the database belonging to the
    * given customer. One page contains a maximum of 10 items.
    * @param customerId The ID of the customer
    * @param pageNumber The page number to retrieve
    * @return A list of Order objects, current page number, and total pages
    */
    public OrderListResult getOrderList(String customerId, int pageNumber) {
        List<Order> customerOrders = orders.stream()
                                    .filter(order -> order.getUserId().equals(customerId))
                                    .collect(Collectors.toList());

        int pageSize = 10;
        int totalOrders = customerOrders.size();
        int totalPages = (int) Math.ceil((double) totalOrders / pageSize);

        if (pageNumber < 1) pageNumber = 1;
        if (pageNumber > totalPages) pageNumber = totalPages;

        int fromIndex = (pageNumber - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalOrders);

        List<Order> pageData = customerOrders.subList(fromIndex, toIndex);

        return new OrderListResult(pageData, pageNumber, totalPages);
    }

    /**
    * Automatically generates test data including customers and orders.
    * Creates 10 customers and randomly generates 50-200 orders for each.
    * Order times should be scattered across different months of the year.
    */
    public void generateTestOrderData() {
        List<String> lines = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            String customerId = "c_" + i;
            int numberOfOrders = (int)(Math.random() * 151 + 50); // 50-200

            for (int j = 0; j < numberOfOrders; j++) {
                String orderId = generateUniqueOrderId();
                String productId = "p_" + ((int)(Math.random() * 100)); // giả định có p_0 -> p_99
                int month = (int)(Math.random() * 12 + 1); // tháng 1-12
                int day = (int)(Math.random() * 28 + 1);
                String orderTime = String.format("2024-%02d-%02dT12:00:00", month, day);

                Order order = new Order(orderId, customerId, productId, orderTime);
                lines.add(order.toString());
            }
        }
        FileUtil.writeLines(FileUtil.DATA_FILE, lines, false); // ghi đè để làm mới
    }

    /**
    * Generates a chart showing the consumption (sum of order prices)
    * across 12 different months for the given customer.
    * @param customerId The ID of the customer
    */
    public void generateSingleCustomerConsumptionFigure(String customerId) {
        Map<Integer, Double> monthToSpending = new HashMap<>();
        for (int i = 1; i <= 12; i++) monthToSpending.put(i, 0.0);

        for (Order order : orders) {
            if (order.getUserId().equals(customerId)) {
                int month = Integer.parseInt(order.getOrderTime().substring(3, 5));
                Product product = getProductById(order.getProId());
                if (product != null) {
                    double currentTotal = monthToSpending.get(month);
                    monthToSpending.put(month, currentTotal + product.getProCurrentPrice());
                }
            }
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 1; i <= 12; i++) {
            dataset.addValue(monthToSpending.get(i), "Consumption", Month.of(i).name());
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Monthly Consumption for Customer " + customerId,
            "Month", "Amount ($)", dataset, PlotOrientation.VERTICAL,
            false, true, false);

        saveChartToFile(chart, "Single Customer Consumption");
    }


    /**
    * Generates a chart showing the consumption (sum of order prices)
    * across 12 different months for all customers.
    */
    public void generateAllCustomersConsumptionFigure() {
        Map<Integer, Double> monthToSpending = new HashMap<>();
        for (int i = 1; i <= 12; i++) monthToSpending.put(i, 0.0);

        for (Order order : orders) {
            int month = Integer.parseInt(order.getOrderTime().substring(3, 5));
            Product product = getProductById(order.getProId());
            if (product != null) {
                double currentTotal = monthToSpending.get(month);
                monthToSpending.put(month, currentTotal + product.getProCurrentPrice());
            }
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 1; i <= 12; i++) {
            dataset.addValue(monthToSpending.get(i), "Total Consumption", Month.of(i).name());
        }

        JFreeChart chart = ChartFactory.createLineChart(
            "Monthly Consumption of All Customers",
            "Month", "Amount ($)", dataset, PlotOrientation.VERTICAL,
            false, true, false);

        saveChartToFile(chart, "All Customers Consumption");
    }


    /**
    * Generates a graph showing the top 10 best-selling products
    * sorted in descending order.
    */
    public void generateAllTop10BestSellersFigure() {
        Map<String, Integer> productSales = new HashMap<>();
        for (Order order : orders) {
            productSales.put(order.getProId(), productSales.getOrDefault(order.getProId(), 0) + 1);
        }

        List<Map.Entry<String, Integer>> sorted = productSales.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
            .limit(10)
            .collect(Collectors.toList());

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : sorted) {
            Product product = getProductById(entry.getKey());
            if (product != null) {
                dataset.addValue(entry.getValue(), "Sales", product.getProName());
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Top 10 Best-Selling Products",
            "Product", "Units Sold", dataset, PlotOrientation.VERTICAL,
            false, true, false);

        saveChartToFile(chart, "Top 10 Best Sellers");
    }

    private Product getProductById(String id) {
        for (Product p : products) {
            if (p.getProId().equals(id)) return p;
        }
        return null;
    }

    public void saveChartToFile(JFreeChart chart, String fileName) {
        try {
            File dir = new File("data/figure");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName + ".png");
            ChartUtils.saveChartAsPNG(file, chart, 800, 600);
            System.out.println("Chart saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving chart: " + e.getMessage());
        }
    }

    /**
    * Removes all data in the data/orders.txt file.
    */
    public void deleteAllOrders() {
        List<String> allLines = FileUtil.readLines(FileUtil.DATA_FILE);
        List<String> remainingLines = new ArrayList<>();

        for (String line : allLines) {
            if (!line.contains("\"order_id\":")) {
                remainingLines.add(line);
            }
        }

        FileUtil.writeLines(FileUtil.DATA_FILE, remainingLines, false);
    }

}
