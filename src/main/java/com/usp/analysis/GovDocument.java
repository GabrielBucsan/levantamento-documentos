package com.usp.analysis;

import com.usp.view.model.CellInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GovDocument {

    String numberPage;
    String name;
    String pubDate;
    String artCategory;
    String identifica;
    String ementa;
    String texto;
    String arquivo;
    String arquivoHtml;
    Map<String, Integer> ocorrenciasExpressoes = new HashMap<>();

    public void setNumberPage(String numberPage) {
        this.numberPage = numberPage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public void setArtCategory(String artCategory) {
        this.artCategory = artCategory;
    }

    public void setIdentifica(String identifica) {
        this.identifica = identifica;
    }

    public void setEmenta(String ementa) {
        this.ementa = ementa;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public void setArquivoHtml(String arquivoHtml) {
        this.arquivoHtml = arquivoHtml;
    }

    public void setExpressionCount(String expression, Integer expressionCount) {
        this.ocorrenciasExpressoes.put(expression, expressionCount);
    }

    public int getExpressionCount(String expression) {
        return this.ocorrenciasExpressoes.get(expression);
    }

    public String getTexto() {
        return this.texto;
    }

    public String getNumberPage() {
        return numberPage;
    }

    public String getName() {
        return name;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getArtCategory() {
        return artCategory;
    }

    public String getIdentifica() {
        return identifica;
    }

    public String getEmenta() {
        return ementa;
    }

    public String getArquivo() {
        return arquivo;
    }

    public String getArquivoHtml() {
        return arquivoHtml;
    }

    public boolean hasNoEmenta() {
        return ementa == null || ementa.isEmpty();
    }

    public List<CellInfo>  getCellInfos(List<String> expressions) {
        List<CellInfo> cellInfos = new ArrayList<>();

        cellInfos.add(new CellInfo(getName()));
        cellInfos.add(new CellInfo(getIdentifica()));
        cellInfos.add(new CellInfo(getNumberPage()));
        cellInfos.add(new CellInfo(getPubDate()));
        cellInfos.add(new CellInfo(getArtCategory()));
        cellInfos.add(new CellInfo(getEmenta()));
        cellInfos.add(new CellInfo(getArquivo(), true));
        cellInfos.add(new CellInfo(getArquivoHtml(), true));

        for(String expression : expressions) {
            cellInfos.add(new CellInfo(String.valueOf(ocorrenciasExpressoes.get(expression))));
        }

        return  cellInfos;
    }
}
