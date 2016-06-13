package com.tinet.ctilink.cdr.util;

import com.tinet.ctilink.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/6/12 17:33
 */
public class CdrUtil {

    private static Logger logger = LoggerFactory.getLogger(CdrUtil.class);

    public static JSONObject handleParam(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String[] value = entry.getValue();
            // value is null, drop
            if (value == null || value.length == 0) {
                logger.error("handleParam, " + entry.getKey() + " value is null, drop it");
                continue;
            }

            //multi value
            if (value.length > 1) {
                Set<String> valueSet = new HashSet<>();
                for (String val : value) {
                    if (StringUtils.isNotEmpty(val)) {
                        valueSet.add(val);
                    }
                }
                if (valueSet.size() == 1) {
                    jsonObject.put(entry.getKey(), valueSet.toArray()[0].toString());
                } else if (valueSet.size() > 1) {
                    jsonObject.put(entry.getKey(), valueSet);
                } else {
                    logger.error("handleParam, " + entry.getKey() + " value is null, drop it");
                }
            } else {
                if (StringUtils.isEmpty(value[0])) {
                    logger.error("handleParam, " + entry.getKey() + " value is null, drop it");
                    continue;
                }
                jsonObject.put(entry.getKey(), value[0]);
            }
        }
        return jsonObject;
    }

    public static boolean checkRequiredParam(Map<String, String[]> requestParams, String[] requiredParam) {
        for (String param : requiredParam) {
            String[] value = requestParams.get(param);
            if (value == null || value.length == 0
                    || StringUtils.isEmpty(value[0])) {
                return false;
            }
        }
        return true;
    }
}
