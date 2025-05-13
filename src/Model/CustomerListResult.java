package Model;
import java.util.ArrayList;
import java.util.List; 


public class CustomerListResult {
    List<Customer> customers; // List customer in current page
    int currentPage;          // Start at 1
    int totalPages;           


    public CustomerListResult(List<Customer> customers, int currentPage, int totalPages) {
        this.customers = customers;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

   
    public CustomerListResult() {
        this.customers = new ArrayList<>();
        this.currentPage = 0;
        this.totalPages = 0;
    }


    public List<Customer> getCustomers() {
        return customers;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    @Override
    public String toString() {
        return "CustomerListResult{" +
                "Number of customers =" + (customers != null ? customers.size() : 0) +
                ", current page =" + currentPage +
                ", total page =" + totalPages +
                '}';
    }
}
