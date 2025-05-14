package Operation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import Model.Product;
import Model.ProductListResult;

import util.FileUtil; 
import util.SimpleJsonParser;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ProductOperation {
    private static  ProductOperation instance = null;

    private ProductOperation(){
        FileUtil.ensureDataFileExists();
    }
    public static synchronized  ProductOperation getInstance() {
        if (instance == null){
            instance = new ProductOperation();
        }
        return instance;
    }

    private Product parseProduct(Map<String, String> data) {
        if (data == null || !data.containsKey("pro_id")) { // Check key
            return null;
        }
        try {
            return new Product(
                    data.get("pro_id"),
                    data.get("pro_model"),
                    data.get("pro_category"),
                    data.get("pro_name"),
                    Double.parseDouble(data.getOrDefault("pro_current_price", "0.0")),
                    Double.parseDouble(data.getOrDefault("pro_raw_price", "0.0")),
                    Double.parseDouble(data.getOrDefault("pro_discount", "0.0")),
                    Integer.parseInt(data.getOrDefault("pro_likes_count", "0"))
            );
        } catch (NumberFormatException e) {
            System.err.println("Parse error for Product data: " + data + " - " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("The error is not determined when parse product data: " + data + " - " + e.getMessage());
            return null;
        }
    }

    // Set data for products
    private List<Product> getAllProductsFromFile() {
        List<String> allLines = FileUtil.readLines(FileUtil.DATA_FILE);
        List<Product> products = new ArrayList<>();
        for (String line : allLines) {
            if (line.contains("\"pro_id\":") && line.contains("\"pro_name\":")) {
                Map<String, String> data = SimpleJsonParser.parse(line);
                Product product = parseProduct(data);
                if (product != null) {
                    products.add(product);
                }
            }
        }
        return products;
    }
    
    public void extractProductsFromFiles() {
        List<Product> currentProducts = getAllProductsFromFile();
        if (currentProducts.isEmpty()) {
            System.out.println("No products found in the data file. Consider adding sample data or implementing data extraction.");
        } else {
            System.out.println(currentProducts.size() + " products currently exist in the data file.");
        }
    }

    /**
    * Retrieves one page of products from the database.
    * One page contains a maximum of 10 items.
    * @param pageNumber The page number to retrieve
    * @return A list of Product objects, current page number, and total pages
    */
    public ProductListResult getProductList(int pageNumber) {
        final int PAGE_SIZE = 10;
        List<Product> allProducts = getAllProductsFromFile();

        if (allProducts.isEmpty()) {
            return new ProductListResult(new ArrayList<>(), 0, 0);
        }

        int totalProducts = allProducts.size();
        int totalPages = (int) Math.ceil((double) totalProducts / PAGE_SIZE);

        if (pageNumber < 1 || pageNumber > totalPages) {
            return new ProductListResult(new ArrayList<>(), pageNumber, totalPages);
        }

        int startIndex = (pageNumber - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalProducts);
        List<Product> pageOfProducts = allProducts.subList(startIndex, endIndex);

        return new ProductListResult(pageOfProducts, pageNumber, totalPages);
    }

    /**
    * Deletes the product from the system based on the provided product_id.
    * @param productId The ID of the product to delete
    * @return true if successful, false otherwise
    */
    private boolean shouldKeepProductLine(String line, String productId) {
        return !line.contains("\"pro_id\":\"" + productId + "\"");
    }

    public boolean deleteProduct(String productId) {
        List<String> allLines = FileUtil.readLines(FileUtil.DATA_FILE);
        List<String> filteredLines = allLines.stream()
            .filter(line -> shouldKeepProductLine(line, productId))
            .collect(Collectors.toList());

        if (allLines.size() == filteredLines.size()) {
            System.err.println("Product not found.");
            return false;
        }
        
        FileUtil.writeLines(FileUtil.DATA_FILE, filteredLines, false);
        System.out.println("Product deleted.");
        return true;
}


    /**
    * Retrieves all products whose name contains the keyword (case insensitive).
    * @param keyword The search keyword
    * @return A list of Product objects matching the keyword
    */
    public List<Product> getProductListByKeyword(String keyword) {
         if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String lowerCaseKeyword = keyword.trim().toLowerCase();
        return getAllProductsFromFile().stream() // Joint
                .filter(product -> product.getProName() != null &&
                                   product.getProName().toLowerCase().contains(lowerCaseKeyword)) // Lambda
                .collect(Collectors.toList()); // Combine
    }

    /**
    * Returns one product object based on the given product_id.
    * @param productId The ID of the product to retrieve
    * @return A Product object or null if not found
    */
    public Product getProductById(String productId) {
         if (productId == null || productId.trim().isEmpty()) {
            return null;
        }

        String trimmedId = productId.trim();
        return getAllProductsFromFile().stream()
                .filter(product -> product.getProId().equals(trimmedId))
                .findFirst().orElse(null);
    }

    /**
    * Generates a bar chart showing the total number of products
    * for each category in descending order.
    * Saves the figure into the data/figure folder.
    */
    public void generateCategoryFigure() {
        List<Product> products = getAllProductsFromFile();
        Map<String, Long> categoryCount = products.stream()
            .collect(Collectors.groupingBy(Product::getProCategory, Collectors.counting()));

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        categoryCount.entrySet().stream()
            .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // Descending
            .forEach(entry -> dataset.addValue(entry.getValue(), "Products", entry.getKey()));

        JFreeChart barChart = ChartFactory.createBarChart(
            "Number of Products per Category",
            "Category",
            "Product Count",
            dataset
        );

        saveChartAsImage(barChart, "data/figure/category_bar_chart.png");
    }

    
    /**
    * Generates a pie chart showing the proportion of products that have
    * a discount value less than 30, between 30 and 60 inclusive,
    * and greater than 60.
    * Saves the figure into the data/figure folder.
    */
    public void generateDiscountFigure() {
        List<Product> products = getAllProductsFromFile();

        long low = products.stream().filter(p -> p.getProDiscount() < 30).count();
        long mid = products.stream().filter(p -> p.getProDiscount() >= 30 && p.getProDiscount() <= 60).count();
        long high = products.stream().filter(p -> p.getProDiscount() > 60).count();

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Discount < 30", low);
        dataset.setValue("30 ≤ Discount ≤ 60", mid);
        dataset.setValue("Discount > 60", high);

        JFreeChart pieChart = ChartFactory.createPieChart(
            "Product Discount Distribution",
            dataset, true, true, false);

        saveChartAsImage(pieChart, "data/figure/discount_pie_chart.png");
    }



    /**
    * Generates a chart displaying the sum of products' likes_count
    * for each category in ascending order.
    * Saves the figure into the data/figure folder.
    */
    public void generateLikesCountFigure() {
        List<Product> products = getAllProductsFromFile();

        Map<String, Integer> categoryLikes = new HashMap<>();
        for (Product p : products) {
            categoryLikes.put(p.getProCategory(),
                categoryLikes.getOrDefault(p.getProCategory(), 0) + p.getProLikesCount());
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        categoryLikes.entrySet().stream()
            .sorted(Map.Entry.comparingByValue()) // Ascending
            .forEach(entry -> dataset.addValue(entry.getValue(), "Likes", entry.getKey()));

        JFreeChart chart = ChartFactory.createBarChart(
            "Likes Count per Category",
            "Category",
            "Total Likes",
            dataset
        );

        saveChartAsImage(chart, "data/figure/likes_count_chart.png");
    }


    /**
    * Generates a scatter chart showing the relationship between
    * likes_count and discount for all products.
    * Saves the figure into the data/figure folder.
    */
    public void generateDiscountLikesCountFigure() {
        List<Product> products = getAllProductsFromFile();
        XYSeries series = new XYSeries("Discount vs Likes");

        for (Product p : products) {
            series.add(p.getProDiscount(), p.getProLikesCount());
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createScatterPlot(
            "Discount vs Likes Count",
            "Discount (%)",
            "Likes Count",
            dataset
        );

        saveChartAsImage(chart, "data/figure/discount_likes_scatter.png");
    }


    /**
    * Removes all product data in the data/products.txt file.
    */
    public void deleteAllProducts() {
    List<String> allLines = FileUtil.readLines(FileUtil.DATA_FILE);
    List<String> filteredLines = allLines.stream()
        .filter(line -> !line.contains("\"pro_id\":") || !line.contains("\"pro_name\":"))
        .collect(Collectors.toList());

    FileUtil.writeLines(FileUtil.DATA_FILE, filteredLines, false);
    System.out.println("All products deleted from the file.");
    }

    private void saveChartAsImage(JFreeChart chart, String filePath) {
        try {
            File outputFile = new File(filePath);
            outputFile.getParentFile().mkdirs();
            ChartUtils.saveChartAsPNG(outputFile, chart, 800, 600);
            System.out.println("Chart saved to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving chart: " + e.getMessage());
        }
    }

}
