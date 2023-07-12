package com.finalProject.stockbeginner.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DuplicatedEmailException
    extends RuntimeException {

    public DuplicatedEmailException(String message) {
        super(message);
    }
}
