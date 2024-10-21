package it.dmi.system.emails.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class SmtpConfig {

    @JsonProperty("host")
    private final String host;

    @JsonProperty("port")
    private final int port;

    @JsonProperty("auth")
    private final String auth;

    @JsonProperty("starttls")
    private final boolean startTls;

    @JsonProperty("user")
    private final String user;

    @JsonProperty("password")
    private final String password;


}

