package com.my.picturesystembackend.config;

import com.my.picturesystembackend.manager.auth.SaTokenContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

/**
 * 请求结束时兜底清理鉴权上下文。
 *
 * @author pine
 */
@Order(1)
@Component
public class HttpRequestWrapperFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } finally {
            SaTokenContextHolder.clear();
        }
    }

}
