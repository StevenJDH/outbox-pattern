/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.boot;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BannerPrinter {

    private final String bannerPath;
    private final boolean useCustomBanner;
    private static final Logger LOG = LoggerFactory.getLogger(BannerPrinter.class.getName());

    // Using custom config section because quarkus seems to update quarkus.banner.path after rendering in native mode.
    public BannerPrinter(@ConfigProperty(name = "banner.path", defaultValue = "default_banner.txt")String bannerPath,
                         @ConfigProperty(name = "banner.enabled", defaultValue = "false")boolean useCustomBanner) {
        
        this.bannerPath = bannerPath;
        this.useCustomBanner = useCustomBanner;
    }

    public void onStart(@Observes StartupEvent evt) {
        if (!useCustomBanner) {
            return;
        }
        
        String banner = loadBanner();

        if (banner != null) {
            LOG.info("\n\n{}", banner.stripTrailing());
        } else {
            LOG.warn("The [{}] file was not found in classpath.", bannerPath);
        }
    }

    private String loadBanner() {
        try (var is = BannerPrinter.class.getClassLoader().getResourceAsStream(bannerPath)) {
            if (is == null) {
                return null;
            }
            try (var scanner = new Scanner(is, StandardCharsets.UTF_8.name())) {
                return scanner.useDelimiter("\\A").next();
            }
        } catch (IOException ex) {
            LOG.warn("Failed to load the [{}] file.", bannerPath);
            return null;
        }
    }
}
