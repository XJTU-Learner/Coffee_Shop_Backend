package org.xjtu_learner.coffee_shop.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class HttpContext {

    public static String getIp() {
        HttpServletRequest request = getRequest();
        return request == null ? "127.0.0.1" : request.getRemoteHost();
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : requestAttributes.getRequest();
    }

    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : requestAttributes.getResponse();
    }

    public static Map<String, String> getRequestParameters() {
        HashMap<String, String> values = new HashMap<>();
        HttpServletRequest request = getRequest();
        if (request != null) {
            Enumeration enums = request.getParameterNames();

            while (enums.hasMoreElements()) {
                String paramName = (String) enums.nextElement();
                String paramValue = request.getParameter(paramName);
                values.put(paramName, paramValue);
            }
        }
        return values;
    }

    public static String getRequestHeader(String headerName) {
        HttpServletRequest request = getRequest();
        return request == null ? null : request.getHeader(headerName);
    }
}
