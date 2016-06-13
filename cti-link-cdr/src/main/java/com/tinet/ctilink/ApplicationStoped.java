package com.tinet.ctilink;

import com.tinet.ctilink.cdr.service.AnchorEventService;
import com.tinet.ctilink.cdr.service.CdrService;
import com.tinet.ctilink.cdr.service.InvestigationService;
import com.tinet.ctilink.cdr.service.QueueEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

/**
 * @author fengwei //
 * @date 16/6/13 10:14
 */
@Component
public class ApplicationStoped implements ApplicationListener<ContextStoppedEvent> {

    private static Logger logger = LoggerFactory.getLogger(ApplicationStoped.class);

    @Autowired
    private CdrService cdrService;

    @Autowired
    private InvestigationService investigationService;

    @Autowired
    private QueueEventService queueEventService;

    @Autowired
    private AnchorEventService anchorEventService;

    @Override
    public void onApplicationEvent(ContextStoppedEvent event) {
        //shutdown

        cdrService.shutdown();

        investigationService.shutdown();

        queueEventService.shutdown();

        anchorEventService.shutdown();

        logger.info("cti-link-cdr关闭");
        System.out.println("cti-link-cdr关闭");
    }
}
