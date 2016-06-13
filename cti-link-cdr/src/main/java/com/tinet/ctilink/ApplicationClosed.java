package com.tinet.ctilink;


import com.tinet.ctilink.cdr.service.AnchorEventService;
import com.tinet.ctilink.cdr.service.CdrService;
import com.tinet.ctilink.cdr.service.InvestigationService;
import com.tinet.ctilink.cdr.service.QueueEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * @author fengwei //
 * @date 16/6/13 12:56
 */
@Component
public class ApplicationClosed implements ApplicationListener<ContextClosedEvent> {
    private static Logger logger = LoggerFactory.getLogger(ApplicationClosed.class);

    @Autowired
    private CdrService cdrService;

    @Autowired
    private InvestigationService investigationService;

    @Autowired
    private QueueEventService queueEventService;

    @Autowired
    private AnchorEventService anchorEventService;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        logger.info("cti-link-cdr context closed");
        cdrService.shutdown();

        investigationService.shutdown();

        queueEventService.shutdown();

        anchorEventService.shutdown();
    }
}
