package it.dmi.system.emails.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmtpWrapper {

    @JsonProperty("smtp")
    private final SmtpConfig smtp;

    public SmtpWrapper(SmtpConfig smtp) {
        if (smtp == null) {
            throw new IllegalArgumentException("smtp cannot be null");
        }
        this.smtp = smtp;
    }

}

