package com.my.picturesystembackend.controller;

import com.my.picturesystembackend.common.BaseResponse;
import com.my.picturesystembackend.common.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Api(value = "health")
public class MainController {

    /**
     * 健康检查
     */
    @GetMapping("/health")
    @ApiOperation(value = "健康检查", notes = "健康检查")
    public BaseResponse<String> health() {
        return ResultUtils.success("ok");
    }
}
