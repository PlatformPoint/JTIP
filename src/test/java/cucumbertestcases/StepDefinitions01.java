package cucumbertestcases;

/**
 * Created by chq-gurmits on 4/28/2016.
 */
import com.platformpoint.automation.tfs.TfsUtil;
import cucumber.api.Scenario;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StepDefinitions01 {
    private Belly belly;
    private int waitingTime;

    static final Logger LOGGER = LoggerFactory.getLogger(StepDefinitions01.class);
    static long startTime;

    @cucumber.api.java.Before
    public static void setUp(Scenario scenario) throws Exception
    {

        LOGGER.info("Inside  Set Up ");
        startTime = System.nanoTime();
        LOGGER.info(System.getProperty("line.separator"));
        LOGGER.info("*************** Starting Test Execution  **************");
        LOGGER.info("Starting Automated test case  " + scenario.getName());
        LOGGER.info(System.getProperty("line.separator"));

    }


    @cucumber.api.java.After
    public static void cleanUp(Scenario scenario) throws Exception
    {
        long elapsedTime = System.nanoTime() - startTime;
        LOGGER.info(System.getProperty("line.separator"));
        LOGGER.info("*************** EXECUTION TIME  **************");
        LOGGER.info("Total execution time to execute test  (in seconds) : " + (elapsedTime / 1000000000.000));
        LOGGER.info(System.getProperty("line.separator"));
        TfsUtil.testExecutionTime = elapsedTime;
        String scenarioStatus=scenario.getStatus();
        String scenarioName=scenario.getName();
        LOGGER.info("Scenario Name = "+scenarioName);
        LOGGER.info("Scenario status = "+scenarioStatus);
        TfsUtil.updateTfsTestRun(scenarioName, scenarioStatus);
    }




    @Given("^I have (\\d+) cukes in my belly$")
    public void i_have_cukes_in_my_belly(int cukes) throws Throwable {
        belly = new Belly();
        belly.eat(cukes);
    }

    @When("^I wait (\\d+) hour$")
    public void i_wait_hour(int waitingTime) throws Throwable {
        this.waitingTime = waitingTime;
    }

    @Then("^my belly should (.*)$")
    public void my_belly_should_growl(String expectedSound) throws Throwable {
        String actualSound = belly.getSound(waitingTime);
        assertThat(actualSound, is(expectedSound));
    }
}