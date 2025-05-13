package Operation;

import java.util.List;
import java.util.Map;

import Model.Product;
import Model.ProductListResult;

public class ProductOperation {
    private static  ProductOperation instance = null;

    private ProductOperation(){
        UserOperation.FileUtil.ensureDataFileExists();
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

    /**
    * Extracts product information from the given product data files.
    * The data is saved into the data/products.txt file.
    */
    public void extractProductsFromFiles() {
    // Implementation
    }

    /**
    * Retrieves one page of products from the database.
    * One page contains a maximum of 10 items.
    * @param pageNumber The page number to retrieve
    * @return A list of Product objects, current page number, and total pages
    */
    public ProductListResult getProductList(int pageNumber) {
    // Implementation
    }

    /**
    * Deletes the product from the system based on the provided product_id.
    * @param productId The ID of the product to delete
    * @return true if successful, false otherwise
    */
    public boolean deleteProduct(String productId) {
    // Implementation
    }

    /**
    * Retrieves all products whose name contains the keyword (case insensitive).
    * @param keyword The search keyword
    * @return A list of Product objects matching the keyword
    */
    public List<Product> getProductListByKeyword(String keyword) {
    // Implementation
    }

    /**
    * Returns one product object based on the given product_id.
    * @param productId The ID of the product to retrieve
    * @return A Product object or null if not found
    */
    public Product getProductById(String productId) {
    // Implementation
    }

    /**
    * Generates a bar chart showing the total number of products
    * for each category in descending order.
    * Saves the figure into the data/figure folder.
    */
    public void generateCategoryFigure() {
    // Implementation using Java charting library
    }
    
    /**
    * Generates a pie chart showing the proportion of products that have
    * a discount value less than 30, between 30 and 60 inclusive,
    * and greater than 60.
    * Saves the figure into the data/figure folder.
    */
    public void generateDiscountFigure() {
    // Implementation using Java charting library
    }


    /**
    * Generates a chart displaying the sum of products' likes_count
    * for each category in ascending order.
    * Saves the figure into the data/figure folder.
    */
    public void generateLikesCountFigure() {
    // Implementation using Java charting library
    }

    /**
    * Generates a scatter chart showing the relationship between
    * likes_count and discount for all products.
    * Saves the figure into the data/figure folder.
    */
    public void generateDiscountLikesCountFigure() {
    // Implementation using Java charting library
    }

    /**
    * Removes all product data in the data/products.txt file.
    */
    public void deleteAllProducts() {
    // Implementation
    }

}
