package com.tinet.ctilink.cdr.entity;

/**
 * @author fengwei //
 * @date 16/6/6 15:59
 */
public class CdrIb {
    //required
    private Integer enterpriseId;

    private String uniqueId;

    private Integer callType;

    private Integer status;

    private String endReason;

    private Integer sipCause;

    private Long startTime;

    private Long endTime;


    //optional
    private String mainUniqueId;

    private String customerNumber;

    private String customerNumberType;

    private String customerAreaCode;

    private String clid;

    private String queue;

    private Long joinQueueTime;

    private String cno;

    private String exten;

    private String agentNumber;

    private String transfer;

    private String consult;

    private String threeway;

    private String amd;

    //?????
    private String requestUniqueId;

    private String recordFile;


}
