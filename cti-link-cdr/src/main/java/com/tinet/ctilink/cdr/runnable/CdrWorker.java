package com.tinet.ctilink.cdr.runnable;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.tinet.ctilink.aws.AwsDynamoDBService;
import com.tinet.ctilink.cdr.inc.CdrConst;
import com.tinet.ctilink.cdr.inc.CdrMacro;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author fengwei //
 * @date 16/6/7 17:34
 */
public class CdrWorker extends Worker {

    public CdrWorker(AwsDynamoDBService awsDynamoDBService, JSONObject data) {
        super(awsDynamoDBService, data);
    }

    public void executeData(JSONObject jsonObject) {
        try {
            Integer enterpriseId = jsonObject.getInt(CdrConst.CDR_ENTERPRISE_ID);
            String uniqueId = jsonObject.getString(CdrConst.CDR_UNIQUE_ID);
            String mainUniqueId = jsonObject.getString(CdrConst.CDR_MAIN_UNIQUE_ID);
            Integer callType = jsonObject.getInt(CdrConst.CDR_CALL_TYPE);
            String tableName;
            switch (callType) {
                case Const.CDR_CALL_TYPE_IB:
                    //从通道
                    if (!uniqueId.equals(mainUniqueId)) {
                        tableName = CdrMacro.CDR_IB_DETAIL_TABLE_NAME;
                    } else {
                        tableName = CdrMacro.CDR_IB_TABLE_NAME;
                    }
                    break;
                case Const.CDR_CALL_TYPE_OB_PREVIEW:
                case Const.CDR_CALL_TYPE_OB_DIRECT:
                case Const.CDR_CALL_TYPE_OB_INTERNAL:
                    //从通道
                    if (!uniqueId.equals(mainUniqueId)) {
                        tableName = CdrMacro.CDR_OB_AGENT_DETAIL_TABLE_NAME;
                    } else {
                        tableName = CdrMacro.CDR_OB_AGENT_TABLE_NAME;
                    }
                    break;
                case Const.CDR_CALL_TYPE_OB_WEBCALL:
                case Const.CDR_CALL_TYPE_OB_PREDICTIVE:
                    //从通道
                    if (!uniqueId.equals(mainUniqueId)) {
                        tableName = CdrMacro.CDR_OB_CUSTOMER_DETAIL_TABLE_NAME;
                    } else {
                        tableName = CdrMacro.CDR_OB_CUSTOMER_TABLE_NAME;
                    }
                    break;
                default:
                    logger.error("bad callType:" + callType);
                    return;
            }

            //构造item
            Item item = new Item();
            //设置pk
            item.withPrimaryKey(CdrConst.CDR_ENTERPRISE_ID, enterpriseId, CdrConst.CDR_UNIQUE_ID, uniqueId);
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                switch (key) {
                    case CdrConst.CDR_ENTERPRISE_ID:
                        item.withInt(key, enterpriseId);
                        break;
                    case CdrConst.CDR_UNIQUE_ID:
                        item.withString(key, uniqueId);
                        break;
                    case CdrConst.CDR_START_TIME:
                    case CdrConst.CDR_END_TIME:
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

            awsDynamoDBService.putItem(tableName, item);

        } catch (Exception e) {
            logger.error("executeData error, ", e);
        }
    }

}
