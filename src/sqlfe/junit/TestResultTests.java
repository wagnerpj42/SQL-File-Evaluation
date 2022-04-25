package sqlfe.junit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sqlfe.general.TestResult;
import sqlfe.util.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestResultTests extends AbstractTest {

    private final TestResult testresult = new TestResult();
    ArrayList<Integer> c_warning = new ArrayList<>(1);
    ArrayList<String[]> e_row = new ArrayList(Arrays.asList("1","2","3"));
    ArrayList<String[]> m_row = new ArrayList(Arrays.asList("4","5","6"));

    @Before
    public void setup() throws IOException {
        Utilities.forTesting = true;
        // setting the value of parameters :- score,warning, extra rows and missing rows
        c_warning.add(22);
        testresult.addWarning(22);
        testresult.setScore(23);
        testresult.setExtraRows(e_row);
        testresult.setMissingRows(m_row);
    }

    @Test
    public void test_testResults(){
        try {
            // check the values set in setter getter is working fine
            assertEquals(c_warning, testresult.getWarnings());
            assertEquals(23, testresult.getScore());
            assertEquals(e_row, testresult.getExtraRows());
            assertEquals(m_row, testresult.getMissingRows());

        }
        catch (Exception e) {
            System.out.println(e);
            fail();

        }


    }

    @After
    public void teardown () {
        e_row=null;
        m_row=null;
        testresult.setScore(0);
        testresult.setExtraRows(e_row);
        testresult.setMissingRows(m_row);
    }

}
