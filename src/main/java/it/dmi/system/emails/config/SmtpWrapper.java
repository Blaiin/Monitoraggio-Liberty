package it.dmi.system.emails.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SmtpWrapper(@JsonProperty("smtp") SmtpConfig smtp) {

    public SmtpWrapper (SmtpConfig smtp) {
        if (smtp == null) {
            throw new IllegalArgumentException("smtp cannot be null");
        }
        this.smtp = smtp;
    }

}

