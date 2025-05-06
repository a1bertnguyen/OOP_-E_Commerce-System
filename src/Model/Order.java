package Model;

public class Order {
    /**
     * Constructs an order object.
     * 
     * @param orderId   Must be a unique string, format is o_5 digits such as
     *                  o_12345
     * @param userId    ID of the user who placed the order
     * @param proId     ID of the product ordered
     * @param orderTime Format: "DD-MM-YYYY_HH:MM:SS"
     */
    private String orderId;
    private String userId;
    private String proId;
    private String orderTime;

    public Order(String orderId, String userId, String proId, String orderTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.proId = proId;
        this.orderTime = orderTime;
    }

    /**
     * Default constructor
     */
    public Order() {
        this.orderId = "o_00000";
        this.userId = "u_0000000000";
        this.proId = "p000";
        this.orderTime = "01-01-2000_00:00:00";
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public String getProId() {
        return proId;
    }

    public String getUserId() {
        return userId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public void setProId(String proId) {
        this.proId = proId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the order information as a formatted string.
     * 
     * @return String in JSON-like format
     */

    @Override
    public String toString() {
        return String.format(
                "{\"order_id\":\"%s\", \"user_id\":\"%s\", \"pro_id\":\"%s\", \"order_time\":\"%s\"}",
                orderId, userId, proId, orderTime);
    }

}
