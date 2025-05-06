package Model;

public class Product {
    /**
     * Constructs a product object.
     * 
     * @param proId           Product ID (must be unique)
     * @param proModel        Product model
     * @param proCategory     Product category
     * @param proName         Product name
     * @param proCurrentPrice Current price of the product
     * @param proRawPrice     Original price of the product
     * @param proDiscount     Discount percentage
     * @param proLikesCount   Number of likes
     */
    private String proId;
    private String proModel;
    private String proCategory;
    private String proName;
    private double proCurrentPrice;
    private double proRawPrice;
    private double proDiscount;
    private int proLikesCount;

    public Product(String proId, String proModel, String proCategory,
            String proName, double proCurrentPrice, double proRawPrice,
            double proDiscount, int proLikesCount) {
        this.proId = proId;
        this.proModel = proModel;
        this.proCategory = proCategory;
        this.proName = proName;
        this.proCurrentPrice = proCurrentPrice;
        this.proRawPrice = proRawPrice;
        this.proDiscount = proDiscount;
        this.proLikesCount = proLikesCount;
    }

    /**
     * Default constructor
     */
    public Product() {
        this.proId = "p_0000000000";
        this.proModel = "default_model";
        this.proCategory = "default_category";
        this.proName = "default_name";
        this.proCurrentPrice = 0.0;
        this.proRawPrice = 0.0;
        this.proDiscount = 0.0;
        this.proLikesCount = 0;
    }

    public String getProId() {
        return proId;
    }

    public String getProName() {
        return proName;
    }

    public double getProCurrentPrice() {
        return proCurrentPrice;
    }

    public double getProDiscount() {
        return proDiscount;
    }

    public int getProLikesCount() {
        return proLikesCount;
    }

    public double getDiscountAmount() {
        return proRawPrice * (proDiscount / 100.0);
    }

    /**
     * Returns the product information as a formatted string.
     * 
     * @return String in JSON-like format
     */
    @Override
    public String toString() {
        // Return in format: {"pro_id":"xxx", "pro_model":"xxx",
        // "pro_category":"xxx",
        // "pro_name":"xxx", "pro_current_price":"xxx", "pro_raw_price":"xxx",
        // "pro_discount":"xxx", "pro_likes_count":"xxx"}
        return String.format("{\"pro_id\":\"%s\",\"pro_model\":\"%s\",\"pro_category\":\"%s\"," +
                "\"pro_name\":\"%s\",\"pro_current_price\":%.2f,\"pro_raw_price\":%.2f," +
                "\"pro_discount\":%.2f,\"pro_likes_count\":%d}",
                proId, proModel, proCategory, proName,
                proCurrentPrice, proRawPrice, proDiscount, proLikesCount);
    }

}
