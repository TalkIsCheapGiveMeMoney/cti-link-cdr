package com.tinet.ctilink.cdr.inc;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.SystemSetting;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.inc.SystemSettingConst;
import com.tinet.ctilink.json.JSONObject;
import com.tinet.ctilink.util.ContextUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fengwei //
 * @date 16/6/12 14:10
 */
public class CdrMacro {

    public static String CDR_IB_TABLE_NAME = "CdrIb";

    public static String CDR_IB_DETAIL_TABLE_NAME = "CdrIbDetail";

    public static String CDR_OB_AGENT_TABLE_NAME = "CdrObAgent";

    public static String CDR_OB_AGENT_DETAIL_TABLE_NAME = "CdrObAgentDetail";

    public static String CDR_OB_CUSTOMER_TABLE_NAME = "CdrObCustomer";

    public static String CDR_OB_CUSTOMER_DETAIL_TABLE_NAME = "CdrObCustomerDetail";

    public static String ANCHOR_EVENT_TABLE_NAME = "AnchorEvent";

    public static String QUEUE_EVENT_TABLE_NAME = "QueueEvent";

    public static String INVESTIGATION_RECORD_TABLE_NAME = "InvestigationRecord";


    //cdr push
    public static Boolean CDR_PUBLISH = false;

    public static String CDR_PUBLISH_CHANNEL;

    public static Boolean ANCHOR_EVENT_PUBLISH = false;

    public static String ANCHOR_EVENT_PUBLISH_CHANNEL;

    public static Boolean QUEUE_EVENT_PUBLISH = false;

    public static String QUEUE_EVENT_PUBLISH_CHANNEL;

    public static Boolean INVESTIGATION_RECORD_PUBLISH = false;

    public static String INVESTIGATION_RECORD_CHANNEL;

    public static String PUBLISH_REDIS_HOST;

    public static int PUBLISH_REDIS_PORT;

    //加载表名, 支持表名可配置
    public static void loadMacro() {
        RedisService redisService = ContextUtil.getBean(RedisService.class);
        List<SystemSetting> systemSettingList = redisService.getList(Const.REDIS_DB_CONF_INDEX, CacheKey.SYSTEM_SETTING
                , SystemSetting.class);

        for (SystemSetting systemSetting : systemSettingList) {
            switch (systemSetting.getName()) {
                case SystemSettingConst.SYSTEM_SETTING_NAME_CDR_IB_TABLE_NAME:
                    CDR_IB_TABLE_NAME = systemSetting.getValue();
                    break;
                case SystemSettingConst.SYSTEM_SETTING_NAME_CDR_IB_DETAIL_TABLE_NAME:
                    CDR_IB_DETAIL_TABLE_NAME = systemSetting.getValue();
                    break;
                case SystemSettingConst.SYSTEM_SETTING_NAME_CDR_OB_AGENT_TABLE_NAME:
                    CDR_OB_AGENT_TABLE_NAME = systemSetting.getValue();
                    break;
                case SystemSettingConst.SYSTEM_SETTING_NAME_CDR_OB_AGENT_DETAIL_TABLE_NAME:
                    CDR_OB_AGENT_DETAIL_TABLE_NAME = systemSetting.getValue();
                    break;
                case SystemSettingConst.SYSTEM_SETTING_NAME_CDR_OB_CUSTOMER_TABLE_NAME:
                    CDR_OB_CUSTOMER_TABLE_NAME = systemSetting.getValue();
                    break;
                case SystemSettingConst.SYSTEM_SETTING_NAME_CDR_OB_CUSTOMER_DETAIL_TABLE_NAME:
                    CDR_OB_CUSTOMER_DETAIL_TABLE_NAME = systemSetting.getValue();
                    break;
                case SystemSettingConst.SYSTEM_SETTING_NAME_ANCHOR_EVENT_TABLE_NAME:
                    ANCHOR_EVENT_TABLE_NAME = systemSetting.getValue();
                    break;
                case SystemSettingConst.SYSTEM_SETTING_NAME_QUEUE_EVENT_TABLE_NAME:
                    QUEUE_EVENT_TABLE_NAME = systemSetting.getValue();
                    break;
                case SystemSettingConst.SYSTEM_SETTING_NAME_INVESTIGATION_RECORD_TABLE_NAME:
                    INVESTIGATION_RECORD_TABLE_NAME = systemSetting.getValue();
                    break;
                case SystemSettingConst.SYSTEM_SETTING_NAME_CDR_PUBLISH:
                    CDR_PUBLISH = Integer.parseInt(systemSetting.getValue()) == 1;
                    CDR_PUBLISH_CHANNEL = systemSetting.getProperty();
                    break;
                case SystemSettingConst.SYSTEM_SETTING_NAME_QUEUE_EVENT_PUBLISH:
                    QUEUE_EVENT_PUBLISH = Integer.parseInt(systemSetting.getValue()) == 1;
                    QUEUE_EVENT_PUBLISH_CHANNEL = systemSetting.getProperty();
                    break;
                case SystemSettingConst.SYSTEM_SETTING_NAME_ANCHOR_EVENT_PUBLISH:
                    ANCHOR_EVENT_PUBLISH = Integer.parseInt(systemSetting.getValue()) == 1;
                    ANCHOR_EVENT_PUBLISH_CHANNEL = systemSetting.getProperty();
                    break;
                case SystemSettingConst.SYSTEM_SETTING_NAME_INVESTIGATION_RECORD_PUBLISH:
                    INVESTIGATION_RECORD_PUBLISH = Integer.parseInt(systemSetting.getValue()) == 1;
                    INVESTIGATION_RECORD_CHANNEL = systemSetting.getProperty();
                    break;
                case SystemSettingConst.SYSTEM_SETTING_NAME_PUBLISH_REDIS:
                    if (StringUtils.isNoneEmpty(systemSetting.getValue())) {
                        JSONObject jsonObject = JSONObject.fromObject(systemSetting.getValue());
                        if (jsonObject != null) {
                            PUBLISH_REDIS_HOST = jsonObject.getString("host");
                            PUBLISH_REDIS_PORT = jsonObject.getInt("port");
                        }
                    }
                    break;
            }
        }
    }
}
