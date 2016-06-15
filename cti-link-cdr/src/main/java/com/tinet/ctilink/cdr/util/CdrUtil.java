package com.tinet.ctilink.cdr.util;

import com.tinet.ctilink.cdr.inc.CdrConst;
import com.tinet.ctilink.json.JSONArray;
import com.tinet.ctilink.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author fengwei //
 * @date 16/6/12 17:33
 */
public class CdrUtil {

    private static Logger logger = LoggerFactory.getLogger(CdrUtil.class);

    /**
     * 处理参数, 1.将空值删除, 2.数组转成List
     */
    public static JSONObject validateParam(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();
            // value is null, drop
            if (value == null || value.length == 0) {
                logger.debug(key + " value is null, drop it");
                continue;
            }

            //multi value
            if (value.length > 1) {
                List<String> valueList = new ArrayList<>();
                for (String val : value) {
                    if (StringUtils.isNotEmpty(val)) {
                        valueList.add(val);
                    }
                }
                if (valueList.size() == 1) {
                    jsonObject.put(key, valueList.get(0));
                } else if (valueList.size() > 1) {
                    jsonObject.put(key, valueList);
                } else {
                    logger.debug(key + " value is null, drop it");
                }
            } else {
                if (StringUtils.isEmpty(value[0])) {
                    logger.debug(key + " value is null, drop it");
                    continue;
                }
                jsonObject.put(key, value[0]);
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
