package edu.uwec.cs.wagnerpj.sqltest.general;

import java.util.ArrayList;

public class TestResult {
    //enumeration of warning types (so far there is only one defined)
    public static int MISMATCHED_SCORE_WARNING = 0;

    private ArrayList<Integer> warnings;    //list of all warnings a test may have issued. NOTE: See enumeration above
    private int score;                      //calculated score of a test
    private ArrayList<String[]> missingRows;//if applicable, number of missing rows (student failed to supply)
    private ArrayList<String[]> extraRows;  //if applicable, number of extra rows (student should not have supplied)

    public TestResult(){ }

    public TestResult(int score, ArrayList<String[]> missingRows, ArrayList<String[]> extraRows) {
        this.warnings = new ArrayList<Integer>();
        this.score = score;
        this.missingRows = missingRows;
        this.extraRows = extraRows;
    }

    public TestResult(int score) {
        this.score = score;
    }

    //Checks if this TestResult contains a warning
    public boolean containsWarning(int warning){
        return this.warnings.contains(warning);
    }

    //Adds a warning to this TestResult
    public void addWarning(int warning) {
        this.warnings.add(warning);
    }

    //GETTERS AND SETTERS
    public ArrayList<Integer> getWarnings() {
        return warnings;
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
