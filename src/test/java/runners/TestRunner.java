package runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/functionalTests",
        glue = "stepDefinitions",
        plugin= {"pretty", "html:target/cucumber", "summary"}

)
public class TestRunner {
}
