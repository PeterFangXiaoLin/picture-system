package com.my.controller;

import com.my.common.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.my.common.ResultUtils.success;

@RestController
public class MainController {

    @GetMapping("/health")
    public BaseResponse<String> health() {
        return success("ok");
    }

    @GetMapping("/test")
    public void testRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("https://www.baidu.com");
    }
}
