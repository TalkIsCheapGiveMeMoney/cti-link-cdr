package com.tinet.ctilink.cdr.service;

import com.tinet.ctilink.aws.AwsDynamoDBService;
import com.tinet.ctilink.cdr.runnable.AnchorEventWorker;
import com.tinet.ctilink.cdr.runnable.CdrWorker;
import com.tinet.ctilink.json.JSONObject;
import com.tinet.ctilink.mq.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author fengwei //
 * @date 16/6/12 19:29
 */
@Component
public class AnchorEventService extends Thread {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageQueue anchorEventMessageQueue;

    @Autowired
    private AwsDynamoDBService awsDynamoDBService;

    private ExecutorService pool = Executors.newFixedThreadPool(50);

    private volatile boolean shutdown = false;

    @Override
    public void run() {
        if (logger.isInfoEnabled()) {
            logger.info(this.getName() + " thread start!");
        }
        while (!shutdown) {
            try {
                List<JSONObject> dataList = anchorEventMessageQueue.receiveMessage(JSONObject.class);
                //一次取出多条
                if (dataList != null && !dataList.isEmpty()) {
                    for (JSONObject data : dataList) {
                        try {
                            pool.submit(new AnchorEventWorker(awsDynamoDBService, data));
                        } catch (Exception e) {
                            logger.error("pool.submit error, ", e);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("receiveMessage error, ", e);
            }
        }
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public synchronized void shutdown() {
        if (logger.isInfoEnabled()) {
            logger.info(this.getName() + " thread shutdown!");
        }
        shutdown = true;
        //关闭线程池
        pool.shutdown();
    }
}
