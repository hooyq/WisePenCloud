package com.oriole.wisepen.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;

class FeignConfigurationTest {

    @Test
    void feignObjectMapperCannotDecodeMillisTimestampToLocalDateTime() throws Exception {
        ObjectMapper objectMapper = feignObjectMapper();

        String json = """
                {
                  "expiration": 1783502298794
                }
                """;

        assertThatCode(() -> objectMapper.readValue(json, TokenExpirationPayload.class))
                .doesNotThrowAnyException();
    }

    private static ObjectMapper feignObjectMapper() throws Exception {
        Method method = FeignConfiguration.class.getDeclaredMethod("feignObjectMapper");
        method.setAccessible(true);
        return (ObjectMapper) method.invoke(new FeignConfiguration());
    }

    private static class TokenExpirationPayload {
        @SuppressWarnings("unused")
        public LocalDateTime expiration;
    }
}
