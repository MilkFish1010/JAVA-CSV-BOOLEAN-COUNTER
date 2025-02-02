import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class GroupBooleanCounter {
    public static void main(String[] args) {
        if (args.length > 0) {
            // Console mode
            runConsoleMode(args[0]);
        } else {
            // GUI mode
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
        System.out.println("Boolean Value Counts:");
        results.forEach((k, v) -> System.out.println(k + " -> " + v));
    }

    private static void runGuiMode() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(null, "No file selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        try {
            Map<String, Integer> results = BooleanCounterEngine.processCSVFile(filePath);
            showGuiResults(results);
        } catch (IOException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void showGuiResults(Map<String, Integer> results) {
        JFrame frame = new JFrame("Boolean Value Counts");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextArea textArea = new JTextArea(20, 50);
        textArea.setEditable(false);
        textArea.append("Boolean Value Counts:\n");
        results.forEach((k, v) -> textArea.append(k + " -> " + v + "\n"));

        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static class BooleanCounterEngine {
        static Map<String, Integer> processCSVFile(String filePath) throws IOException, IllegalArgumentException {
            List<String[]> rows = readCSVRows(filePath);
            validateCSVContents(rows);
            
            boolean hasHeader = detectHeader(rows.get(0));
            List<Integer> booleanColumns = findBooleanColumns(rows, hasHeader);
            
            return countBooleanCombinations(rows, hasHeader, booleanColumns);
        }

        private static List<String[]> readCSVRows(String filePath) throws IOException {
            List<String[]> rows = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] row = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    for (int i = 0; i < row.length; i++) {
                        row[i] = row[i].trim().replaceAll("^\"|\"$", "");
                    }
                    rows.add(row);
                }
            }
            return rows;
        }

        private static void validateCSVContents(List<String[]> rows) {
            if (rows.isEmpty()) {
                throw new IllegalArgumentException("CSV file is empty");
            }
        }

        private static boolean detectHeader(String[] firstRow) {
            for (String cell : firstRow) {
                String lowerCell = cell.toLowerCase();
                if (!lowerCell.equals("true") && !lowerCell.equals("false")) {
                    return true;
                }
            }
            return false;
        }

        private static List<Integer> findBooleanColumns(List<String[]> rows, boolean hasHeader) {
            List<Integer> booleanColumns = new ArrayList<>();
            if (rows.isEmpty()) return booleanColumns;

            int startRow = hasHeader ? 1 : 0;
            int colCount = rows.get(startRow).length;

            for (int col = 0; col < colCount; col++) {
                boolean isBoolean = true;
                for (int rowIdx = startRow; rowIdx < rows.size(); rowIdx++) {
                    String[] row = rows.get(rowIdx);
                    if (col >= row.length || !isValidBoolean(row[col])) {
                        isBoolean = false;
                        break;
                    }
                }
                if (isBoolean) booleanColumns.add(col);
            }
            
            if (booleanColumns.isEmpty()) {
                throw new IllegalArgumentException("No boolean columns found");
            }
            return booleanColumns;
        }

        private static boolean isValidBoolean(String value) {
            String lowerValue = value.toLowerCase();
            return lowerValue.equals("true") || lowerValue.equals("false");
        }

        private static Map<String, Integer> countBooleanCombinations(List<String[]> rows, boolean hasHeader, 
                                                                    List<Integer> booleanColumns) {
            Map<String, Integer> countMap = new HashMap<>();
            int startRow = hasHeader ? 1 : 0;

            for (int i = startRow; i < rows.size(); i++) {
                String[] row = rows.get(i);
                List<String> combination = new ArrayList<>();
                
                for (int col : booleanColumns) {
                    String value = (col < row.length) ? row[col].toLowerCase() : "false";
                    combination.add(value);
                }
                
                String key = String.join(", ", combination);
                countMap.put(key, countMap.getOrDefault(key, 0) + 1);
            }
            return countMap;
        }
    }
}
