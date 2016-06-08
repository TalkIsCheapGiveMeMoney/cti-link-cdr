package com.tinet.ctilink.cdr.servlet;

import com.tinet.ctilink.json.JSONObject;
import com.tinet.ctilink.mq.MessageQueue;
import com.tinet.ctilink.util.ContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author fengwei //
 * @date 16/5/31 15:22
 */
@WebServlet("/interface/SaveCdr")
public class SaveCdrServlet extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private MessageQueue messageQueue;

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
        JSONObject jsonObject = new JSONObject();

        boolean res = messageQueue.sendMessage(request.getParameterMap());
        if (res) {
            jsonObject.put("result", 0);
        } else {
            jsonObject.put("result", -1);
            logger.error("SaveCdr sendMessage failed!");
        }
        out.append(jsonObject.toString());
        out.flush();
        out.close();
    }

    @Override
    public void init() throws ServletException {
        messageQueue = ContextUtil.getBean(MessageQueue.class);
    }
}
