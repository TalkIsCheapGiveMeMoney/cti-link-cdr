package com.tinet.ctilink.cdr.handler;

import com.tinet.ctilink.aws.AwsDynamoDBService;
import com.tinet.ctilink.mq.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author fengwei //
 * @date 16/6/7 17:33
 */
@Component
public class CdrHandler extends Thread {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageQueue messageQueue;

    @Autowired
    private AwsDynamoDBService awsDynamoDBService;

    private ExecutorService pool = Executors.newFixedThreadPool(50);

    private boolean stop = false;

    @Override
    public void run() {
        try {
            while (!stop) {
                try {
                    List<Map> data = messageQueue.receiveMessage(Map.class);
                    if (data != null) {
                        CdrTask cdrTask = new CdrTask(awsDynamoDBService);
                        cdrTask.setData(data);
                        pool.execute(cdrTask);
                    }
                } catch (Exception e) {
                    logger.error("handlerCdr error, ", e);
                }
            }
        } catch (Exception e) {
            logger.error("CdrHandler error", e);
        }
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

}
