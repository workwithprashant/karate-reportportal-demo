package workwithprashant.reporting.reportportal;


import workwithprashant.utils.LogHelper;
import com.epam.reportportal.service.ReportPortal;
import com.epam.ta.reportportal.ws.model.attribute.ItemAttributesRQ;
import com.epam.ta.reportportal.ws.model.log.SaveLogRQ;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.intuit.karate.core.Feature;
import com.intuit.karate.core.ScenarioResult;
import com.intuit.karate.core.StepResult;
import com.intuit.karate.core.Tag;
import org.slf4j.Logger;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The type Utils.
 * Some of the code is referred from online resources
 *
 * @author workwithprashant
 */
public class Utils {

    /* Initialize the logger */
    private static final Logger LOGGER = LogHelper.getLogger();

    private Utils() {
        throw new AssertionError("No instances should exist for the class!");
    }

    /**
     * Extract attributes set.
     *
     * @param tags the tags
     * @return the set
     */
    public static Set<ItemAttributesRQ> extractAttributes(List<Tag> tags) {
        Set<ItemAttributesRQ> attributes = new HashSet<ItemAttributesRQ>();
        tags.forEach((tag) -> {
            attributes.add(new ItemAttributesRQ(null, tag.getName()));
        });
        return attributes;
    }

    /**
     * Gets uri.
     *
     * @param feature the feature
     * @return the uri
     */
    static String getURI(Feature feature) {
        return feature.getResource().getPrefixedPath().toString();
    }

    /**
     * Print call step result string.
     *
     * @param stepResult the step result
     * @param stepNumber the step number
     * @return the string
     */
    static String printCallStepResult(StepResult stepResult, int stepNumber) {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("\n\t\t\t");
            sb.append(String.format(String.format("\t\t\t\t  Step %d for calling feature file %%s\n%%s %%s", stepNumber), stepResult.getStep().getFeature().getNameForReport(), stepResult.getStep().getPrefix(), stepResult.getStep().getText()));
            if (stepResult.getErrorMessage() != null) {
                sb.append("\n\t\t\t**Error**\t\n --------------------------------------\n").append(stepResult.getErrorMessage() + "\n");
            }
            if (stepResult.getStepLog() != null) {
                sb.append("\n\t\t\t**Doc string**\t\n --------------------------------------\n").append(stepResult.getStepLog() + "\n");
            }
            sb.append("\n\t\t\t");
        } catch (Exception ex) {
            LOGGER.error("Exception wile printing step result", ex);
        }
        return sb.toString();
    }


    /**
     * Print scenario result string.
     *
     * @param scenarioResult the scenario result
     * @return the string
     */
    static String printScenarioResult(ScenarioResult scenarioResult) {
        StringBuilder sb = new StringBuilder();
        try {

            sb.append("\n\t\t[");
            sb.append("\n\t\tstartTime=" + scenarioResult.getStartTime());
            sb.append("\n\t\tendTime=" + scenarioResult.getEndTime());
            sb.append("\n\t\tfailureMessageForDisplay=" + scenarioResult.getFailureMessageForDisplay());
            sb.append("\n\t\tstepResultsSize=" + scenarioResult.getStepResults().size());
            sb.append("\n\t\tscenarioName=" + scenarioResult.getScenario().getName());
            sb.append("\n\t\tscenarioNameForReport=" + scenarioResult.getScenario().getNameAndDescription());
            sb.append("\n\t\tscenarioKeyword=" + scenarioResult.getScenario().getTagsEffective());
            sb.append("\n\t\tscenarioDescription=" + scenarioResult.getScenario().getDescription());
            sb.append("\n\t\tstepResults=[");
            for (StepResult stepResult : scenarioResult.getStepResults()) {
                sb.append(Utils.printStepResult(stepResult, 0));
            }
            sb.append("\n\t\t]");

        } catch (Exception ex) {
            LOGGER.error("Exception wile printing scenario result", ex);

        }
        return sb.toString();
    }

    /**
     * Print step result string.
     *
     * @param stepResult the step result
     * @param stepNumber the step number
     * @return the string
     */
    static String printStepResult(StepResult stepResult, int stepNumber) {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("\n\t\t\t\t");
            sb.append(String.format("%n\tStep %d for %s%n%s %s", stepNumber, stepResult.getStep().getFeature().getNameForReport(), stepResult.getStep().getPrefix(), stepResult.getStep().getText()));
            if (stepResult.getErrorMessage() != null) {
                sb.append("\n\t\t\t**Error**\t\n --------------------------------------\n").append(stepResult.getErrorMessage());
            }
            if (stepResult.getStepLog() != null) {
                sb.append("\n\t\t\t**Doc string**\t\n --------------------------------------\n").append(stepResult.getStepLog());
            }
            sb.append("\n\t\t\t");

        } catch (Exception ex) {
            LOGGER.error("Exception while printing step result", ex);
        }
        return sb.toString();
    }

    /**
     * Send log.
     *
     * @param message the message
     * @param level   the level
     * @param logTime step log time
     */
    static void sendLog(final String message, final String level, Date logTime) {

        if (Strings.isNullOrEmpty(message)) {
            return;
        }
        ReportPortal.emitLog((Function<String, SaveLogRQ>) itemUuid -> {
            SaveLogRQ rq = new SaveLogRQ();
            rq.setMessage(message);
            rq.setItemUuid(itemUuid);
            rq.setLevel(level);
            rq.setLogTime(logTime);
            return rq;
        });
    }

}
