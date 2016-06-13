package com.tinet.ctilink.cdr.runnable;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.tinet.ctilink.aws.AwsDynamoDBService;
import com.tinet.ctilink.cdr.inc.CdrConst;
import com.tinet.ctilink.cdr.inc.CdrMacro;
import com.tinet.ctilink.json.JSONObject;

import java.util.Map;

/**
 * @author fengwei //
 * @date 16/6/12 18:22
 */
public class QueueEventWorker extends Worker {

    public QueueEventWorker(AwsDynamoDBService awsDynamoDBService, JSONObject data) {
        super(awsDynamoDBService, data);
    }

    public void executeData(JSONObject jsonObject) {
        try {
            Integer enterpriseId = jsonObject.getInt(CdrConst.ENTERPRISE_ID);
            String id = jsonObject.getString(CdrConst.ID);

            String tableName = CdrMacro.QUEUE_EVENT;
            //构造item
            Item item = new Item();
            //设置pk
            item.withPrimaryKey(CdrConst.CDR_ENTERPRISE_ID, enterpriseId, CdrConst.ID, id);

            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                switch (key) {
                    case CdrConst.ENTERPRISE_ID:
                        item.withInt(key, enterpriseId);
                        break;
                    case CdrConst.EVENT_TIME:
                        item.withLong(key, Long.parseLong(value.toString()));
                        break;
                    default:
                        item.withString(key, value.toString());
                }
            }
            awsDynamoDBService.putItem(tableName, item);

        } catch (Exception e) {
            logger.error("executeData error, ", e);
        }
    }

}
