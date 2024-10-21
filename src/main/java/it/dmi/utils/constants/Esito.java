package it.dmi.utils.constants;

import lombok.Getter;

@Getter
public enum Esito {
    POSITIVE('0'),
    NEGATIVE('1');

    private final char value;

    Esito(char value) {
        this.value = value;
    }
}


