package ru.bdayloop.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static <T> T readBody(HttpServletRequest req, Class<T> type) throws IOException {
        return mapper.readValue(req.getInputStream(), type);
    }

    public static void writeBody(HttpServletResponse resp, Object value) throws IOException{
        resp.setContentType("application/json");
        mapper.writeValue(resp.getWriter(), value);
    }
}
