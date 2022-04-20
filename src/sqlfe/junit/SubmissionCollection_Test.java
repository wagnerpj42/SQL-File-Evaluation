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

public class SubmissionCollection_Test extends AbstractTest{
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;
    private final SubmissionCollection subColl = new SubmissionCollection();
    private final BackEnd backEnd = new BackEnd();
    String backEndSubmissionPath; String evaluationsFolderPath; String submissionFileOriginalDir;


    @Before
    public void setup() throws IOException {
        // set up for testing
        Utilities.forTesting = true;
        evaluationsFolderPath = "C://Users//jhard//OneDrive//Documents//Object_Oriented_Development//final project//SQL-File-Evaluation" + "/evaluations/";
        backEndSubmissionPath = "C://Users//jhard//OneDrive//Documents//Object_Oriented_Development//final project//SQL-File-Evaluation" + "/files/test/";
        submissionFileOriginalDir = "C://Users//jhard//OneDrive//Documents//Object_Oriented_Development//final project//SQL-File-Evaluation"+"/files-sample-MySQL/";

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
            File theDir = new File(backEndSubmissionPath);
            // main folder path added /SQL-File-Evaluation/ twice so this was hardcoded for the specific PC
            if (!theDir.exists()){
                theDir.mkdirs();
            }


            backEnd.createTestObject(testDAO, "C://Users//jhard//OneDrive//Documents//Object_Oriented_Development//final project//SQL-File-Evaluation");

            Assignment a = backEnd.createAssignment("C://Users//jhard/OneDrive/Documents/Object_Oriented_Development/final project/SQL-File-Evaluation/assignmentProperties-MySQL/");
            subColl.getAllFiles(backEndSubmissionPath, evaluationsFolderPath, a.getAssignmentName());

            // test getAllFiles method
//            assertEquals(6,subColl.getTotalSubmissions());
            System.out.println(subColl.getSubmissions());
            //assertSame("",subColl.getSubmissions().)
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
