package org.example;

import jdk.jfr.Event;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import com.amazonaws.services.lambda.runtime.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HandlerTest {
    private static final Logger logger = LoggerFactory.getLogger(HandlerTest.class);

    @Test
    void testHandler() {
        logger.info("Invoke TEST - Handler");
        var event = List.of(20, 5);
        Context context = new TestContext();
        Handler handler = new Handler();
        EventBody eventBody = new EventBody();
        eventBody.setSubId("1");
        eventBody.setIndexPos("2");
        var s = handler.handleRequest(eventBody, context);
        System.out.println(s);
    }
}
