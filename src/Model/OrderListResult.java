package Model;

import java.util.ArrayList;
import java.util.List;

public class OrderListResult {
    private List<Order> orders; // List of orders in the current page
    private int currentPage;     // Start at 1
    private int totalPages;      // Total number of pages

    public OrderListResult(List<Order> orders, int currentPage, int totalPages) {
        this.orders = orders != null ? orders : new ArrayList<>();
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    public OrderListResult() {
        this.orders = new ArrayList<>();
        this.currentPage = 0;
        this.totalPages = 0;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    @Override
    public String toString() {
        return "OrderListResult{" +
                "Number of orders =" + (orders != null ? orders.size() : 0) +
                ", current page =" + currentPage +
                ", total page =" + totalPages +
                '}';
    }   
}
