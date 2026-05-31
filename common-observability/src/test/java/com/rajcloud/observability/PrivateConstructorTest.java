package com.rajcloud.observability;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

class PrivateConstructorTest {
    @Test
    void correlationIdPrivateConstructorIsCovered() throws Exception {
        Constructor<CorrelationId> constructor = CorrelationId.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThat(constructor.newInstance()).isNotNull();
    }
}
