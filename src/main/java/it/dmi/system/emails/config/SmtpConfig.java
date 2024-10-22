package it.dmi.system.emails.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SmtpConfig(@JsonProperty("host") String host, @JsonProperty("port") int port,
                         @JsonProperty("auth") String auth, @JsonProperty("starttls") boolean startTls,
                         @JsonProperty("user") String user, @JsonProperty("password") String password) {

}

