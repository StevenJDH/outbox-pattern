/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025-2026 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.management.health;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.File;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class DiskSpaceHealthCheck implements HealthCheck {

    private static final long THRESHOLD_BYTES = 10L * 1024L * 1024L; // 10 MB.
    private static final String PATH = "."; // Current directory.

    @Override
    public HealthCheckResponse call() {
        var path = new File(PATH);
        boolean exists = path.exists();
        long total = exists ? path.getTotalSpace() : 0;
        long free = exists ? path.getFreeSpace() : 0;

        var builder = HealthCheckResponse.named("diskSpace")
                .withData("total", total)
                .withData("free", free)
                .withData("threshold", THRESHOLD_BYTES)
                .withData("path", path.getAbsolutePath())
                .withData("exists", exists);

        if (!exists || free < THRESHOLD_BYTES) {
            builder.down();
        } else {
            builder.up();
        }

        return builder.build();
    }
}
