package com.aspireapp.loan.interceptor;

import com.aspireapp.loan.constants.ResponseCode;
import com.aspireapp.loan.dao.UserDao;
import com.aspireapp.loan.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.Normalizer;
import java.util.Objects;

@Component
@Slf4j
public class ValidateUserTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private UserDao userDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) throws Exception {
        log.info("Pre Handle interceptor of validateUserTokenInterceptor for request {}", request);
        String token = request.getHeader("token");
        String deviceId = request.getHeader("deviceid");
        String installId = request.getHeader("installid");
        String simId = request.getHeader("simid");

        try {
            if (StringUtils.isEmpty(token)) {
                log.warn("Token value is empty for request {}", request);
                sendFailureResponse(response, ResponseCode.UNAUTHORIZED);
                return false;
            }

            String normalized = Normalizer.normalize(token, Normalizer.Form.NFD);
            token = normalized.replaceAll("[^A-Za-z0-9]", "");


            User user = userDao.findByToken(token);

            if (user != null) {
                String ip = request.getHeader("True-Client-IP");
                if (StringUtils.isEmpty(ip)) {
                    ip = request.getHeader("X-FORWARDED-FOR");
                    if (StringUtils.isEmpty(ip)) {
                        ip = request.getRemoteAddr();
                    }
                }
                log.info("User found id:{} and ip:{}", user.getId(), ip);

                request.setAttribute("user", user);
                request.setAttribute("clientIp", ip);
                return true;
            }
        } catch (Throwable th) {
            log.error("Exception occurred in pre handle interceptor ValidateUserTokenInterceptor: {}", th);
        }

        sendFailureResponse(response, ResponseCode.UNAUTHORIZED);
        return false;
    }

    private void sendFailureResponse(HttpServletResponse response, String responseCode) {
        response.setStatus(Integer.parseInt(responseCode));
    }
}
