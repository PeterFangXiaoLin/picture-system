package com.my.convert;

import com.my.controller.vo.user.UserRespVO;
import com.my.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConvert {
    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    UserRespVO userDOToUserRespVO(User user);
}
