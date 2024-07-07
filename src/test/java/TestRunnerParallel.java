import com.intuit.karate.core.Feature;
import workwithprashant.karate.ReportportalHook;
import workwithprashant.utils.LogHelper;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is to execute tests
 *
 * @author workwithprashant
 */
class TestRunnerParallel {

    /* Initialize the logger */
    private static final Logger log = LogHelper.getLogger();

    /**
     * This method is to launch tests in parallel as per thread counts
     *
     * @throws IOException the io exception
     */
    @Test
    void testParallel() throws IOException {
        log.info("+------------------------------------+");
        log.info("| Starting Functional Test Execution |");
        log.info("+------------------------------------+");

        Runner.path("classpath:features/users.feature")
                .tags("@demo")
                .hook(new ReportportalHook())
                .parallel(1);
    }

}
