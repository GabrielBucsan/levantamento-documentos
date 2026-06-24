package com.usp.view;

import javax.swing.*;

public class AnalysisProgressBar {

    private static AnalysisProgressBar instance;

    private JProgressBar progressBar;

    public synchronized static AnalysisProgressBar getInstance() {
        if(instance == null) {
           instance = initializeBar(0);
        }
        return instance;
    }

    public void increment() {
        progressBar.setValue(progressBar.getValue() + 1);
    }

    public JProgressBar getProgressBar() {
        return instance.progressBar;
    }

    public static AnalysisProgressBar initializeBar(int total) {
        instance = new AnalysisProgressBar();
        instance.progressBar = new JProgressBar(0, total);
        instance.progressBar.setValue(0);
        instance.progressBar.setStringPainted(true);
        return instance;
    }

}
