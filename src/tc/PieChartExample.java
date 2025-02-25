import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;

public class PieChartExample extends JFrame {

    public PieChartExample(String title) {
        super(title);

        // Create dataset
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Type A", 40);
        dataset.setValue("Type B", 30);
        dataset.setValue("Type C", 20);
        dataset.setValue("Type D", 10);

        // Create chart
        JFreeChart chart = ChartFactory.createPieChart(
                "Pie Chart Example",  // Chart title
                dataset,              // Dataset
                true,                 // Include legend
                true,                 // Tooltips
                false                 // URLs
        );

        // Create Panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PieChartExample example = new PieChartExample("JFreeChart Pie Chart Example");
            example.setSize(800, 600);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }
}