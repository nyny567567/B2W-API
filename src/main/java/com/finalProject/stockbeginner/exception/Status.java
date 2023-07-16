package com.finalProject.stockbeginner.exception;

public enum Status {
    NOTFOUND_EMAIL("이메일 정보 제공 동의를 하지 않으시면 가입이 어렵습니다."),
    Already_JOINED("이미 가입하신 이메일입니다.");


    private String message;

    Status(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

