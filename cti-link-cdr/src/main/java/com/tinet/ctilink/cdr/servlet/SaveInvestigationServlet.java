package com.tinet.ctilink.cdr.servlet;

import com.tinet.ctilink.cdr.inc.CdrConst;
import com.tinet.ctilink.cdr.util.CdrUtil;
import com.tinet.ctilink.json.JSONObject;
import com.tinet.ctilink.mq.MessageQueue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/6/8 10:33
 */
@Component
public class SaveInvestigationServlet extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(getClass());

    //required param
    private final static String[] REQUIRED_PARAM = {CdrConst.ENTERPRISE_ID, CdrConst.MAIN_UNIQUE_ID
            , CdrConst.START_TIME, CdrConst.END_TIME, CdrConst.CALL_TYPE};

    @Autowired
    private MessageQueue investigationMessageQueue;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject result = new JSONObject();

        if (logger.isDebugEnabled()) {
            logger.debug("receive investigation: " +request.getParameterMap().toString());
        }

        // check required param
        if (!CdrUtil.checkRequiredParam(request.getParameterMap(), REQUIRED_PARAM)) {
            logger.error("SaveInvestigationServlet.checkRequiredParam failed, lack of required param");
            result.put("result", -1);
            result.put("description", "param invalid");
        }

        // handle param
        JSONObject params = CdrUtil.handleParam(request);
        if (params.isEmpty()) {
            result.put("result", -1);
            result.put("description", "param invalid");
        } else {
            //放到sqs失败要返回 result -1
            boolean res = investigationMessageQueue.sendMessage(params);
            if (res) {
                result.put("result", 0);
                result.put("description", "success");
            } else {
                result.put("result", -1);
                result.put("description", "send message error");
                logger.error("SaveInvestigationServlet sendMessage failed!");
            }
        }

        out.append(result.toString());
        out.flush();
        out.close();
    }

}
