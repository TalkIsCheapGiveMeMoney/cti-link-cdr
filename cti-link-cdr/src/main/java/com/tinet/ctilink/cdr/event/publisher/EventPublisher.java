package com.tinet.ctilink.cdr.event.publisher;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.tinet.ctilink.cdr.inc.CdrMacro;
import com.tinet.ctilink.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author fengwei //
 * @date 16/6/12 14:34
 */
public class EventPublisher implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(EventPublisher.class);

    private static StringRedisTemplate redisTemplate;

    public static String host;

    public static int port;

    public static void publishCdrEvent(JSONObject event) {
        try {
            if (CdrMacro.CDR_PUBLISH && redisTemplate != null) {
                redisTemplate.convertAndSend(CdrMacro.CDR_PUBLISH_CHANNEL, JSONObject.getJSONString(event));
            }
        } catch (Exception e) {
            logger.error("EventPublisher publishCdrEvent error,", e);
        }
    }

    public static void publishInvestigationEvent(JSONObject event) {
        try {
            if (CdrMacro.INVESTIGATION_RECORD_PUBLISH && redisTemplate != null) {
                redisTemplate.convertAndSend(CdrMacro.INVESTIGATION_RECORD_CHANNEL, JSONObject.getJSONString(event));
            }
        } catch (Exception e) {
            logger.error("EventPublisher publishInvestigationEvent error,", e);
        }
    }

    public static void publishAnchorEvent(JSONObject event) {
        try {
            if (CdrMacro.ANCHOR_EVENT_PUBLISH && redisTemplate != null) {
                redisTemplate.convertAndSend(CdrMacro.ANCHOR_EVENT_PUBLISH_CHANNEL, JSONObject.getJSONString(event));
            }
        } catch (Exception e) {
            logger.error("EventPublisher publishAnchorEvent error,", e);
        }
    }

    public static void publishQueueEvent(JSONObject event) {
        try {
            if (CdrMacro.QUEUE_EVENT_PUBLISH && redisTemplate != null) {
                redisTemplate.convertAndSend(CdrMacro.QUEUE_EVENT_PUBLISH_CHANNEL, JSONObject.getJSONString(event));
            }
        } catch (Exception e) {
            logger.error("EventPublisher publishQueueEvent error,", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRedisTemplate();
    }

    public synchronized static void setRedisTemplate() {
        if (StringUtils.isEmpty(CdrMacro.PUBLISH_REDIS_HOST)
                || CdrMacro.PUBLISH_REDIS_PORT == 0) {
            return;
        }
        if (CdrMacro.PUBLISH_REDIS_HOST.equals(host) && port == CdrMacro.PUBLISH_REDIS_PORT) {
            return;
        }
        host = CdrMacro.PUBLISH_REDIS_HOST;
        port = CdrMacro.PUBLISH_REDIS_PORT;
        try {
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxIdle(20);
            jedisPoolConfig.setMaxTotal(20);
            jedisPoolConfig.setMinIdle(5);

            JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig);
            jedisConnectionFactory.setHostName(CdrMacro.PUBLISH_REDIS_HOST);
            jedisConnectionFactory.setDatabase(0);
            jedisConnectionFactory.setPort(CdrMacro.PUBLISH_REDIS_PORT);
            jedisConnectionFactory.setUsePool(true);
            jedisConnectionFactory.afterPropertiesSet();
            redisTemplate = new StringRedisTemplate(jedisConnectionFactory);
        } catch (Exception e) {
            logger.error("init redisTemplate error!");
        }
    }
}
