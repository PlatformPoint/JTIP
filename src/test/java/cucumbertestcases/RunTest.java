package cucumbertestcases;
import com.expeditors.framework.cucumber.annotations.AfterSuite;
import com.expeditors.framework.cucumber.annotations.BeforeSuite;
import com.expeditors.framework.cucumber.annotations.ExtendedCucumberRunner;
import cucumber.api.CucumberOptions;
import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RunWith(ExtendedCucumberRunner.class)
@CucumberOptions(format = {"pretty", "html:target/cucumber-html-report"},
        features= {
                "src/test/resources/cucumbertestcases/CucumberTests01.feature"
        },
        glue="cucumbertestcases"
)
public class RunTest
{
    static final Logger LOGGER = LoggerFactory.getLogger(RunTest.class);
    public static BaseCucumberTFSTestClass baseClass= new BaseCucumberTFSTestClass();

   @BeforeSuite
    public static void setUp() throws Exception {
        LOGGER.info("Inside @BeforeSuite - SetUp");
        BaseCucumberTFSTestClass.setUpBeforeClassBase();
    }

    @AfterSuite
    public static void tearDown() throws Exception {
        LOGGER.info("Inside @AfterSuite - tearDown");
        BaseCucumberTFSTestClass.tearDownAfterClassBase();
    }


}
