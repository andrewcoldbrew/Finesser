package myApp.controllers.views;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;


import com.sendgrid.*;
import javafx.event.ActionEvent;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import myApp.Main;
import myApp.utils.ConnectionManager;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class ReportController {
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

    public void initialize() {
        updateLabels();
        loadUserData(Main.getUserId());
    }

    private void loadUserData(int userId) {
        String query = "SELECT fname, email, DOB, country FROM user WHERE userID = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String username = rs.getString("fname");
                String email = rs.getString("email");
                LocalDate dob = rs.getDate("DOB").toLocalDate();
                String country = rs.getString("country");

                updateReportDetails(username, email, dob, country);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateReportDetails(String fname, String email, LocalDate dob, String country) {
        usernameLabel.setText(fname);
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
    private void handleGenerateReport(ActionEvent event) {
        String desktopPath = System.getProperty("user.home") + "/Desktop";
        String filename = desktopPath + File.separator + "report.pdf";
        try {
            generatePdfReport(filename);
            String recipientEmail = emailLabel.getText();
            String subject = "Your Report";
            String body = "Please find attached your report.";
            File attachment = new File(filename);

            sendEmailWithAttachment(recipientEmail, subject, body, attachment);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error generating and sending the report: " + e.getMessage());
        }
    }

    public void generatePdfReport(String filename) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

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

            File file = new File(filename);
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            document.save(file);
            System.out.println("PDF created: " + filename);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error generating PDF: " + e.getMessage());
        }
    }
    @SuppressWarnings("all")
    public void sendEmailWithAttachment(String recipientEmail, String subject, String body, File attachment) {
        String apiKey = "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";

        Email from = new Email("finess.rmit@gmail.com");
        Email to = new Email(recipientEmail);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        Personalization personalization = new Personalization();
        personalization.addTo(to);
        mail.addPersonalization(personalization);

        Attachments attachments = new Attachments();
        attachments.setFilename(attachment.getName());
        attachments.setType("application/pdf");

        try {

            byte[] pdfBytes = Files.readAllBytes(attachment.toPath());
            attachments.setContent(Base64.getEncoder().encodeToString(pdfBytes));
            mail.addAttachments(attachments);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            System.out.println("Email sent successfully. Status code: " + response.getStatusCode());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }



}
