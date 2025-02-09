import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class GroupBooleanCounter {
    private static JFrame mainFrame;
    private static CardLayout cardLayout;
    private static JPanel cardPanel;
    private static JLabel trueCountLabel;
    private static JLabel falseCountLabel;
    private static JLabel totalBooleansLabel;
    private static JLabel resultsTitleLabel;  // Added for dynamic title

    public static void main(String[] args) {
        if (args.length > 0) {
            runConsoleMode(args[0]);
        } else {
            SwingUtilities.invokeLater(GroupBooleanCounter::runGuiMode);
        }
    }

    private static void runConsoleMode(String filePath) {
        try {
            Map<String, Integer> results = BooleanCounterEngine.processCSVFile(filePath);
            printConsoleResults(results);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void printConsoleResults(Map<String, Integer> results) {
        int trueCount = results.getOrDefault("True Count", 0);
        int falseCount = results.getOrDefault("False Count", 0);
        int total = trueCount + falseCount;
        
        System.out.println("True Count: " + trueCount);
        System.out.println("False Count: " + falseCount);
        System.out.println("Total Booleans: " + total);
    }

    private static void runGuiMode() {
        mainFrame = new JFrame("Boolean Counter");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setPreferredSize(new Dimension(1024, 768));
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createTitlePanel(), "title");
        cardPanel.add(createResultsPanel(), "results");

        mainFrame.add(cardPanel);
        mainFrame.pack();
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setVisible(true);
    }

    private static JPanel createTitlePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 20, 10, 20);

        JLabel titleLabel = new JLabel("Boolean Counter");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        ImageIcon icon = new ImageIcon("cat-icon.png");
        Image scaledImg = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        titleLabel.setIcon(new ImageIcon(scaledImg));
        titleLabel.setForeground(new Color(0, 0, 0));
        panel.add(titleLabel, gbc);

        JLabel creatorLabel = new JLabel("Created by Hoby Josol");
        creatorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 24));
        creatorLabel.setForeground(new Color(128, 128, 128));
        panel.add(creatorLabel, gbc);

        JLabel descriptionLabel = new JLabel("<html><div style='text-align: center; width: 600px;'>"
                + "This program analyzes CSV files and counts the number of 'true' and 'false' values.<br>"
                + "Simply upload a CSV file using the button below to get started.</div></html>");
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        descriptionLabel.setForeground(new Color(80, 80, 80));
        panel.add(descriptionLabel, gbc);

        JButton uploadButton = new JButton("Upload CSV");
        styleUploadButton(uploadButton);
        uploadButton.addActionListener(e -> handleFileUpload(panel));
        panel.add(uploadButton, gbc);

        return panel;
    }

    private static void styleUploadButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        button.setPreferredSize(new Dimension(300, 80));
        button.setBackground(new Color(108, 117, 125));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private static void handleFileUpload(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            String fileName = fileChooser.getSelectedFile().getName(); // Get filename
            try {
                Map<String, Integer> results = BooleanCounterEngine.processCSVFile(filePath);
                updateResultsDisplay(results, fileName); // Pass filename
                cardLayout.show(cardPanel, "results");
            } catch (IOException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(parent, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 245, 245));

        resultsTitleLabel = new JLabel("Analysis Results"); // Initialize class variable
        resultsTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        ImageIcon icon = new ImageIcon("cat-icon.png");
        Image scaledImg = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        resultsTitleLabel.setIcon(new ImageIcon(scaledImg));
        resultsTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(resultsTitleLabel);
        contentPanel.add(Box.createVerticalStrut(40));

        trueCountLabel = new JLabel("True Count: 0");
        trueCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        trueCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        falseCountLabel = new JLabel("False Count: 0");
        falseCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        falseCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        totalBooleansLabel = new JLabel("Total Booleans: 0");
        totalBooleansLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        totalBooleansLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(trueCountLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(falseCountLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(totalBooleansLabel);
        contentPanel.add(Box.createVerticalGlue());

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        wrapper.add(contentPanel, gbc);

        JButton backButton = new JButton("Back to Main");
        styleBackButton(backButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.add(backButton);

        panel.add(wrapper, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private static void styleBackButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        button.setPreferredSize(new Dimension(200, 50));
        button.setBackground(new Color(108, 117, 125));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(e -> cardLayout.show(cardPanel, "title"));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // Modified to accept filename parameter
    private static void updateResultsDisplay(Map<String, Integer> results, String fileName) {
        resultsTitleLabel.setText("Analysis Results from " + fileName); // Update title with filename
        
        int trueCount = results.getOrDefault("True Count", 0);
        int falseCount = results.getOrDefault("False Count", 0);
        int total = trueCount + falseCount;

        trueCountLabel.setText("True Count: " + trueCount);
        falseCountLabel.setText("False Count: " + falseCount);
        totalBooleansLabel.setText("Total Booleans: " + total);
    }

    private static class BooleanCounterEngine {
        static Map<String, Integer> processCSVFile(String filePath) throws IOException, IllegalArgumentException {
            int trueCount = 0;
            int falseCount = 0;
            
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                boolean headerSkipped = false;
                
                while ((line = br.readLine()) != null) {
                    if (!headerSkipped) {
                        headerSkipped = true;
                        if (isHeader(line)) continue;
                    }
                    
                    String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    for (String value : values) {
                        String cleanValue = value.trim().replaceAll("^\"|\"$", "").toLowerCase();
                        if ("true".equals(cleanValue)) trueCount++;
                        else if ("false".equals(cleanValue)) falseCount++;
                    }
                }
            }

            Map<String, Integer> results = new HashMap<>();
            results.put("True Count", trueCount);
            results.put("False Count", falseCount);
            return results;
        }

        private static boolean isHeader(String line) {
            String[] values = line.split(",", -1);
            for (String value : values) {
                String cleanValue = value.trim().replaceAll("^\"|\"$", "").toLowerCase();
                if (!"true".equals(cleanValue) && !"false".equals(cleanValue)) {
                    return true;
                }
            }
            return false;
        }
    }
}
