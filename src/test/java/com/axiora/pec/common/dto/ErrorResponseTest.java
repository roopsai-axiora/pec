package com.axiora.pec.common;

import com.axiora.pec.common.dto.ErrorResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void shouldCreateErrorResponse() {
        ErrorResponse response = ErrorResponse.of(
                404,
                "Not Found",
                "Resource not found",
                "/api/goals/999"
        );

        assertNotNull(response);
        assertEquals(404, response.status());
        assertEquals("Not Found", response.error());
        assertEquals("Resource not found",
                response.message());
        assertEquals("/api/goals/999", response.path());
        assertNotNull(response.timestamp());
    }

    @Test
    void shouldCreate400ErrorResponse() {
        ErrorResponse response = ErrorResponse.of(
                400,
                "Bad Request",
                "Validation failed",
                "/api/auth/register"
        );

        assertEquals(400, response.status());
        assertEquals("Bad Request", response.error());
    }

    @Test
    void shouldCreate500ErrorResponse() {
        ErrorResponse response = ErrorResponse.of(
                500,
                "Internal Server Error",
                "Unexpected error",
                "/api/goals"
        );

        assertEquals(500, response.status());
        assertEquals("Internal Server Error",
                response.error());
    }
}
