package Model;

import java.util.ArrayList;
import java.util.List;

public class ProductListResult {
    List<Product> products; // List products in current page
    int currentPage;          // Start at 1
    int totalPages;           


    public ProductListResult(List<Product> products, int currentPage, int totalPages) {
        this.products = products != null ? products : new ArrayList<>();
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

   
    public ProductListResult() {
        this.products = new ArrayList<>();
        this.currentPage = 0;
        this.totalPages = 0;
    }


    public List<Product> getProducts() {
        return products;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    @Override
    public String toString() {
        return "ProductListResult{" +
                "Number of products =" + (products != null ? products.size() : 0) +
                ", current page =" + currentPage +
                ", total page =" + totalPages +
                '}';
    }
}
