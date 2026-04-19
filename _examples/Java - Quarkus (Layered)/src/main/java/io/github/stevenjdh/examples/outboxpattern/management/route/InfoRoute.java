/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025-2026 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */
package io.github.stevenjdh.examples.outboxpattern.management.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.vertx.http.ManagementInterface;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Custom management endpoint for info, which is more expressive than the
 * quarkus-info extension. This make it work more like Actuator in Spring Boot.
 */
@Singleton
public class InfoRoute {

    private final String basePath;
    private final ObjectMapper mapper;

    public InfoRoute(@ConfigProperty(name = "quarkus.management.root-path", defaultValue = "/q") String basePath,
            ObjectMapper mapper) {

        this.basePath = basePath;
        this.mapper = mapper;
    }

    protected void register(@Observes ManagementInterface mi) {
        mi.router()
                .get(String.format("%s/info", basePath))
                .handler(rc -> rc.response()
                    .putHeader("Content-Type", "application/json;charset=UTF-8")
                        .end(loadGitInfo()));
    }

    private String loadGitInfo() {
        var props = loadProperties("git.properties");
        Map<String, Object> nestedMap = buildNestedMap(props);
        
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(nestedMap);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex.getMessage(), ex);
        } 
    }
    
    private Properties loadProperties(String resource) {
        try (InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(resource)) {
            
            if (is == null) {
                throw new IOException("The git.properties file was not found in classpath.");
            }
            
            var props = new Properties();
            props.load(is);
            
            return props;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex.getMessage(), ex);
        }
    }

    private Map<String, Object> buildNestedMap(Properties props) {
        Map<String, Object> nestedMap = new HashMap<>();
        for (String key : props.stringPropertyNames()) {
            insertNested(nestedMap, key.split("\\."), props.getProperty(key));
        }
        return nestedMap;
    }

    @SuppressWarnings("unchecked") // To be fully dynamic without pojos, this is required.
    private static void insertNested(Map<String, Object> map, String[] keys, String value) {
        Map<String, Object> current = map;

        for (int i = 0; i < keys.length - 1; i++) {
            String key = keys[i];

            current = (Map<String, Object>) current.compute(key, (k, existing) -> {
                if (existing instanceof Map) {
                    return existing;
                } else if (existing instanceof String) {
                    Map<String, Object> promoted = new HashMap<>();
                    promoted.put("full", existing);
                    return promoted;
                } else {
                    return new HashMap<>();
                }
            });
        }

        String lastKey = keys[keys.length - 1];
        Object existing = current.get(lastKey);

        if (existing instanceof Map) {
            // Insert the leaf value under "full" if last key is already a map.
            ((Map<String, Object>) existing).put("full", value);
        } else {
            current.put(lastKey, value);
        }
    }
}
