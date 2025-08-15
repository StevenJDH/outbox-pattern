/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.infrastructure.boot;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BannerPrinter {

    private static final Logger LOG = LoggerFactory.getLogger(BannerPrinter.class.getName());
    
    public void onStart(@Observes StartupEvent evt) {
        String banner = loadBanner();
        
        if (banner != null) {
            LOG.info("\n\n{}", banner.stripTrailing());
        } else {
            LOG.warn("The banner.txt file was not found in classpath.");
        }
    }
    
    private String loadBanner() {
        try(var is = BannerPrinter.class.getClassLoader().getResourceAsStream("banner.txt")) {
            if (is == null) {
                return null;
            }
            try (var scanner = new Scanner(is, StandardCharsets.UTF_8.name())) {
                return scanner.useDelimiter("\\A").next();
            }
        } catch (IOException ex) {
            LOG.warn("Failed to load the banner.txt file.");
            return null;
        }
    }
}
