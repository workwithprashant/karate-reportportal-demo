import com.dell.utils.LogHelper;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Collections;

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

        Runner.path("classpath:")
                .tags(Collections.singletonList("demo"))
                .parallel(1);

    }

}
