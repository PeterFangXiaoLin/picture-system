package com.my.picturesystembackend.controller;

import com.my.picturesystembackend.common.BaseResponse;
import com.my.picturesystembackend.common.ResultUtils;
import com.my.picturesystembackend.model.dto.thumb.DoThumbRequest;
import com.my.picturesystembackend.service.ThumbService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/thumb")
@RequiredArgsConstructor
public class ThumbController {

    private final ThumbService thumbService;

    /**
     * 点赞
     *
     * @param doThumbRequest 点赞请求
     * @param request 登录用户
     * @return 是否成功
     */
    @PostMapping("/do")
    @ApiOperation("点赞")
    public BaseResponse<Boolean> doThumb(@RequestBody DoThumbRequest doThumbRequest, HttpServletRequest request) {
        return ResultUtils.success(thumbService.doThumb(doThumbRequest, request));
    }

    /**
     * 取消点赞
     *
     * @param doThumbRequest 取消点赞请求
     * @param request 登录用户
     * @return 是否成功
     */
    @PostMapping("/undo")
    @ApiOperation("取消点赞")
    public BaseResponse<Boolean> undoThumb(@RequestBody DoThumbRequest doThumbRequest, HttpServletRequest request) {
        return ResultUtils.success(thumbService.undoThumb(doThumbRequest, request));
    }
}
