package jit.wxs.jdbc.horizontal.sharding.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author jitwxs
 * @date 2020年02月15日 14:07
 */
public class ThreadUtils {

    public static String trace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if(stackTrace.length <= 0) {
            return StringUtils.EMPTY;
        }

        StringBuilder sb = new StringBuilder(1024);
        for(StackTraceElement e : stackTrace) {
            sb.append(String.format("%s: %s(): %s", e.getClassName(), e.getMethodName(), e.getLineNumber())).append("\n");
        }
        return sb.toString();
    }
}
