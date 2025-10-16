package com.my.picturesystembackend;

import com.my.picturesystembackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
class PictureSystemBackendApplicationTests {

    private final UserService userService;

    @Test
    void contextLoads() {
        userService.getById(null);
    }

}
