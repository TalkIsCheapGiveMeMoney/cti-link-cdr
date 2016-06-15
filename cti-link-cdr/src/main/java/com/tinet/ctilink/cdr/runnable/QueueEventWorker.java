package com.tinet.ctilink.cdr.runnable;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.tinet.ctilink.aws.AwsDynamoDBService;
import com.tinet.ctilink.cdr.event.publisher.EventPublisher;
import com.tinet.ctilink.cdr.inc.CdrConst;
import com.tinet.ctilink.cdr.inc.CdrMacro;
import com.tinet.ctilink.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author fengwei //
 * @date 16/6/12 18:22
 */
public class QueueEventWorker extends Worker {

    public QueueEventWorker(AwsDynamoDBService awsDynamoDBService, JSONObject data) {
        super(awsDynamoDBService, data);
    }

    public void executeData(JSONObject event) {
        try {
            Integer enterpriseId = event.getInt(CdrConst.ENTERPRISE_ID);
            String id = event.getString(CdrConst.ID);

            //构造item
            Item item = new Item();
            //设置pk
            item.withPrimaryKey(CdrConst.CDR_ENTERPRISE_ID, enterpriseId, CdrConst.ID, id);

            for (Map.Entry<String, Object> entry : event.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value == null || StringUtils.isEmpty(value.toString())) {
                    continue;
                }
                switch (key) {
                    case CdrConst.ENTERPRISE_ID:
                        item.withInt(key, enterpriseId);
                        break;
                    case CdrConst.EVENT_TIME:
                        item.withLong(key, Long.parseLong(value.toString()));
                        break;
                    default:
                        if (value instanceof List) {
                            item.withList(key, (List) value);
                        } else {
                            item.withString(key, value.toString());
                        }
                }
            }

            awsDynamoDBService.putItem(CdrMacro.QUEUE_EVENT_TABLE_NAME, item, "attribute_not_exists(id)", null, null);
            //publish queue event
            EventPublisher.publishQueueEvent(event);
        } catch (ConditionalCheckFailedException e) {
            //主键重复会报错
            if (logger.isDebugEnabled()) {
                logger.debug("ConditionalCheckFailedException", e);
            }
        } catch (Exception e) {
            logger.error("executeData error, ", e);
        }
    }

}
