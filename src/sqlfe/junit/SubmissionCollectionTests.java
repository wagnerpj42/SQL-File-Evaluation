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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class SubmissionCollectionTests extends AbstractTest{
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;
    private final SubmissionCollection subColl = new SubmissionCollection();
    private final BackEnd backEnd = new BackEnd();
    String backEndSubmissionPath; String evaluationsFolderPath; String submissionFileOriginalDir;
    String expecHash;


    @Before
    public void setup() throws IOException {
        // set up for testing
        Utilities.forTesting = true;
        evaluationsFolderPath = mainFolderPath + "/evaluations/";
        backEndSubmissionPath = mainFolderPath + "/files/test/";
        submissionFileOriginalDir = mainFolderPath+"/files-sample-MySQL/";

        File theDir = new File(backEndSubmissionPath);
        if (!theDir.exists()){
            theDir.mkdirs();
        }

        // copy the submission of students into this file.
        for(int i=1;i<=6;i++) {
            String filename = "lt_s0" + Integer.toString(i) + ".sql";
            Files.copy(Paths.get(submissionFileOriginalDir + filename), Paths.get(backEndSubmissionPath+filename), StandardCopyOption.REPLACE_EXISTING);
        }

        // error init
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void testSubmissionCollection() {
        try {

            backEnd.createTestObject(testDAO, "C://Users//jhard//OneDrive//Documents//Object_Oriented_Development//final project//SQL-File-Evaluation");
            Assignment a = backEnd.createAssignment("C://Users//jhard/OneDrive/Documents/Object_Oriented_Development/final project/SQL-File-Evaluation/assignmentProperties-MySQL/");
            subColl.getAllFiles(backEndSubmissionPath, evaluationsFolderPath, a.getAssignmentName());

            // test getAllFiles method
            assertEquals(6,subColl.getTotalSubmissions());

            // test hashCode method
            assertEquals(937134617,subColl.hashCode());

            // test submission name
            List<Submission> submissions = subColl.getSubmissions();
            String[] fileNames = new String[] {"lt_s01.sql", "lt_s02.sql", "lt_s03.sql", "lt_s04.sql", "lt_s05.sql", "lt_s06.sql"};
            Set<String> fileNamesSet = new HashSet<>(Arrays.asList(fileNames));
            for(Submission i:submissions) {
                String fileName = i.getSubmissionFileName();
                assertTrue(fileNamesSet.contains(fileName));
            }
            System.out.println(subColl.getSubmissionMarks());
            //assertSame("lt_s01.sql",subColl.getSubmissions());
      }
        catch(Exception e){
            System.out.println(e);
            fail();
        }
    }

    @After
    public void teardown () {
       final File file = new File(backEndSubmissionPath);
       File[] list = file.listFiles();
       //System.out.println(list.length);
       if (list.length != 0) {
           for (File currentFile : list) {
               currentFile.delete();
           }
          if (file.delete()) {
              System.setErr(originalErr);
          }
       }

    }

}
