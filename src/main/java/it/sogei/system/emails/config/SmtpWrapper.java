package it.sogei.system.emails.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.sogei.system.emails.config.SmtpConfig;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SmtpWrapper(
        @JsonProperty("smtp") SmtpConfig smtp) {
    public SmtpWrapper {
        if (smtp == null) {
            throw new IllegalArgumentException("smtp cannot be null");
        }
    }
}
