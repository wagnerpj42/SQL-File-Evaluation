/*
 * QueryEvaluationLists - class that holds query evaluation params.
 *
 * Created - 1-Mar-2022
 */
package sqlfe.general;

import sqlfe.sqltests.ISQLTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QueryEvaluationLists {

    // List that holds query evaluations params.
    final private ArrayList<ISQLTest> questionTests ;
    final private ArrayList<Integer> questionPcts  ;
    final private ArrayList<String> questionConditions ;

    // methods
    // constructors
    // all-args constructor
    public QueryEvaluationLists(ArrayList<ISQLTest> questionTests, ArrayList<Integer> questionPcts, ArrayList<String> questionConditions) {
        this.questionTests = questionTests;
        this.questionPcts = questionPcts;
        this.questionConditions = questionConditions;
    }

    // methods
    //default constructor
    public QueryEvaluationLists() {
        this.questionTests = new ArrayList<>();
        this.questionPcts = new ArrayList<>();
        this.questionConditions = new ArrayList<>();
    }

    public ArrayList<ISQLTest> getQuestionTests(){
        return this.questionTests;
    }

    public ArrayList<Integer> getQuestionPcts(){
        return this.questionPcts;
    }

    public ArrayList<String> getQuestionConditions(){
        return this.questionConditions;
    }

    /**
     * Create a map for the holding question number to metrics . This functions does preprocessing.
     * @param questions. A list that holds objects containing data.
     * @return the map holding data
     */
    public Map<String,QueryEvaluationLists> createQuestionNoToEvaluationMetricsMap(ArrayList<Question> questions) {


        Map<String,QueryEvaluationLists> questionNoToEvaluationMetrics= new HashMap<>();

        // iterate through the list of questions.
        for (Question question:questions) {
            // get the question number
            String questionName=question.getQNumStr();
            // create object to hold the data.
            QueryEvaluationLists queryEvaluationLists= new QueryEvaluationLists();

            // Get all the tests corresponding to the question.
            ArrayList<EvalComponentInQuestion> questionEvalComps = question.getTests();

            for (EvalComponentInQuestion questionEvalComp : questionEvalComps) {
                // get test names
                String currTestName = questionEvalComp.getEvalComponentName();
                currTestName = "sqlfe.sqltests." + currTestName;

                // make test object out of test name
                try {
                    Class<?> aClass = Class.forName(currTestName);
                    Object oTest = aClass.newInstance();
                    ISQLTest test = (ISQLTest) oTest;
                    queryEvaluationLists.questionTests.add(test);
                } catch (Exception e) {
                    System.out.println("exception in generating class object from name");
                }

                // get percents
                int currTestPct = questionEvalComp.getPercent();
                queryEvaluationLists.questionPcts.add(currTestPct);

                // get condition
                String currTestCondition = questionEvalComp.getCondition();
                queryEvaluationLists.questionConditions.add(currTestCondition);
            }    // end - for each test in question

            questionNoToEvaluationMetrics.put(questionName,queryEvaluationLists);

        }
        return questionNoToEvaluationMetrics;
    }


}
