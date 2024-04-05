package org.bz.app.mspeople.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;

public class JsonMapper {

    private JsonMapper() {
    }

    public static ObjectMapper getMapper() {
        return new ObjectMapper();
    }

    public static String writeValueAsString(@NotNull Object object) {
        try {
            return JsonMapper.getMapper().writeValueAsString(object);
        } catch (JsonProcessingException x) {
            throw new IllegalArgumentException(x.getMessage(), x);
        }
    }
}
