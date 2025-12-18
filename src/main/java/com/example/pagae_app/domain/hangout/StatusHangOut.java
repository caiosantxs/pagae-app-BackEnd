package com.example.pagae_app.domain.hangout;

public enum StatusHangOut {
    ATIVO("ATIVO"),
    FINALIZADO("FINALIZADO");

    private String value;

    StatusHangOut(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
