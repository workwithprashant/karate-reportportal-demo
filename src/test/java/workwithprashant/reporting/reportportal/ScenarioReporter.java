package workwithprashant.reporting.reportportal;


import workwithprashant.utils.LogHelper;
import com.epam.reportportal.listeners.ListenerParameters;
import com.epam.reportportal.service.Launch;
import com.epam.reportportal.service.ReportPortal;
import com.epam.ta.reportportal.ws.model.FinishExecutionRQ;
import com.epam.ta.reportportal.ws.model.FinishTestItemRQ;
import com.epam.ta.reportportal.ws.model.StartTestItemRQ;
import com.epam.ta.reportportal.ws.model.attribute.ItemAttributesRQ;
import com.epam.ta.reportportal.ws.model.launch.StartLaunchRQ;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.intuit.karate.Results;
import com.intuit.karate.core.*;
import io.reactivex.Maybe;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Scenario Reporter
 * Some of the code is referred from online resources
 *
 * @author workwithprashant
 */
public class ScenarioReporter {

    private static final String COLON_INFIX = ": ";
    private static final String SKIPPED_ISSUE_KEY = "skippedIssue";
    /* Initialize the logger */
    private static final Logger LOGGER = LogHelper.getLogger();
    private static final String INFO_LEVEL = "INFO";
    private static final String ERROR_LEVEL = "ERROR";
    private static final String PASSED = "passed";
    private static final String FAILED = "failed";
    private static final Map<String, Date> featureStartDateMap = Collections.synchronizedMap(new HashMap<>());
    private static ScenarioReporter scenarioReporter;
    private static Supplier<Launch> launch;
    private static String featureNamePrefix;

    /**
     * Build start feature rq start test item rq.
     *
     * @param featureResult the feature result
     * @return the start test item rq
     */
    public static StartTestItemRQ buildStartFeatureRq(FeatureResult featureResult) {
        Feature feature = featureResult.getFeature();
        StartTestItemRQ rq = new StartTestItemRQ();
        if (!isNullOrEmpty(feature.getName())) {
            rq.setDescription(feature.getName());
        }
        if (feature.getTags() != null && !feature.getTags().isEmpty()) {
            Set<ItemAttributesRQ> attributes = Utils.extractAttributes(feature.getTags());
            rq.setAttributes(attributes);
        }
        String featureUri = Utils.getURI(feature);
        rq.setName(FilenameUtils.getBaseName(featureUri));
        if (featureStartDateMap.containsKey(featureUri)) {
            rq.setStartTime(featureStartDateMap.get(featureUri));
        } else {
            rq.setStartTime(Calendar.getInstance().getTime());
        }
        rq.setType("TEST");
        return rq;
    }

    /**
     * Build start launch rq start launch rq.
     *
     * @param parameters the parameters
     * @return the start launch rq
     */
    protected static StartLaunchRQ buildStartLaunchRq(ListenerParameters parameters) {

        StartLaunchRQ rq = new StartLaunchRQ();
        rq.setName(parameters.getLaunchName());
        rq.setStartTime(Calendar.getInstance().getTime());
        rq.setMode(parameters.getLaunchRunningMode());
        HashSet<ItemAttributesRQ> attributes = new HashSet<>(parameters.getAttributes());
        rq.setAttributes(attributes);
        rq.setDescription(parameters.getDescription());
        rq.setRerun(parameters.isRerun());
        if (!isNullOrEmpty(parameters.getRerunOf())) {
            rq.setRerunOf(parameters.getRerunOf());
        }
        Boolean skippedAnIssue = parameters.getSkippedAnIssue();
        ItemAttributesRQ skippedIssueAttr = new ItemAttributesRQ();
        skippedIssueAttr.setKey(SKIPPED_ISSUE_KEY);
        skippedIssueAttr.setValue(skippedAnIssue == null ? "true" : skippedAnIssue.toString());
        skippedIssueAttr.setSystem(true);
        attributes.add(skippedIssueAttr);
        rq.setAttributes(attributes);
        return rq;
    }

    /**
     * Build start scenerio rq start test item rq.
     *
     * @param scenarioResult the scenario result
     * @return the start test item rq
     */
    protected static StartTestItemRQ buildStartScenarioRq(ScenarioResult scenarioResult) {
        StartTestItemRQ rq = new StartTestItemRQ();
        rq.setDescription(scenarioResult.getScenario().getDescription());
        rq.setName(scenarioResult.getScenario().getName());
        rq.setStartTime(new Date(scenarioResult.getStartTime()));
        rq.setType("STEP");
        return rq;
    }

    /**
     * Build stop feature rq finish test item rq.
     *
     * @param featureResult the feature result
     * @return the finish test item rq
     */
    public static FinishTestItemRQ buildStopFeatureRq(FeatureResult featureResult) {
        Date now = Calendar.getInstance().getTime();
        FinishTestItemRQ rq = new FinishTestItemRQ();
        rq.setEndTime(now);
        rq.setStatus(getFeatureStatus(featureResult));
        return rq;
    }

    private static FinishTestItemRQ buildStopScenarioRq(ScenarioResult scenarioResult) {
        Date now = Calendar.getInstance().getTime();
        FinishTestItemRQ rq = new FinishTestItemRQ();
        String featureUri = Utils.getURI(scenarioResult.getScenario().getFeature());
        rq.setEndTime(new Date(scenarioResult.getEndTime()));
        rq.setStatus(getScenarioStatus(scenarioResult));
        return rq;
    }

