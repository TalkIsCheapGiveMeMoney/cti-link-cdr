package com.tinet.ctilink.cdr;

import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author fengwei //
 * @date 16/6/4 16:52
 */
public class QueueEventListener {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisService redisService;

    public void handleMessage(String json, String channel) {
        if (logger.isInfoEnabled()) {
            logger.info(channel + " receive event : " + json);
        }
        JSONObject event = JSONObject.fromObject(json);
        if (event == null) {
            return;
        }

        System.out.println(json);
    }
}
