package sqlfe.junit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sqlfe.general.*;
import sqlfe.util.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class BackEndTests extends AbstractTest{

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;
    private final BackEnd backEnd = new BackEnd();
    String backEndSubmissionPath;

    @Before
    public void setup() throws IOException {
        Utilities.forTesting = true;

        // code to create new folder and copy files
        // lt_s01.sql
        backEndSubmissionPath = mainFolderPath + "/files/test/";

        String submissionFileOriginalDir=mainFolderPath+"/files-sample-MySQL/";

        // create the test directory
        File theDir = new File(backEndSubmissionPath);
        if (!theDir.exists()){
            theDir.mkdirs();
        }

        // copy the submission of students into this file.
        for(int i=1;i<=6;i++) {
            String filename="lt_s0" + Integer.toString(i) + ".sql";
            Files.copy(Paths.get(submissionFileOriginalDir + filename), Paths.get(backEndSubmissionPath+filename), StandardCopyOption.REPLACE_EXISTING);
        }
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
            String evaluationFolderPath=  mainFolderPath + "/evaluations/";
            String gradesFileName = evaluationFolderPath + "AAA_grade_summary.out";

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
            String expectedAns="Assignment  : CS 260, Fall 2019, Lab Test\n" +
                    "\n" +
                    "Student 03: 74.84: 15, 16, 9.72, 10.8, 13.52, 6.8, 3,\n" +
                    "Student 02: 79.45: 15, 16, 13.95, 11.7, 16, 6.8, 0,\n" +
                    "Student 01: 102.1: 15, 16, 18, 17.1, 16, 17, 3,\n" +
                    "Student 05: 35.9: 5.25, 4, 5.4, 7.2, 6.4, 7.65, 0,\n" +
                    "Student 04: 77.5: 15, 4.8, 9.9, 18, 12.8, 17, 0,\n" +
                    "Student 06: 105: 15, 16, 18, 18, 16, 17, 5,\n";
            String actualAns="";
            for( String line:Files.readAllLines(Paths.get(gradesFileName))){
                actualAns+=line+"\n";
            }
            assertEquals(actualAns, expectedAns);

        }
        catch (Exception e) {
            System.out.println(e);
            fail();
        }
    }

    @After
    public void teardown () {
        // delete the folders and files created
        final File file = new File(backEndSubmissionPath);
        File[] list = file.listFiles();
        if (list != null) {
            for (File currentFile : list) {
                currentFile.delete();
            }
        }
        if (file.delete()) {
            System.setErr(originalErr);
        }
    }

}