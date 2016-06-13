package com.tinet.ctilink.cdr.inc;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.SystemSetting;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.inc.SystemSettingConst;
import com.tinet.ctilink.util.ContextUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fengwei //
 * @date 16/6/12 14:10
 */
public class CdrMacro {

    public static String CDR_IB_TABLE_NAME = "CdrIb";

    public static String CDR_IB_DETAIL_TABLE_NAME = "CdrIbDetail";

    public static String CDR_OB_AGENT_TABLE_NAME = "ObAgent";

    public static String CDR_OB_AGENT_DETAIL_TABLE_NAME = "CdrObAgentDetail";

    public static String CDR_OB_CUSTOMER_TABLE_NAME = "CdrObCustomer";

    public static String CDR_OB_CUSTOMER_DETAIL_TABLE_NAME = "CdrObCustomerDetail";

    public static String ANCHOR_EVENT = "AnchorEvent";

    public static String QUEUE_EVENT = "QueueEvent";

    public static String INVESTIGATION_RECORD = "InvestigationRecord";


    //加载表名, 支持表名可配置
    public static void loadTableName() {
        RedisService redisService = ContextUtil.getBean(RedisService.class);

        String prefix = "";
        String suffix = "";
        SystemSetting systemSetting = redisService.get(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.SYSTEM_SETTING_NAME
                , SystemSettingConst.SYSTEM_SETTING_NAME_DYNAMODB_TABLE_NAME_PREFIX), SystemSetting.class);
        if (systemSetting != null && StringUtils.isNotEmpty(systemSetting.getValue())) {
            prefix = systemSetting.getValue();
        }

        systemSetting = redisService.get(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.SYSTEM_SETTING_NAME
                , SystemSettingConst.SYSTEM_SETTING_NAME_DYNAMODB_TABLE_NAME_SUFFIX), SystemSetting.class);
        if (systemSetting != null && StringUtils.isNotEmpty(systemSetting.getValue())) {
            suffix = systemSetting.getValue();
        }

        CDR_IB_TABLE_NAME = prefix + CDR_IB_TABLE_NAME + suffix;
        CDR_IB_DETAIL_TABLE_NAME = prefix + CDR_IB_DETAIL_TABLE_NAME + suffix;
        CDR_OB_AGENT_TABLE_NAME = prefix + CDR_OB_AGENT_TABLE_NAME + suffix;
        CDR_OB_AGENT_DETAIL_TABLE_NAME = prefix + CDR_OB_AGENT_DETAIL_TABLE_NAME + suffix;
        CDR_OB_CUSTOMER_TABLE_NAME = prefix + CDR_OB_CUSTOMER_TABLE_NAME + suffix;
        CDR_OB_CUSTOMER_DETAIL_TABLE_NAME = prefix + CDR_OB_CUSTOMER_DETAIL_TABLE_NAME + suffix;
        ANCHOR_EVENT = prefix + ANCHOR_EVENT + suffix;
        QUEUE_EVENT = prefix + QUEUE_EVENT + suffix;
        INVESTIGATION_RECORD = prefix + INVESTIGATION_RECORD + suffix;

    }
}
