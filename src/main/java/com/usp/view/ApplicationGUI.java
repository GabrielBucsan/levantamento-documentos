package com.usp.view;

import com.usp.analysis.AnalysisHelper;
import com.usp.analysis.AnalysisResult;
import com.usp.utils.FileHelper;
import com.usp.analysis.SearchExpressions;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class ApplicationGUI {

    AnalysisHelper analysisHelper = new AnalysisHelper();
    FileHelper fileHelper = new FileHelper();

    public void startGui() {
        JFrame frame = createMainFrame();
        GridBagConstraints gbc = createLayout();
        JTextArea searchField = createSearchField(frame, gbc);
        JTextArea responsibleSearchField = createResponsibleSearchField(frame, gbc);
        JTextField directoryField = createDirectoryField(frame, gbc);
        createDirectoryButton(gbc, frame, directoryField);
        createExecutionButton(gbc, frame, searchField, responsibleSearchField, directoryField);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void createExecutionButton(GridBagConstraints gbc, JFrame frame, JTextArea searchField, JTextArea responsibleSearchField, JTextField directoryField) {
        JButton executeButton = new JButton("Executar");
        gbc.gridx = 0;
        gbc.gridy = 6;
        frame.add(executeButton, gbc);

        executeButton.addActionListener(e -> {
            String searchExpression = searchField.getText();
            String responsibleSearchExpression = responsibleSearchField.getText();
            String directoryPath = directoryField.getText();

            fileHelper.clearErros(directoryPath);

            if (searchExpression.isEmpty() || directoryPath.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Por favor, preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
            } else {
                SearchExpressions expressions = new SearchExpressions(searchExpression, responsibleSearchExpression);
                fileHelper.saveExpressions(expressions, directoryPath);

                executeButton.setEnabled(false);
                executeButton.setText("Análise em andamento");

                AnalysisProgressBar bar = getAnalysisProgressBar(gbc, frame, directoryPath);

                new Thread(() -> {
                    try (Stream<Path> paths = Files.list(new File(directoryPath).toPath())) {
                        paths.filter(Files::isDirectory)
                                .forEach(directory -> doAnalysis(expressions, directory));
                    } catch (IOException err) {
                        err.printStackTrace();
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

    private AnalysisProgressBar getAnalysisProgressBar(GridBagConstraints gbc, JFrame frame, String directoryPath) {
        AnalysisProgressBar bar = AnalysisProgressBar.initializeBar(fileHelper.countXmlFiles(directoryPath));
        gbc.gridx = 0;
        gbc.gridy = 7;
        frame.add(bar.getProgressBar(), gbc);
        return bar;
    }

    private void doAnalysis(SearchExpressions expressions, Path directory) {
        List<AnalysisResult> results = analysisHelper.runAnalysis(directory.toAbsolutePath().toString(), expressions);
        ExcelHelper excelHelper = new ExcelHelper();
        excelHelper.exportResultsAsXlsx(results, "./", "resultado-" + directory.getFileName().toString());
    }

    private void createDirectoryButton(GridBagConstraints gbc, JFrame frame, JTextField directoryField) {
        JButton browseButton = new JButton("Procurar");
        gbc.gridx = 1;
        gbc.gridy = 5;
        frame.add(browseButton, gbc);

        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                directoryField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
    }

    private static GridBagConstraints createLayout() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private JFrame createMainFrame() {
        JFrame frame = new JFrame("Busca de expressões em arquivos do Diário Oficial da União");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 300);
        frame.setLayout(new GridBagLayout());
        return frame;
    }

    private JTextArea createSearchField(JFrame frame, GridBagConstraints gbc) {
        JLabel searchLabel = new JLabel("Expressões para pesquisa no corpo do documento (separados por vírgula):");
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
        JLabel directoryLabel = new JLabel("Diretório contendo arquivos para pesquisa:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        frame.add(directoryLabel, gbc);

        JTextField directoryField = new JTextField(20);
        directoryField.setEditable(false);
        gbc.gridx = 0;
        gbc.gridy = 5;
        frame.add(directoryField, gbc);

        String loadedDirectoryPath = fileHelper.loadDirectoryPath();
        if(loadedDirectoryPath != null) {
            directoryField.setText(loadedDirectoryPath);
        }

        return directoryField;
    }

    private JTextArea createResponsibleSearchField(JFrame frame,  GridBagConstraints gbc) {
        JLabel searchLabel = new JLabel("Expressões para filtro em Órgão Responsável (separados por vírgula):");
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
