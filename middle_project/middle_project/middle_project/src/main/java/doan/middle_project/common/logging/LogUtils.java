package doan.middle_project.common.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogUtils {
    static final Logger LOGGER = LogManager.getLogger(LogUtils.class);

    public static Logger getLog() {
        return LOGGER;
    }
}
