/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.repository;

import io.github.stevenjdh.examples.outboxpattern.repository.entity.OutboxEventEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventEntity, UUID> {

}
