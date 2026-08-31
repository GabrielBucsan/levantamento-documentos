package com.usp.view;

import com.usp.analysis.AnalysisHelper;
import com.usp.analysis.AnalysisResult;
import com.usp.utils.FileHelper;
import com.usp.analysis.SearchExpressions;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ApplicationGUI {

    AnalysisHelper analysisHelper = new AnalysisHelper();
    FileHelper fileHelper = new FileHelper();
    JPanel yearsPanel = new JPanel();
    List<JCheckBox> yearCheckboxes = new ArrayList<>();

    public void startGui() {
        JFrame frame = createMainFrame();
        GridBagConstraints gbc = createLayout();
        JTextArea searchField = createSearchField(frame, gbc);
        JTextArea responsibleSearchField = createResponsibleSearchField(frame, gbc);
        JTextField directoryField = createDirectoryField(frame, gbc);
        createYearsSelectionField(frame, gbc);
        refreshYearCheckboxes(directoryField.getText());
        createExecutionButton(gbc, frame, searchField, responsibleSearchField, directoryField);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void createYearsSelectionField(JFrame frame, GridBagConstraints gbc) {
        JLabel yearsLabel = new JLabel("Anos a pesquisar:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        frame.add(yearsLabel, gbc);

        yearsPanel.setLayout(new GridLayout(0, 1));

        // Painel "âncora": impede que o JScrollPane estique yearsPanel para preencher
        // toda a altura disponível quando há poucos itens (cada linha ficaria enorme).
        JPanel yearsPanelAnchor = new JPanel(new BorderLayout());
        yearsPanelAnchor.add(yearsPanel, BorderLayout.NORTH);

        JScrollPane yearsScrollPane = new JScrollPane(yearsPanelAnchor);
        yearsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // Só a altura é fixa aqui; a largura é 1px de propósito para não competir com a
        // largura "real" da coluna (definida pelos rótulos/campos) — o fill=HORIZONTAL
        // do GridBagLayout estica o componente para a mesma largura de todas as outras seções.
        Dimension yearsAreaSize = new Dimension(1, 110);
        yearsScrollPane.setPreferredSize(yearsAreaSize);
        yearsScrollPane.setMinimumSize(yearsAreaSize);
        gbc.gridx = 0;
        gbc.gridy = 7;
        frame.add(yearsScrollPane, gbc);
    }

    private static final Color YEAR_ROW_COLOR_EVEN = Color.WHITE;
    private static final Color YEAR_ROW_COLOR_ODD = new Color(235, 235, 235);

    private void refreshYearCheckboxes(String directoryPath) {
        yearsPanel.removeAll();
        yearCheckboxes.clear();

        if (directoryPath != null && !directoryPath.isEmpty()) {
            List<String> years = fileHelper.listSubdirectoryNames(directoryPath);
            for (int i = 0; i < years.size(); i++) {
                JCheckBox yearCheckBox = new JCheckBox(years.get(i), true);
                yearCheckBox.setOpaque(true);
                yearCheckBox.setBackground(i % 2 == 0 ? YEAR_ROW_COLOR_EVEN : YEAR_ROW_COLOR_ODD);
                yearCheckboxes.add(yearCheckBox);
                yearsPanel.add(yearCheckBox);
            }
        }

        yearsPanel.revalidate();
        yearsPanel.repaint();
    }

    private List<Path> getSelectedYearPaths(String directoryPath) {
        List<Path> selectedYearPaths = new ArrayList<>();
        for (JCheckBox yearCheckBox : yearCheckboxes) {
            if (yearCheckBox.isSelected()) {
                selectedYearPaths.add(Paths.get(directoryPath, yearCheckBox.getText()));
            }
        }
        return selectedYearPaths;
    }

    private void createExecutionButton(GridBagConstraints gbc, JFrame frame, JTextArea searchField, JTextArea responsibleSearchField, JTextField directoryField) {
        JButton executeButton = new JButton("Executar");
        gbc.gridx = 0;
        gbc.gridy = 8;
        frame.add(executeButton, gbc);

        executeButton.addActionListener(e -> {
            String searchExpression = searchField.getText();
            String responsibleSearchExpression = responsibleSearchField.getText();
            String directoryPath = directoryField.getText();
            List<Path> selectedYearPaths = getSelectedYearPaths(directoryPath);

            fileHelper.clearErros(directoryPath);

            if (searchExpression.isEmpty() || directoryPath.isEmpty() || selectedYearPaths.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Por favor, preencha todos os campos e selecione ao menos um ano.", "Erro", JOptionPane.ERROR_MESSAGE);
            } else {
                SearchExpressions expressions = new SearchExpressions(searchExpression, responsibleSearchExpression);
                fileHelper.saveExpressions(expressions, directoryPath);

                executeButton.setEnabled(false);
                executeButton.setText("Análise em andamento");

                AnalysisProgressBar bar = getAnalysisProgressBar(gbc, frame, selectedYearPaths);

                new Thread(() -> {
                    try {
                        selectedYearPaths.forEach(directory -> doAnalysis(expressions, directory));
                    } finally {
                        SwingUtilities.invokeLater(() -> {
                            executeButton.setEnabled(true);
                            executeButton.setText("Executar");
                            JOptionPane.showMessageDialog(frame, "Análise concluída!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                            frame.remove(bar.getProgressBar());
                        });
                    }
                }).start();
            }
        });
    }

    private AnalysisProgressBar getAnalysisProgressBar(GridBagConstraints gbc, JFrame frame, List<Path> selectedYearPaths) {
        int totalXmlFiles = 0;
        for (Path yearPath : selectedYearPaths) {
            totalXmlFiles += fileHelper.countXmlFiles(yearPath.toString());
        }

        AnalysisProgressBar bar = AnalysisProgressBar.initializeBar(totalXmlFiles);
        gbc.gridx = 0;
        gbc.gridy = 9;
        frame.add(bar.getProgressBar(), gbc);
        return bar;
    }

    private void doAnalysis(SearchExpressions expressions, Path directory) {
        List<AnalysisResult> results = analysisHelper.runAnalysis(directory.toAbsolutePath().toString(), expressions);
        ExcelHelper excelHelper = new ExcelHelper();
        excelHelper.exportResultsAsXlsx(results, "./", "resultado-" + directory.getFileName().toString());
    }

    private static GridBagConstraints createLayout() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private JFrame createMainFrame() {
        JFrame frame = new JFrame("Busca de termos em arquivos do Diário Oficial da União");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(720, 580);
        frame.setLayout(new GridBagLayout());
        return frame;
    }

    private JTextArea createSearchField(JFrame frame, GridBagConstraints gbc) {
        JLabel searchLabel = new JLabel("Termos para pesquisa no corpo do documento (separados por vírgula):");
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(searchLabel, gbc);

        JTextArea searchField = new JTextArea(5, 30);
        gbc.gridx = 0;
        gbc.gridy = 1;
        searchField.setLineWrap(true);
        searchField.setWrapStyleWord(true);
        frame.add(searchField, gbc);

        String loadedExpressions = fileHelper.loadExpressions();
        if(loadedExpressions != null) {
            searchField.setText(loadedExpressions);
        }

        return searchField;
    }

    private JTextField createDirectoryField(JFrame frame, GridBagConstraints gbc) {
        JLabel directoryLabel = new JLabel("Pasta contendo arquivos para pesquisa:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        frame.add(directoryLabel, gbc);

        JTextField directoryField = new JTextField(20);
        directoryField.setEditable(false);

        JButton browseButton = new JButton("Procurar");
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                directoryField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                refreshYearCheckboxes(directoryField.getText());
            }
        });

        // Campo + botão somados formam uma única seção na coluna, com a mesma largura
        // que as demais seções (fill=HORIZONTAL aplicado ao painel como um todo).
        JPanel directoryRow = new JPanel(new BorderLayout(5, 0));
        directoryRow.add(directoryField, BorderLayout.CENTER);
        directoryRow.add(browseButton, BorderLayout.EAST);

        gbc.gridx = 0;
        gbc.gridy = 5;
        frame.add(directoryRow, gbc);

        String loadedDirectoryPath = fileHelper.loadDirectoryPath();
        if(loadedDirectoryPath != null) {
            directoryField.setText(loadedDirectoryPath);
        }

        return directoryField;
    }

    private JTextArea createResponsibleSearchField(JFrame frame,  GridBagConstraints gbc) {
        JLabel searchLabel = new JLabel("Termos para filtro em Órgão Responsável (separados por vírgula):");
        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(searchLabel, gbc);

        JTextArea searchField = new JTextArea(5, 30);
        gbc.gridx = 0;
        gbc.gridy = 3;
        frame.add(searchField, gbc);

        String loadedExpressions = fileHelper.loadResponsibleExpressions();
        if(loadedExpressions != null) {
            searchField.setText(loadedExpressions);
        }

        return searchField;
    }

}
