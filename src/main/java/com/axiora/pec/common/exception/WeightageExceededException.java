package com.axiora.pec.common.exception;

import org.springframework.http.HttpStatus;

public class WeightageExceededException extends PecException {

    public WeightageExceededException() {
        super("Total weightage exceeds 100 for this period",
                HttpStatus.UNPROCESSABLE_CONTENT);
    }
}