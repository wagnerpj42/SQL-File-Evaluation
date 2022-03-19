package sqlfe.junit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.fail;

public class BackEndTest extends AbstractTest{

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    @Before
    public void setup() {
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void testGoThroughAllSubmissions() {
        try{


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
