package sqlfe.junit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sqlfe.general.Assignment;
import sqlfe.general.BackEnd;
import sqlfe.util.Utilities;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AssignmentTests extends AbstractTest {

    private  BackEnd backEnd ;

    @Before
    public void setup() throws IOException {
        Utilities.forTesting = true;
        backEnd = new BackEnd();
        String testProperties="Assignment  : CS 260, Fall 2019, Lab Test\n" +
                "\n" +
                "@. (15 points)\n"+
                "SELECT CustID, Fname, Lname, AccClosedDate\n"+
                "FROM Customer C\n"+
                "JOIN Account A ON A.Customer = C.CustID\n"+
                "WHERE AccStatus = 'Closed'\n"+
                "AND AccOpenLocation = 'Central'\n"+
                "AND AccClosedDate >= '2017-03-01'\n";

        String testQuestions = "CS 260, Fall 2019, Lab Test\n" +
                "\n" +
                "1. (15 points)\n" +
                "SELECT CustID, Fname, Lname, AccClosedDate\n" +
                "FROM Customer C\n" +
                "JOIN Account A ON A.Customer = C.CustID\n" +
                "WHERE AccStatus = 'Closed'\n" +
                "AND AccOpenLocation = 'Central' \n" +
                "AND AccClosedDate >= '2017-03-01';\n" +
                "\n" +
                "CondCompiles 10 \"\"\n" +
                "CondBasicContent 15 \"\"\n" +
                "TestRowCount 10\n" +
                "TestColumnCount 10\n" +
                "CondTableCount 10 \" == 2\"\n" +
                "CondWhereCount 10 \" >= 1\"\n" +
                "TestResultSetEqualContent 35\n" +
                "\n" +
                "\n" +
                "2-1. (16 points) \n" +
                "SELECT AccOpenLocation, AccStatus, COUNT(*) AS \"Location/Status Ct.\"\n" +
                "FROM Account\n" +
                "GROUP BY AccOpenLocation, AccStatus\n" +
                "ORDER BY AccOpenLocation, AccStatus;\n" +
                "\n" +
                "CondCompiles 15 \"\"\n" +
                "CondBasicContent 10 \"\"\n" +
                "CondGroupByCount 10 \" >= 1\"\n" +
                "CondOrderByCount 10 \" >= 1\"\n" +
                "TestColumnCount 10\n" +
                "TestResultSetEqualContent 45";

        BufferedWriter writer;

        try {
            // creating file to test regular expression of questions and assignment name
            writer = new BufferedWriter(new FileWriter("TestAssignmentProperties.txt"));
            writer.write(testProperties);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
          // creating file to test correct question count and question points of each question.
          writer = new BufferedWriter(new FileWriter("TestAssignmentQuestions.txt"));
          writer.write(testQuestions);
          writer.close();

        } catch (IOException e) {
          e.printStackTrace();
        }


    }

    @Test
    public void testAssign() {
        try {
            // To check whether regular expression for question number is working or not.
            Assignment a = backEnd.createAssignment( mainFolderPath+ "/TestAssignmentProperties.txt");
            assertEquals(0,a.getQuestions().size());

            // To check whether Assignment name is correctly set or not.
            assertEquals("Assignment  : CS 260, Fall 2019, Lab Test",a.getAssignmentName());

            // To check each question with correct format is correctly captured and total number of questions are correctly updated or not.
            Assignment b = backEnd.createAssignment( mainFolderPath+ "/TestAssignmentQuestions.txt");
            assertEquals(2,b.getQuestions().size());

            // To check if the question points of each question is correctly captured or not.
            assertEquals(15,b.getQuestions().get(0).getQuestionPoints());
            assertEquals(16,b.getQuestions().get(1).getQuestionPoints());

        }

        catch (Exception e) {
            System.out.println(e);
            fail();

        }
    }

    @After
    public void teardown () {
        File file = new File(mainFolderPath + "/TestAssignmentProperties.txt");
        file.delete();

        file = new File(mainFolderPath + "/TestAssignmentQuestions.txt");
        file.delete();

        }
    }