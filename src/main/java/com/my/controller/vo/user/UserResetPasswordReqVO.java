package com.my.controller.vo.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserResetPasswordReqVO implements Serializable {
    private Long id;
    private String oldPassword;
    private String newPassword;
}