    private static StringBuilder callResultLog(List<FeatureResult> callLog) {
        StringBuilder result = new StringBuilder();
        int n = 1;
        for (FeatureResult i : callLog) {
            for (ScenarioResult j : i.getScenarioResults()) {
                for (StepResult k : j.getStepResults()) {
                    result.append(Utils.printCallStepResult(k, n));
                    n++;
                    if (k.getCallResults() != null) {
                        result.append(callResultLog(k.getCallResults()));
                    }
                }

            }
        }
        return result;
    }

    private static String getFeatureStatus(FeatureResult featureResult) {
        StatusEnum status = StatusEnum.SKIPPED;
        if (featureResult.getScenarioCount() > 0) {
            if (featureResult.isFailed()) {
                status = StatusEnum.FAILED;
            } else {
                status = StatusEnum.PASSED;
            }
        }
        return status.toString();
    }

    private static String getLaunchStatus(Results results) {
        StatusEnum status = StatusEnum.SKIPPED;
        if (results.getScenariosTotal() > 0) {
            if (results.getFailCount() > 0) {
                status = StatusEnum.FAILED;
            } else {
                status = StatusEnum.PASSED;
            }
        }
        return status.toString();
    }

    /**
     * Gets root item id.
     *
     * @return the root item id
     */
    protected static Maybe<String> getRootItemId() {
        return null;
    }

    private static String getScenarioStatus(ScenarioResult scenarioResult) {
        StatusEnum status = StatusEnum.SKIPPED;
        if (scenarioResult.getStepResults() != null && scenarioResult.getStepResults().size() > 0) {
            if (scenarioResult.getFailedStep() != null) {
                status = StatusEnum.FAILED;
            } else {
                status = StatusEnum.PASSED;
            }
        }
        return status.toString();
    }

    /**
     * Handle after feature.
     *
     * @param fr the fr
     * @throws InterruptedException the interrupted exception
     */
    public static void handleAfterFeature(FeatureRuntime fr) throws InterruptedException {
        FeatureResult featureResult = fr.result;
        String featureUri = Utils.getURI(featureResult.getFeature());
        String featureName = FilenameUtils.getBaseName(featureUri);

        if (featureResult.getScenarioCount() <= 0) {
            LOGGER.trace("Dropping feature event as scenario count is zero featureName={} ", featureName);
            return;
        }

        if (!isNullOrEmpty(featureNamePrefix)) {
            if (!featureName.startsWith(featureNamePrefix)) {
                LOGGER.trace("Dropping feature event due to feature name prefix mismatch featureName={} ", featureName);
                return;
            }
        }
        StartTestItemRQ startTestItemRQ = buildStartFeatureRq(featureResult);
        Maybe<String> featureId = launch.get().startTestItem(getRootItemId(), startTestItemRQ);
        for (ScenarioResult scenarioResult : featureResult.getScenarioResults()) {
            Maybe<String> scenarioId = launch.get().startTestItem(featureId, buildStartScenarioRq(scenarioResult));
            List<StepResult> stepResults = scenarioResult.getStepResults();
            int stepNumber = 1;
            long scnProgressTime = scenarioResult.getStartTime();
            for (StepResult stepResult : stepResults) {
                // Measuring time duration for steps
                scnProgressTime += TimeUnit.NANOSECONDS.toMillis(stepResult.getResult().getDurationNanos());
                String status = stepResult.getResult().getStatus();
                String logLevel = PASSED.equals(status) ? INFO_LEVEL : ERROR_LEVEL;
                String logs = Utils.printStepResult(stepResult, stepNumber);
                if (stepResult.getCallResults() != null) {
                    StringBuilder callResults = callResultLog(stepResult.getCallResults());
                    logs += callResults;
                }
                stepNumber++;
                sendLog(logs, logLevel, new Date(scnProgressTime));
            }

            FinishTestItemRQ finishTestItemRQ = buildStopScenarioRq(scenarioResult);
            launch.get().finishTestItem(scenarioId, finishTestItemRQ);

        }

        FinishTestItemRQ finishTestItemRQ = buildStopFeatureRq(featureResult);
        launch.get().finishTestItem(featureId, finishTestItemRQ);

    }

    /**
     * Handle before feature.
     *
     * @param feature the feature
     */
    public static void handleBeforeFeature(Feature feature) {
        String featureUri = Utils.getURI(feature);
        String featureName = FilenameUtils.getBaseName(featureUri);
        if (!isNullOrEmpty(featureNamePrefix)) {
            if (!featureName.startsWith(featureNamePrefix)) {
                LOGGER.trace("Dropping feature event due to feature name prefix mismatch featureName={} ", featureName);
                return;
            }
        }
        featureStartDateMap.put(featureUri, Calendar.getInstance().getTime());

    }

    private static void sendLog(final String message, final String level, Date logTime) {
        Utils.sendLog(message, level, logTime);
    }


    /**
     * Start launch.
     */
    public static void startLaunch() {

        launch = Suppliers.memoize(new Supplier<Launch>() {
            @Override
            public Launch get() {
                final ReportPortal reportPortal = ReportPortal.builder().build();
                StartLaunchRQ rq = buildStartLaunchRq(reportPortal.getParameters());
                return reportPortal.newLaunch(rq);
            }
        });

        launch.get().start();
    }

    /**
     * Stop launch.
     *
     * @param results the results
     */
    public static void stopLaunch(Results results) {
        FinishExecutionRQ finishLaunchRq = new FinishExecutionRQ();
        finishLaunchRq.setEndTime(Calendar.getInstance().getTime());
        finishLaunchRq.setStatus(getLaunchStatus(results));
        launch.get().finish(finishLaunchRq);
    }

}

