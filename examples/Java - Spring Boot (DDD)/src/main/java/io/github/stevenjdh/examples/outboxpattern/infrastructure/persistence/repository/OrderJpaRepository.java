/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.infrastructure.persistence.repository;

import io.github.stevenjdh.examples.outboxpattern.infrastructure.persistence.entity.OrderEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

}
