package com.usp.view;

import com.usp.analysis.AnalysisResult;
import com.usp.analysis.GovDocument;
import com.usp.utils.FileHelper;
import com.usp.view.model.CellInfo;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ExcelHelper {

    public void exportResultsAsXlsx(List<AnalysisResult> results, String directoryPath, String resultFileName) {
        Workbook workbook = generateXlsx(results);
        FileHelper fileHelper = new FileHelper();
        fileHelper.saveXlsxToFile(workbook, directoryPath, resultFileName);
    }

    private Workbook generateXlsx(List<AnalysisResult> results) {
        Workbook workbook = new XSSFWorkbook();
        List<String> errors = new ArrayList<>();

        generateTotalSheet(workbook, results);
        generateMonthSheets(workbook, results, errors);
        generateErrorSheet(workbook, errors);

        return workbook;
    }

    private void generateMonthSheets(Workbook workbook, List<AnalysisResult> results, List<String> errors) {
        for (AnalysisResult result : results) {
            Sheet sheet = workbook.createSheet(result.getFolderName());
            printResultOnSheet(workbook, result, sheet);
            errors.addAll(result.getErrors());
        }
    }

    private void printResultOnSheet(Workbook workbook, AnalysisResult result, Sheet sheet) {
        Integer row = 0;
        row = createTotalColumns(result, sheet, row);
        row = createBlankRow(sheet, row);
        int totalColumns = createDocumentHeader(sheet, result.getSearchExpressions(), row++);
        for(int i = 0; i < result.getFiles().size(); i++) {
            GovDocument document = result.getFiles().get(i);
            row = createDocumentResultRow(workbook, document, result.getSearchExpressions(), sheet, row);
        }
        for (int i = 0; i < totalColumns; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void generateTotalSheet(Workbook workbook, List<AnalysisResult> results) {
        Sheet sheet = workbook.createSheet("Totais");
        AnalysisResult totalResult = AnalysisResult.totalizeResults(results);
        createHTMLFiles(totalResult);
        printResultOnSheet(workbook, totalResult, sheet);
    }

    private void createHTMLFiles(AnalysisResult result) {
        FileHelper helper = new FileHelper();
        helper.clearHTMLFiles(result.getFolderName());
        for(GovDocument document : result.getFiles()) {
            document.setArquivoHtml(helper.createViewFile(result.getFolderName(), document.getIdentifica(), document.getTexto()));
        }
    }

    private void generateErrorSheet(Workbook workbook, List<String> errors) {
        Sheet sheet = workbook.createSheet("Erros");

        for(int i = 0; i < errors.size(); i++) {
            Row row = sheet.createRow(i);
            Cell cell1 = row.createCell(0);
            cell1.setCellValue(errors.get(i));
        }
    }

    private Integer createTotalColumns(AnalysisResult result, Sheet sheet, Integer line) {
        Row row = sheet.createRow(line++);
        Cell cell1 = row.createCell(0);
        cell1.setCellValue("Arquivos analisados:");
        Cell cell2 = row.createCell(1);
        cell2.setCellValue(result.getTotalFiles());

        Row row2 = sheet.createRow(line++);
        Cell cell3 = row2.createCell(0);
        cell3.setCellValue("Arquivos contendo pelo menos um dos termos:");
        Cell cell4 = row2.createCell(1);
        cell4.setCellValue(result.getFilesContainingKeywords());

        Row row3 = sheet.createRow(line++);
        Cell cell5 = row3.createCell(0);
        cell5.setCellValue("Arquivos com o campo Ementa vazio ou em branco:");
        Cell cell6 = row3.createCell(1);
        cell6.setCellValue(result.getFilesWithoutEmenta());

        for(String expression : result.getSearchExpressions()) {
            Row expressionRow = sheet.createRow(line++);
            Cell expressionCell = expressionRow.createCell(0);
            expressionCell.setCellValue(expression);
            Cell countCell = expressionRow.createCell(1);
            countCell.setCellValue(result.getExpressionCount(expression));
        }

        return line;
    }

    private Integer createBlankRow(Sheet sheet, Integer row) {
        sheet.createRow(row++);
        return row;
    }

    private int createDocumentHeader(Sheet sheet, List<String> expressions, Integer line) {
        Row row = sheet.createRow(line);

        List<Object> objects = new ArrayList<>();
        objects.add("Tipo de norma");
        objects.add("Nome da norma");
        objects.add("Página do DOU");
        objects.add("Data de publicação no DOU");
        objects.add("Órgão responsável pela publicação");
        objects.add("Ementa");
        objects.add("Arquivo");
        objects.add("Arquivo de visualização");
        objects.addAll(expressions);

        for(int i = 0; i < objects.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(objects.get(i).toString());
        }

        sheet.createFreezePane(0, line + 1);

        return objects.size();
    }

    private Integer createDocumentResultRow(Workbook workbook, GovDocument document, List<String> expressions, Sheet sheet, Integer rowNumber) {
        Row row = sheet.createRow(rowNumber++);

        List<CellInfo> cellInfos = document.getCellInfos(expressions);

        for(int i = 0; i < cellInfos.size(); i++) {
            CellInfo cellInfo = cellInfos.get(i);
            Cell cell = row.createCell(i);
            cell.setCellValue(cellInfo.getValue());
            if(cellInfo.isFileLink()) {
                createLocalLink(workbook, cellInfo, cell);
            }
        }

        return rowNumber;
    }

    private static void createLocalLink(Workbook workbook, CellInfo cellinfo, Cell cell) {
        CreationHelper helper = workbook.getCreationHelper();
        Hyperlink link = helper.createHyperlink(HyperlinkType.FILE);

        URI uri = Paths.get(cellinfo.getValue()).toUri();
        link.setAddress(uri.toString());
        cell.setHyperlink(link);

        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setUnderline(Font.U_SINGLE);
        font.setColor(IndexedColors.BLUE.getIndex());
        style.setFont(font);
        cell.setCellStyle(style);
    }

}
