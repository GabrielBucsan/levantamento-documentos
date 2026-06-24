package com.usp.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class AnalysisResult {

    String folderName;
    int totalFiles = 0;
    int filesContainingKeywords = 0;
    int filesWithoutEmenta = 0;
    List<GovDocument> files = new ArrayList<>();
    SearchExpressions expressions;
    List<String> errors = new ArrayList<>();

    public AnalysisResult(String folderName, SearchExpressions expressions) {
        this.folderName = folderName;
        this.expressions = expressions;
    }

    protected AnalysisResult() {
    }

    public void countFile() {
        totalFiles++;
    }

    public void countFileContainingKeyword(GovDocument document) {
        filesContainingKeywords++;
        files.add(document);
    }

    public void countFileWithoutEmenta() {
        filesWithoutEmenta++;
    }

    public void addError(String error) {
        this.errors.add(error);
    }

    public String getFolderName() {
        return this.folderName;
    }

    public List<GovDocument> getFiles() {
        return this.files;
    }

    public int getTotalFiles() {
        return this.totalFiles;
    }

    public int getFilesContainingKeywords() {
        return this.filesContainingKeywords;
    }

    public int getFilesWithoutEmenta() {
        return filesWithoutEmenta;
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<String> getSearchExpressions() {
        return Arrays.asList(expressions.getSearchExpressions());
    }

    public int getExpressionCount(String expression) {
        int count = 0;
        for(GovDocument document : files) {
            count += document.getExpressionCount(expression);
        }
        return count;
    }

    public static AnalysisResult totalizeResults(Collection<AnalysisResult> results) {
        AnalysisResult totalResult = new AnalysisResult();
        boolean firstTime = true;

        for(AnalysisResult result : results) {
            totalResult.files.addAll(result.getFiles());
            totalResult.totalFiles += result.getTotalFiles();
            totalResult.filesContainingKeywords += result.filesContainingKeywords;
            totalResult.filesWithoutEmenta += result.filesWithoutEmenta;
            if(firstTime) {
                firstTime = false;
                totalResult.expressions = result.expressions;
                totalResult.folderName = result.folderName;
            }
        }

        return totalResult;
    }

}
