package testngtestcases;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

public class TfsTest extends BasetestngTFSTestClass {
    static final Logger LOGGER = LoggerFactory.getLogger(TfsTest.class);



    @Test
    public void logtestngTFSResults_test_003() throws URISyntaxException {
        //TestCaseName="Verify that Passed test cases are reported in TFS";
        //Pass the test - do nothing

    }

    @Test
    public void logtestngTFSResults_test_004() throws URISyntaxException {
        //Fail the test- do nothing
        Assert.fail();

    }



}
