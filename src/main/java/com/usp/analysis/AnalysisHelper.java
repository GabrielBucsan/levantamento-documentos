package com.usp.analysis;

import com.usp.utils.FileHelper;
import com.usp.view.AnalysisProgressBar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class AnalysisHelper {

    // Letras acentuadas/especiais do português que o usuário antes precisava digitar
    // manualmente como "." (ex.: "atenção primária" -> "aten..o prim.ria") para a busca
    // por regex funcionar independente de acentuação/codificação do texto original.
    private static final Pattern SPECIAL_CHARS_PATTERN =
            Pattern.compile("[áàâãäéèêëíìîïóòôõöúùûüçÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇ]");

    public List<AnalysisResult> runAnalysis(String directoryPath, SearchExpressions expressions) {
        AnalysisHelper analysisHelper = new AnalysisHelper();
        List<AnalysisResult> results = new ArrayList<>();
        Path dir = Paths.get(directoryPath);

        try {
            try (Stream<Path> paths = Files.list(dir)) {
                paths.filter(Files::isDirectory)
                        .forEach(folderPath -> results.add(analysisHelper.analyseFolder(folderPath, expressions)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }

    private AnalysisResult analyseFolder(Path folderPath, SearchExpressions expressions) {
        FileHelper reader = new FileHelper();
        AnalysisResult result = new AnalysisResult(folderPath.getFileName().toString(), expressions);

        Path mainDirectory = Paths.get(folderPath.toUri());

        try {
            try (Stream<Path> files = Files.list(mainDirectory)) {
                files.filter(Files::isRegularFile)
                .forEach(file -> {
                    GovDocument documento = null;
                    try {
                        documento = reader.readXmlFile(file);
                    } catch (Exception e) {
                        result.addError(file.getFileName() + " - " + e.getMessage());
                    }

                    if(documento != null) {
                        if(hasExpressionInString(documento.getTexto(), documento, expressions.getSearchExpressions())) {
                            if(expressions.hasResponsibleSearchExpressions()) {
                                if(hasExpressionInString(documento.getArtCategory(), documento, expressions.getResponsibleSearchExpressions())) {
                                    countFile(result, documento);
                                }
                            } else {
                                countFile(result, documento);
                            }
                        }
                    }
                    result.countFile();
                    AnalysisProgressBar.getInstance().increment();
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void countFile(AnalysisResult result, GovDocument documento) {
        result.countFileContainingKeyword(documento);
        if(documento.hasNoEmenta()) {
            result.countFileWithoutEmenta();
        }
    }

    private boolean hasExpressionInString(String texto, GovDocument document, String[] expressoesChave) {
        boolean hasAnyExpression = false;
        for(String expressao : expressoesChave) {
            String expressaoRegex = prepararExpressaoParaRegex(expressao.trim());
            Pattern pattern = Pattern.compile(expressaoRegex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(texto);
            int expressionCount = 0;
            while(matcher.find()) {
                expressionCount++;
            }
            // A chave continua sendo a expressão original digitada pelo usuário (sem a
            // substituição por ".") - é ela que é salva em searchData e exibida na planilha.
            document.setExpressionCount(expressao, expressionCount);
            if(expressionCount > 0) {
                hasAnyExpression = true;
            }
        }

        return hasAnyExpression;
    }

    /**
     * Substitui letras acentuadas/especiais por "." só para fins de compilação do regex de
     * busca - a expressão original (digitada pelo usuário) não é alterada em nenhum outro
     * lugar (searchData.sav, planilhas de resultado, etc.).
     */
    private String prepararExpressaoParaRegex(String expressao) {
        return SPECIAL_CHARS_PATTERN.matcher(expressao).replaceAll(".");
    }

}
