package workwithprashant.karate;


import com.intuit.karate.RuntimeHook;
import com.intuit.karate.Suite;
import com.intuit.karate.core.FeatureRuntime;
import com.intuit.karate.core.ScenarioRuntime;
import com.intuit.karate.core.Step;
import com.intuit.karate.core.StepResult;
import org.slf4j.Logger;
import workwithprashant.utils.LogHelper;

import static workwithprashant.reporting.reportportal.ScenarioReporter.*;

/**
 * Reportportal Execution Hook.
 *
 * @author workwithprashant
 */
public class ReportportalHook implements RuntimeHook {
    // return false if the scenario / item should be excluded from the test-run

    /* Initialize the logger */
    private static final Logger log = LogHelper.getLogger();

    @Override
    public void afterFeature(FeatureRuntime fr) {
        try {
            if (fr.caller.depth <= 0) {
                handleAfterFeature(fr);
            }
        } catch (Exception ex) {
            log.error("Error while handling after feature event: {}", ex.getMessage(), ex);
        }
    }

    @Override
    public void afterScenario(ScenarioRuntime sr) {
        // EMPTY
    }

    @Override
    public void afterStep(StepResult result, ScenarioRuntime sr) {
        // EMPTY
    }

    @Override
    public void afterSuite(Suite suite) {
        try {
            stopLaunch(suite.buildResults());
        } catch (Exception ex) {
            log.error("Error while handling after all event: {}", ex.getMessage(), ex);
        }
    }

    @Override
    public boolean beforeFeature(FeatureRuntime fr) {
        try {
            if (fr.caller.depth <= 0) {
//                handleBeforeFeature(fr.feature);
                handleBeforeFeature(fr.featureCall.feature);
            }
        } catch (Exception ex) {
            log.error("Error while handling before feature event: {}", ex.getMessage(), ex);
        }

        return true;
    }

    @Override
    public boolean beforeScenario(ScenarioRuntime sr) {
        return true;
    }

    @Override
    public boolean beforeStep(Step step, ScenarioRuntime sr) {
        return true;
    }

    @Override
    public void beforeSuite(Suite suite) {
        try {
            startLaunch();
        } catch (Exception ex) {
            log.error("Error while handling before all event: {}", ex.getMessage(), ex);
        }
    }

}