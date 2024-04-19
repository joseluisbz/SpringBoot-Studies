package org.bz.app.mspeople.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;

public class JsonMapperUtil {

    private JsonMapperUtil() {
    }

    public static ObjectMapper getMapper() {
        return new ObjectMapper();
    }

    public static String writeValueAsString(@NotNull Object object) {
        try {
            return JsonMapperUtil.getMapper().writeValueAsString(object);
        } catch (JsonProcessingException x) {
            throw new IllegalArgumentException(x.getMessage(), x);
        }
    }
}
