package com.usp.analysis;

public class SearchExpressions {

    private final String searchExpressionRaw;
    private final String responsibleExpressionRaw;
    private final String[] searchExpressions;
    private final String[] responsibleSearchExpressions;

    public SearchExpressions(String searchExpression, String responsibleExpression) {
        searchExpressions = searchExpression.split(",");
        searchExpressionRaw = searchExpression;
        responsibleSearchExpressions = responsibleExpression.split(",");
        responsibleExpressionRaw = responsibleExpression;
    }

    public String[] getSearchExpressions() {
        return this.searchExpressions;
    }

    public String[] getResponsibleSearchExpressions() {
        return this.responsibleSearchExpressions;
    }

    public String getSearchExpressionRaw() {
        return searchExpressionRaw;
    }

    public String getResponsibleExpressionRaw() {
        return responsibleExpressionRaw;
    }

    public boolean hasResponsibleSearchExpressions() {
        return this.responsibleSearchExpressions != null && this.responsibleSearchExpressions.length > 0;
    }

}
