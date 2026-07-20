package com.my.picturesystembackend.manager.auth;

import com.my.picturesystembackend.model.entity.Space;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SaTokenContextHolderTest {

    @AfterEach
    void tearDown() {
        SaTokenContextHolder.clear();
    }

    @Test
    void shouldStoreAndClearContext() {
        Space space = new Space();
        space.setId(1L);
        SaTokenContextHolder.setContext(SpaceUserAuthContext.ofSpace(space));

        assertEquals(1L, SaTokenContextHolder.getContext().getSpace().getId());

        SaTokenContextHolder.clear();
        assertNull(SaTokenContextHolder.getContext());
    }

    @Test
    void shouldNotLeakContextToAnotherThread() {
        SaTokenContextHolder.setContext(SpaceUserAuthContext.ofSpaceId(1L));

        SpaceUserAuthContext otherThreadContext = CompletableFuture
                .supplyAsync(SaTokenContextHolder::getContext)
                .join();

        assertNull(otherThreadContext);
        assertEquals(1L, SaTokenContextHolder.getContext().getSpaceId());
    }
}
