package it.dmi.rest;

import it.dmi.rest.endpoint.QueryAPIResource;
import it.dmi.rest.endpoint.RamUsageAPIResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
@Slf4j
public class RestApplication extends Application {

    private static int maxMessages = 1;

    public RestApplication() {
        if (maxMessages == 1) {
            log.debug("RestApplication initialized.");
            maxMessages++;
        }
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(QueryAPIResource.class);
        classes.add(RamUsageAPIResource.class);
        return classes;
    }

}