/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.infrastructure.shared.exception;

public class InfrastructureException extends RuntimeException {

    private final String errorCode;

    public InfrastructureException(String message) {
        super(message);
        this.errorCode = null;
    }

    public InfrastructureException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }

    public InfrastructureException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
