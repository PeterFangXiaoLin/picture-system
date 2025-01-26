package com.my.controller.vo.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterReqVO implements Serializable {
    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
