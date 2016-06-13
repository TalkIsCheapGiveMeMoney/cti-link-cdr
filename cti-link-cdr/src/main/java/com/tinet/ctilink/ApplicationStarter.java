package com.tinet.ctilink;

import com.tinet.ctilink.aws.AwsDynamoDBService;
import com.tinet.ctilink.cdr.inc.CdrMacro;
import com.tinet.ctilink.cdr.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * 应用程序启动器
 * 
 * @author Jiangsl
 *
 */
@Component
public class ApplicationStarter implements ApplicationListener<ContextRefreshedEvent> {
	private static Logger logger = LoggerFactory.getLogger(ApplicationStarter.class);

	@Autowired
	private CdrService cdrService;

	@Autowired
	private InvestigationService investigationService;

	@Autowired
	private QueueEventService queueEventService;

	@Autowired
	private AnchorEventService anchorEventService;

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		
		// 设置JVM的DNS缓存时间
		// http://docs.amazonaws.cn/AWSSdkDocsJava/latest/DeveloperGuide/java-dg-jvm-ttl.html
		java.security.Security.setProperty("networkaddress.cache.ttl", "60");

		//加载cdr表的名字
		CdrMacro.loadTableName();

		//启动cdr service
		cdrService.setName("cti-link-cdr-service");
		cdrService.start();

		//启动investigation service
		investigationService.setName("cti-link-cdr-investigation-service");
		investigationService.start();

		//启动queueEvent service
		queueEventService.setName("cti-link-cdr-queue-event-service");
		queueEventService.start();

		//启动anchorEvent service
		anchorEventService.setName("cti-link-cdr-anchor-event-service");
		anchorEventService.start();

		logger.info("cti-link-cdr启动成功");
		System.out.println("cti-link-cdr启动成功");
	}
}