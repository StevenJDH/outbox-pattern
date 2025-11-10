/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonNodeBinaryType;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;

/**
 * The class io.hypersistence.utils.hibernate.type.json.JsonBinaryType requires knowledge of the Java
 * type it serializes, but there are no JPA annotations in use when using the Debezium Outbox Extension.
 * As such, it cannot be used in the payload type config. Instead, we create a Custom Hibernate UserType
 * by subclassing JsonBinaryType and passing the missing class information directly in code. This fixes
 * the 'Could not resolve property type' error when referenced in the payload type config.
 * 
 * Reference:
 * https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#basic-custom-type-BasicType
 */
public class PayloadJsonBinaryType extends AbstractSingleColumnStandardBasicType<JsonNode> {

    public static final PayloadJsonBinaryType INSTANCE = new PayloadJsonBinaryType();

    public PayloadJsonBinaryType() {
        super(
            JsonNodeBinaryType.INSTANCE.getJdbcTypeDescriptor(),
            JsonNodeBinaryType.INSTANCE.getJavaTypeDescriptor()
        );
    }

    @Override
    public String getName() {
        return "payload-jsonb";
    }
}
