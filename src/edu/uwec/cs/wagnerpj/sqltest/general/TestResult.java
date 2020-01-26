package edu.uwec.cs.wagnerpj.sqltest.general;

import java.util.ArrayList;

public class TestResult {
    private boolean warning;
    private int score;
    private ArrayList<String[]> missingRows;
    private ArrayList<String[]> extraRows;

    public TestResult(){ }

    public TestResult(int score, ArrayList<String[]> missingRows, ArrayList<String[]> extraRows, boolean warning) {
        this.warning = warning;
        this.score = score;
        this.missingRows = missingRows;
        this.extraRows = extraRows;
    }

    public TestResult(int score) {
        this.score = score;
    }

    public boolean getWarning() {
        return warning;
    }

    public void setWarning(boolean warning) {
        this.warning = warning;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ArrayList<String[]> getMissingRows() {
        return missingRows;
    }

    public void setMissingRows(ArrayList<String[]> missingRows) {
        this.missingRows = missingRows;
    }

    public ArrayList<String[]> getExtraRows() {
        return extraRows;
    }

    public void setExtraRows(ArrayList<String[]> extraRows) {
        this.extraRows = extraRows;
    }
}
