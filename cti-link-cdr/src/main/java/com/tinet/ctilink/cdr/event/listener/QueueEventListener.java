package com.tinet.ctilink.cdr.event.listener;

import com.tinet.ctilink.mq.MessageQueue;
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
    private MessageQueue queueEventMessageQueue;

    public void handleMessage(String json, String channel) {
        if (logger.isInfoEnabled()) {
            logger.info(channel + " receive event : " + json);
        }
        if (json == null) {
            return;
        }

        try {
            queueEventMessageQueue.sendMessage(json);
        } catch (Exception e) {
            logger.error("QueueEventListener queueEventMessageQueue.sendMessage error, ", e);
        }
    }
}
