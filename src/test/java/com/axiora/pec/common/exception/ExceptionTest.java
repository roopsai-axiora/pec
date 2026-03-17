package com.axiora.pec.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExceptionTest {

    @Test
    void shouldCreateResourceNotFoundException() {
        ResourceNotFoundException ex =
                new ResourceNotFoundException("Goal", 1L);

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage()
                .contains("Goal"));
        assertTrue(ex.getMessage()
                .contains("1"));
    }

    @Test
    void shouldCreateResourceNotFoundExceptionWithMessage() {
        ResourceNotFoundException ex =
                new ResourceNotFoundException("Goal not found");

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("Goal not found", ex.getMessage());
    }

    @Test
    void shouldCreateEmailAlreadyExistsException() {
        EmailAlreadyExistsException ex =
                new EmailAlreadyExistsException("roop@axiora.com");

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage()
                .contains("roop@axiora.com"));
    }

    @Test
    void shouldCreateWeightageExceededException() {
        WeightageExceededException ex =
                new WeightageExceededException();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY,
                ex.getStatus());
        assertTrue(ex.getMessage()
                .contains("weightage"));
    }
}