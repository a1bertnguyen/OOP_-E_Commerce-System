import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;

public class TestChart {
    public static void main(String[] args) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(4, "A", "X");
        dataset.addValue(7, "A", "Y");

        JFreeChart chart = ChartFactory.createBarChart(
                "Test", "Category", "Value", dataset);

        try {
            ChartUtils.saveChartAsJPEG(new File("chart.jpg"), chart, 800, 600);
            System.out.println("Chart saved!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
