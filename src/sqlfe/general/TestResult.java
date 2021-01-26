package sqlfe.general;

import java.util.ArrayList;

public class TestResult {
	// data
	//enumeration of warning types (so far there is only one defined)
    public static int MISMATCHED_SCORE_WARNING = 0;

    private ArrayList<Integer> warnings;    //list of all warnings a test may have issued. NOTE: See enumeration above
    private int score;                      //calculated score of a test
    private ArrayList<String[]> missingRows;//if applicable, number of missing rows (student failed to supply)
    private ArrayList<String[]> extraRows;  //if applicable, number of extra rows (student should not have supplied)

    // methods
    // constructors
    // -- default constructor
    public TestResult() {
        this.warnings = new ArrayList<Integer>();
    	this.score = 0;
        this.missingRows = null;
        this.extraRows = null;    	
    }

    // -- all-args constructor
    public TestResult(int score, ArrayList<String[]> missingRows, ArrayList<String[]> extraRows) {
        this.warnings = new ArrayList<Integer>();
        this.score = score;
        this.missingRows = missingRows;
        this.extraRows = extraRows;
    }

    // -- one-arg constructor starting with score
    public TestResult(int score) {
        this.warnings = new ArrayList<Integer>();
    	this.score = score;
        this.missingRows = null;
        this.extraRows = null;
    }

    // containsWarning - Checks if this TestResult contains a warning
    public boolean containsWarning(int warning){
        return this.warnings.contains(warning);
    }

    // addWarning - Adds a warning to this TestResult
    public void addWarning(int warning) {
    	//System.out.println("This testResult object is: " + this);
    	//System.out.println("This object's warnings is: " + this.warnings);
        this.warnings.add(warning);
    }

    // getters and setters
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
}	// end - class TestResult
