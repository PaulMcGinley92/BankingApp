package ie.atu.sw;

/**
 * JUnit 5 test suite for the BankingApp project.
 *
 * This suite runs all test classes specified in the
 * @SelectClasses annotation. Additional test classes
 * can be added here if the project grows.
 */

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BankingAppTest.class 
})
public class AllTestsSuite {
    
}