package com.tinet.ctilink.cdr.handler;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.tinet.ctilink.aws.AwsDynamoDBService;
import com.tinet.ctilink.inc.Const;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/6/7 17:34
 */
public class CdrTask implements Runnable {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private List<Map> data;

    private AwsDynamoDBService awsDynamoDBService;

    public CdrTask(AwsDynamoDBService awsDynamoDBService) {
        this.awsDynamoDBService = awsDynamoDBService;
    }

    @Override
    public void run() {
        //入库
        for (Map map : data) {
            executeCdr((Map<String, String[]>) map);
        }
    }

    private void executeCdr(Map<String, String[]> cdrMap) {
        try {
            Integer enterpriseId = Integer.parseInt(cdrMap.get("enterpriseId")[0]);
            String uniqueId = cdrMap.get("cdr_unique_id")[0];
            String mainUniqueId = cdrMap.get("cdr_main_unique_id")[0];
            Integer callType = Integer.parseInt(cdrMap.get("cdr_call_type")[0]);
            String tableName;
            switch(callType){
                case Const.CDR_CALL_TYPE_IB:
                    tableName = "CdrIb";
                    break;
                case Const.CDR_CALL_TYPE_IB_CALL_AGNET:
                case Const.CDR_CALL_TYPE_IB_TRANSFER:
                case Const.CDR_CALL_TYPE_IB_CONSULT:
                case Const.CDR_CALL_TYPE_IB_THREEWAY:
                case Const.CDR_CALL_TYPE_IB_SPY:
                case Const.CDR_CALL_TYPE_IB_WHISPER:
                case Const.CDR_CALL_TYPE_IB_BARGE:
                case Const.CDR_CALL_TYPE_IB_PICKUP:
                case Const.CDR_CALL_TYPE_IB_CALL_TEL:
                    tableName = "CdrIbDetail";
                    break;
                case Const.CDR_CALL_TYPE_OB_PREVIEW:
                case Const.CDR_CALL_TYPE_OB_DIRECT:
                case Const.CDR_CALL_TYPE_OB_INTERNAL:
                    tableName = "CdrObAgent";
                    break;
                case Const.CDR_CALL_TYPE_OB_CALL_CUSTOMER:
                case Const.CDR_CALL_TYPE_OB_CALL_AGENT:
                case Const.CDR_CALL_TYPE_OB_TRANSFER:
                case Const.CDR_CALL_TYPE_OB_CONSULT:
                case Const.CDR_CALL_TYPE_OB_THREEWAY:
                case Const.CDR_CALL_TYPE_OB_SPY:
                case Const.CDR_CALL_TYPE_OB_WHISPER:
                case Const.CDR_CALL_TYPE_OB_BARGE:
                    tableName = "CdrObAgentDetail";
                    break;
                case Const.CDR_CALL_TYPE_OB_PREDICTIVE:
                case Const.CDR_CALL_TYPE_OB_WEBCALL:
                    tableName = "CdrObCustomer";
                    break;

                default:
                    logger.error("bad callType:" + callType);
                    return;
            }

            //构造item
            Item item = new Item();
            //设置pk
            item.withPrimaryKey("enterpriseId", enterpriseId, "uniqueId", uniqueId);
            for (Map.Entry<String, String[]> entry : cdrMap.entrySet()) {
                String key = entry.getKey();
                String[] value = entry.getValue();
                if (value == null || value.length == 0) {
                    logger.error(key + "的值为空, 丢弃");
                    continue;
                }
                switch (key) {
                    case "enterpriseId":
                    case "uniqueId":
                        break;
                    case "startTime":
                    case "endTime":
                        item.withLong(key, Long.parseLong(value[0]));
                        break;
                    default:
                        if (value.length == 1) {
                            if (StringUtils.isNotEmpty(value[0])) {
                                item.withString(entry.getKey(), value[0]);
                            }
                        } else {
                            Set<String> set = new HashSet<>();
                            for (String v : value) {
                                if (StringUtils.isNotEmpty(v)) {
                                    set.add(v);
                                }
                            }
                            if (set.size() == 1) {
                                item.withString(entry.getKey(), set.toArray()[0].toString());
                            } else if (set.size() > 1) {
                                item.withStringSet(entry.getKey(), set);
                            }
                        }
                }
            }
            awsDynamoDBService.putItem(tableName, item);
        } catch (Exception e) {
            logger.error("error happen", e);
        }
    }

    public void setData(List<Map> data) {
        this.data = data;
    }
}
