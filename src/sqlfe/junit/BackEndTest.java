package sqlfe.junit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sqlfe.general.*;
import sqlfe.util.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class BackEndTest extends AbstractTest{

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;
    private final BackEnd backEnd = new BackEnd();

    @Before
    public void setup() {
        Utilities.forTesting = true;
        backEnd.createTestObject(testDAO, mainFolderPath);
        System.setErr(new PrintStream(errContent));
    }

    // for evaluate: invalid assignmentPropertiesFileName, submissionFolderPath, evaluationFolderPath, dao,
    // SubmissionCollection sc,ArrayList<Submission> sa, ArrayList<Question> questions - invalid
    // single line output format has marks for all questions.
    @Test
    public void testEvaluate() {
        try {
            backEnd.evaluate();

            // test the number of lines in the output file. Should be number of files  + 2.
            String backEndSubmissionPath = mainFolderPath + "/files/test/";
            String evaluationFolderPath=  mainFolderPath + "/evaluations/";
            String gradesFileName = evaluationFolderPath + "AAA_grade_summary.out";

            String gradesTestFile = evaluationFolderPath + "/test/grade_summary_test.out";

            int numberOfFiles = Objects.requireNonNull(new File(backEndSubmissionPath).list()).length;
            long lineCount;
            try (Stream<String> stream = Files.lines(Paths.get(gradesFileName), StandardCharsets.UTF_8)) {
                lineCount = stream.count();
            }
            assertEquals(lineCount,numberOfFiles+2);

            // test for each submission the output should contain grade for each question
            Assignment a = backEnd.createAssignment(mainFolderPath + "/assignmentProperties-MySQL");

            Map<Integer, ArrayList<Question>>  questionToAnswer = new HashMap<>();

            // iterate through the list of questions.
            for( Question question: a.getQuestions()){
                // get the question number
                Integer questionNo = Integer.parseInt(String.valueOf(question.getQNumStr().charAt(0)));

                //Add the question to the map.
                questionToAnswer.putIfAbsent(questionNo, new ArrayList<>());
                questionToAnswer.get(questionNo).add(question);
            }

            int questions= questionToAnswer.size();
            Scanner scanner = new Scanner(new File(gradesFileName));
            int lineNo=0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(lineNo>=2){
                    String[] arrOfStr = line.split(":");
                    assertEquals(arrOfStr.length,3);
                    int freqComma =0;
                    for(int i=0;i<arrOfStr[2].length();i++){
                        if(arrOfStr[2].charAt(i)==',')
                            freqComma++;
                    }
                    assertEquals(freqComma,questions);
                }
                lineNo++;
            }

            // invalid folder paths
            backEnd.setSubmissionFolderPath("invalidPath");
            backEnd.setEvaluationFolderPath("invalidPath");
            backEnd.evaluate();
            assertEquals("Error in reading submission collection\n".trim(), errContent.toString().trim());

            errContent.reset();
            backEnd.createTestObject(testDAO, mainFolderPath);


            // invalid DAO object
            IDAO invalidDAO	= new MySQL80DataAccessObject("localhost", "3306", "sqlf", "roo", "", true);
            backEnd.createTestObject(invalidDAO, mainFolderPath);
            backEnd.evaluate();
            assertEquals("Invalid database properties\n".trim(), errContent.toString().trim());

            backEnd.createTestObject(testDAO, mainFolderPath);

            //Test for grade submission, the output file should match are predefined one to pass
            backEnd.evaluate();
            assertEquals(Files.readAllLines(Paths.get(gradesTestFile)), Files.readAllLines(Paths.get(gradesFileName)));

//Reflection demo
//            Field privateStringField= backEnd.getClass().getDeclaredField("abc");
//            privateStringField.setAccessible(true);
//            boolean height = privateStringField.getBoolean(backEnd);

//            Method method = backEnd.getClass().getDeclaredMethod("createQuestionToAnswer", ArrayList.class);
//            method.setAccessible(true);
//            Map<Integer, ArrayList<Question>> mp= (Map<Integer, ArrayList<Question>>) method.invoke(backEnd, a.getQuestions());
//

        }
        catch (Exception e) {
            System.out.println(e);
            fail();
        }
    }

    @After
    public void teardown () {
        System.setErr(originalErr);
    }





}
