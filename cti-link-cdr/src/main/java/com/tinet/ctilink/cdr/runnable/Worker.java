package com.tinet.ctilink.cdr.runnable;

import com.tinet.ctilink.aws.AwsDynamoDBService;
import com.tinet.ctilink.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fengwei //
 * @date 16/6/12 19:41
 */
public abstract class Worker implements Runnable {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected AwsDynamoDBService awsDynamoDBService;

    protected JSONObject data;

    public Worker(AwsDynamoDBService awsDynamoDBService, JSONObject data) {
        this.awsDynamoDBService = awsDynamoDBService;
        this.data = data;
    }

    @Override
    public void run() {
        //入库
        try {
            executeData(data);
        } catch (Exception e) {
            logger.error("executeData error, ", e);
        }
    }

    protected abstract void executeData(JSONObject jsonObject);

}
