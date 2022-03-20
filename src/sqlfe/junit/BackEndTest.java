package sqlfe.junit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sqlfe.general.Assignment;
import sqlfe.general.BackEnd;
import sqlfe.general.FrontEnd;
import sqlfe.util.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.fail;

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

    @Test
    public void testGoThroughAllSubmissions() {
        try {
            backEnd.evaluate();
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
