package junittestcases;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

public class TfsTest extends BaseJunitTFSTestClass {
    static final Logger LOGGER = LoggerFactory.getLogger(TfsTest.class);



    @Test
    public void logjunitTFSResults_test_001() throws URISyntaxException {
        //TestCaseName="Verify that Passed test cases are reported in TFS";
        //Pass the test - do nothing



    }

    @Test
    public void logjunitTFSResults_test_002() throws URISyntaxException {
        //Fail the test- do nothing
        Assert.fail();

    }



}
