package com.finalProject.stockbeginner.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EmailSigninFailedException
    extends RuntimeException {

    public EmailSigninFailedException(String message) {
        super(message);
    }
}
