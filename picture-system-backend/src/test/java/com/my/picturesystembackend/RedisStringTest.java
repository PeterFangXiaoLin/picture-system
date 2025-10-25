package com.my.picturesystembackend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedisStringTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void testRedisStringOperation() {
        // 获取操作对象
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();

        // key and value
        String key = "testKey";
        String value = "testValue";

        // 测试新增值
        valueOps.set(key, value);
        String storedValue = valueOps.get(key);
        assertEquals(value, storedValue, "存储的值与预期值不一致");

        // 测试修改操作
        String updateValue = "updateValue";
        valueOps.set(key, updateValue);
        storedValue = valueOps.get(key);
        assertEquals(updateValue, storedValue, "修改的值与预期值不一致");

        // 测试查询操作
        storedValue = valueOps.get(key);
        assertNotNull(storedValue, "查询的值为空");
        assertEquals(updateValue, storedValue, "查询的值与预期值不一致");

        // 测试删除操作
        stringRedisTemplate.delete(key);
        storedValue = valueOps.get(key);
        assertNull(storedValue, "删除失败");
    }
}
