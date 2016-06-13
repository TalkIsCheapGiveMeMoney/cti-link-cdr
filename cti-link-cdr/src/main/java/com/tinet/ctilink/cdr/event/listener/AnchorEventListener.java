package com.tinet.ctilink.cdr.event.listener;

import com.tinet.ctilink.mq.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author fengwei //
 * @date 16/6/12 11:04
 */
public class AnchorEventListener {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageQueue anchorEventMessageQueue;

    public void handleMessage(String json, String channel) {
        if (logger.isInfoEnabled()) {
            logger.info(channel + " receive event : " + json);
        }
        if (json == null) {
            return;
        }

        try {
            anchorEventMessageQueue.sendMessage(json);
        } catch (Exception e) {
            logger.error("AnchorEventListener anchorEventMessageQueue.sendMessage error, ", e);
        }
    }
}
