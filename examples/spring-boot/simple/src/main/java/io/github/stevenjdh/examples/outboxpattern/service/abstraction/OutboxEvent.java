/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.service.abstraction;

import java.time.Instant;

public interface OutboxEvent<T, ID> {
    
    String getAggregateType();
    ID getAggregateId();
    String getType();
    T getPayload();
    Instant getTimestamp();
}
