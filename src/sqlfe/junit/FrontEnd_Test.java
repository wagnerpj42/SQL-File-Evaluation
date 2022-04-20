package sqlfe.junit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sqlfe.general.*;
import sqlfe.util.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


import static org.junit.Assert.*;


public class FrontEnd_Test extends AbstractTest {

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;
    private final FrontEnd frontEnd = new FrontEnd();
    private final BackEnd backEnd = new BackEnd();
    String frontEndSubmissionPath;

    @Before
    public void setup()  {
        // set up for testing
        Utilities.forTesting = true;

        // setters
        frontEnd.setDbmsChoice("SQL");
        frontEnd.setDbmsHost("host");
        frontEnd.setDbmsPort("port");
        frontEnd.setDbmsSystemID("ID");
        frontEnd.setDbmsUsername("Username");
        frontEnd.setDbmsPassword("Password");
        frontEnd.setEvaluationFolder("Folder");
        frontEnd.setAssignPropFile("PropFile");
        frontEnd.setABackEnd(backEnd);

        // error init
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void testFrontEnd() {
        try {
            // testing getters
            assertSame("SQL", frontEnd.getDbmsChoice());
            assertSame("host",frontEnd.getDbmsHost());
            assertSame("port",frontEnd.getDbmsPort());
            assertSame("ID",frontEnd.getDbmsSystemID());
            assertSame("Username",frontEnd.getDbmsUsername());
            assertSame("Password",frontEnd.getDbmsPassword());
            assertSame("Folder",frontEnd.getEvaluationFolder());
            assertSame("PropFile",frontEnd.getAssignPropFile());
            assertEquals(backEnd,frontEnd.getABackEnd());
        }
        catch(Exception e){
                System.out.println(e);
                fail();
            }
    }

    @After
    public void teardown () {
        // putting dbms choice back to original value
        frontEnd.setDbmsChoice(null);
        frontEnd.setDbmsHost(null);
        frontEnd.setDbmsPort(null);
        frontEnd.setDbmsSystemID(null);
        frontEnd.setDbmsUsername(null);
        frontEnd.setDbmsPassword(null);
        frontEnd.setEvaluationFolder(null);
        frontEnd.setAssignPropFile(null);
        frontEnd.setABackEnd(null);
        // putting system error back to original error
        System.setErr(originalErr);
    }

}





