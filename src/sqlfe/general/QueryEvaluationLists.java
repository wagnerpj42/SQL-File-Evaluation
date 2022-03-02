package sqlfe.general;

import sqlfe.sqltests.ISQLTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QueryEvaluationLists {
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

    public Map<String,QueryEvaluationLists> createQuestionNoToEvaluationMetricsMap(ArrayList<Question> questions) {

        Map<String,QueryEvaluationLists> questionNoToEvaluationMetrics= new HashMap<>();

        for (Question question:questions) {
            String questionName=question.getQNumStr();
            QueryEvaluationLists queryEvaluationLists= new QueryEvaluationLists();

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
