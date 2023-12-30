package myApp.controllers.views;

import java.io.IOException;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import myApp.controllers.components.LoadingScreen;
import myApp.controllers.components.ReportLoading;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import myApp.Main;
import myApp.utils.ConnectionManager;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import javax.swing.*;

public class ReportController {
    public StackPane stackPane;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label dobLabel;
    @FXML
    private Label countryLabel;
    @FXML
    private Label totalBudgetsLabel;
    @FXML
    private Label totalIncomeFinancesLabel;
    @FXML
    private Label totalOutcomeFinancesLabel;
    @FXML
    private Label totalTransactionsLabel;
    @FXML
    private Label topSpentCategoriesLabel;
    @FXML
    private Button generateReportButton;
    @FXML
    private Label dateLabel;
    @FXML
    private BarChart<String, Number> categoryAmountBarChart;
    @FXML
    private TextFlow chartDescriptionTextFlow;


    public void initialize() {
        new LoadingScreen(stackPane);
        Platform.runLater(() -> {
            loadUserData(Main.getUserId());
            updateLabels();
            loadCategoryAmountData();
        });

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        dateLabel.setText("Date: " + currentDate.format(formatter));
    }

    private void loadCategoryAmountData() {
        String query = "SELECT category, SUM(amount) AS total FROM transaction WHERE userID = ? AND category NOT IN ('Income', 'Dividend Income', 'Investment', 'Rent', 'Subscription', 'Insurance', 'Bills') GROUP BY category ORDER BY total DESC";

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Transaction Amounts by Category");

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, Main.getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String category = rs.getString("category");
                double total = rs.getDouble("total");
                XYChart.Data<String, Number> data = new XYChart.Data<>(category, total);
                series.getData().add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            categoryAmountBarChart.getData().clear();
            categoryAmountBarChart.getData().add(series);

            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    node.setStyle("-fx-bar-fill: " + getCategoryColor(data.getXValue()) + ";");
                } else {
                    data.nodeProperty().addListener((observableValue, oldNode, newNode) -> {
                        if (newNode != null) {
                            newNode.setStyle("-fx-bar-fill: " + getCategoryColor(data.getXValue()) + ";");
                        }
                    });
                }
            }
            updateChartDescription();
        });
    }

    private void generateChartDescriptionText(Map<String, Double> categoryAmounts) {
        if (categoryAmounts.isEmpty()) {
            return;
        }

        String highestCategory = Collections.max(categoryAmounts.entrySet(), Map.Entry.comparingByValue()).getKey();
        String lowestCategory = Collections.min(categoryAmounts.entrySet(), Map.Entry.comparingByValue()).getKey();


        Text introText = new Text("This chart illustrates the distribution of financial transactions. ");
        Text highestTextPrefix = new Text("The highest spending is in ");
        Text highestCategoryText = new Text(highestCategory); 
        Text highestTextSuffix = new Text(". ");
        Text lowestTextPrefix = new Text("The lowest is in ");
        Text lowestCategoryText = new Text(lowestCategory);
        Text lowestTextSuffix = new Text(". ");
        Text outroText = new Text("Each bar represents a different category of spending, providing a quick visual summary of financial activity.");

        highestCategoryText.setFont(Font.font("Cambria", FontWeight.BOLD, 18));
        lowestCategoryText.setFont(Font.font("Cambria", FontWeight.BOLD, 18));


        chartDescriptionTextFlow.getChildren().clear();
        chartDescriptionTextFlow.getChildren().addAll(introText, highestTextPrefix, highestCategoryText, highestTextSuffix, lowestTextPrefix, lowestCategoryText, lowestTextSuffix, outroText);
    }

    private void updateChartDescription() {
        Map<String, Double> categoryAmounts = new HashMap<>();
        for (XYChart.Series<String, Number> series : categoryAmountBarChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                categoryAmounts.put(data.getXValue(), data.getYValue().doubleValue());
            }
        }

        generateChartDescriptionText(categoryAmounts);
    }


    private Map<String, Double> extractCategoryAmountsFromChart() {
        Map<String, Double> categoryAmounts = new HashMap<>();
        for (XYChart.Series<String, Number> series : categoryAmountBarChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                categoryAmounts.put(data.getXValue(), data.getYValue().doubleValue());
            }
        }
        return categoryAmounts;
    }


    private String getCategoryColor(String category) {
        switch (category) {
            case "Clothes":
                return "orange";
            case "Education":
                return "dodgerblue";
            case "Entertainment":
                return "mediumseagreen";
            case "Food":
                return "crimson";
            case "Groceries":
                return "gold";
            case "Healthcare":
                return "violet";
            case "Transportation":
                return "lightcoral";
            case "Travel":
                return "lightskyblue";
            case "Other":
                return "silver";
            default:
                return "gray";
        }
    }


    private void loadUserData(int userId) {
        String query = "SELECT username, email, DOB, country FROM user WHERE userID = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                String email = rs.getString("email");
                LocalDate dob = rs.getDate("DOB").toLocalDate();
                String country = rs.getString("country");

                updateReportDetails(username, email, dob, country);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateReportDetails(String username, String email, LocalDate dob, String country) {
        usernameLabel.setText(username);
        emailLabel.setText(email);
        dobLabel.setText(dob.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        countryLabel.setText(country);
    }
    private void updateLabels() {
        totalBudgetsLabel.setText("Total Number of Budgets: " + getTotalNumberOfBudgets());
        totalIncomeFinancesLabel.setText("Total Number of Income Finances: " + getTotalNumberOfIncomeFinances());
        totalOutcomeFinancesLabel.setText("Total Number of Outcome Finances: " + getTotalNumberOfOutcomeFinances());
        totalTransactionsLabel.setText("Total Number of Transactions Made: " + getTotalNumberOfTransactions());

        Map<String, Double> topCategories = getTopSpentCategories();
        String topCategoriesText = topCategories.entrySet().stream()
                .map(entry -> entry.getKey() + ": $" + String.format("%.2f", entry.getValue()))
                .collect(Collectors.joining(", "));
        topSpentCategoriesLabel.setText("Top Spent Categories: " + topCategoriesText);
    }
    public int getTotalNumberOfBudgets() {
        String query = "SELECT COUNT(*) FROM budget WHERE userID = ?";
        return getCount(query);
    }

    public int getTotalNumberOfIncomeFinances() {
        String query = "SELECT COUNT(*) FROM transaction WHERE category IN ('Income', 'Dividend Income', 'Investment') AND userID = ?";
        return getCount(query);
    }

    public int getTotalNumberOfOutcomeFinances() {
        String query = "SELECT COUNT(*) FROM transaction WHERE category IN ('Subscription', 'Insurance', 'Bills') AND userID = ?";
        return getCount(query);
    }

    public int getTotalNumberOfTransactions() {
        String query = "SELECT COUNT(*) FROM transaction WHERE userID = ?";
        return getCount(query);
    }

    public Map<String, Double> getTopSpentCategories() {
        Map<String, Double> categoryTotals = new HashMap<>();
        String query = "SELECT category, SUM(amount) AS total FROM transaction WHERE category NOT IN ('Income', 'Dividend Income', 'Investment', 'Subscription', 'Insurance', 'Bills','Rent') AND userID = ? GROUP BY category ORDER BY total DESC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, Main.getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String category = rs.getString("category");
                double total = rs.getDouble("total");
                categoryTotals.put(category, total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryTotals;
    }

    private int getCount(String query) {
        int count = 0;
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, Main.getUserId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    public String formatReportAsString() {
        int totalBudgets = getTotalNumberOfBudgets();
        int totalIncomeFinances = getTotalNumberOfIncomeFinances();
        int totalOutcomeFinances = getTotalNumberOfOutcomeFinances();
        int totalTransactions = getTotalNumberOfTransactions();
        String topCategoriesText = getTopCategoriesText();

        return String.format(
                "Report for User: %s\n" +
                        "Email: %s\n" +
                        "Date of Birth: %s\n" +
                        "Country: %s\n\n" +
                        "Total Number of Budgets: %d\n" +
                        "Total Number of Income Finances: %d\n" +
                        "Total Number of Outcome Finances: %d\n" +
                        "Total Number of Transactions Made: %d\n" +
                        "Top Spent Categories: %s\n",
                usernameLabel.getText(),
                emailLabel.getText(),
                dobLabel.getText(),
                countryLabel.getText(),
                totalBudgets,
                totalIncomeFinances,
                totalOutcomeFinances,
                totalTransactions,
                topCategoriesText
        );
    }

    private String getTopCategoriesText() {
        Map<String, Double> topCategories = getTopSpentCategories();
        return topCategories.entrySet().stream()
                .map(entry -> entry.getKey() + ": $" + String.format("%.2f", entry.getValue()))
                .collect(Collectors.joining(", "));
    }
    @SuppressWarnings("all")
    @FXML
    public   void handleGenerateReport(ActionEvent event) {
//        String desktopPath = System.getProperty("user.home") + "/Desktop";
//        String filename = desktopPath + File.separator + "report.pdf";
//        generatePdfReport(filename);
//        ReportLoading reportLoading = new ReportLoading(stackPane); // Assuming mainPane is the StackPane
//        ReportGeneratorSwingWorker worker = new ReportGeneratorSwingWorker(reportLoading);
//        worker.execute();

        ReportLoading reportLoading = new ReportLoading(stackPane); // Assuming stackPane is the StackPane
        generatePdfReportInBackground(reportLoading);
    }

    private void generatePdfReportInBackground(ReportLoading reportLoading) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                String desktopPath = System.getProperty("user.home") + "/Desktop";
                String filename = desktopPath + File.separator + "report.pdf";
                generatePdfReport(filename);
                return null;
            }

            @Override
            protected void done() {
                // This method is called on the EDT when the background task is done
                Platform.runLater(reportLoading::changeState);
            }
        };

        // Execute the SwingWorker in a separate thread
        worker.execute();
    }

    @SuppressWarnings("all")
    private void generatePdfReport(String filename) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            String currentDirectory = System.getProperty("user.dir");
            System.out.println("The current working directory is: " + currentDirectory);


            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(25, 700);

                String text = formatReportAsString();
                for (String line: text.split("\n")) {
                    contentStream.showText(line);
                    contentStream.newLine();
                }

                contentStream.endText();
            }

            document.save(filename);
            System.out.println("PDF created");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
