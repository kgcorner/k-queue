package com.lluvia.security;

import com.kgcorner.lluvia.model.Application;
import com.kgcorner.util.Strings;
import com.lluvia.exception.InvalidTokenException;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthFilter implements Filter{
    private static final String APPLICATION_PATH = "/applications";
    private static final String SUBSCRIBE_PATH = "/subscribers";
    private static final Logger LOGGER = Logger.getLogger(AuthFilter.class);
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //Do Nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String path = ((HttpServletRequest) request).getServletPath();
        String method = ((HttpServletRequest) request).getMethod();
        LOGGER.info("PATH:"+path);

        if(method.equals("OPTIONS") || path.equals(APPLICATION_PATH) || path.endsWith(SUBSCRIBE_PATH)) {
            chain.doFilter(request, response);
        }
        else {
            String token = ((HttpServletRequest) request).getHeader("Authorization");
            if(Strings.isNullOrEmpty(token)) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("message", "Authorization token is missing");
                    jsonObject.put("code", 400);
                    ((HttpServletResponse) response).setStatus(400);
                    ((HttpServletResponse) response).getOutputStream().write(jsonObject.toString().getBytes());
                    return;
                } catch (JSONException e) {
                    LOGGER.error(e.getMessage(), e);
                }

            }
            try {
                Application application = Authenticator.authenticate(token);
                request.setAttribute("Application", application);
                chain.doFilter(request, response);
            } catch (InvalidTokenException e) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("message", e.getMessage());
                    jsonObject.put("code", 403);
                    ((HttpServletResponse) response).setStatus(403);
                    ((HttpServletResponse) response).getOutputStream().write(jsonObject.toString().getBytes());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

            }
        }
    }

    @Override
    public void destroy() {
        //Do Nothing
    }
}
