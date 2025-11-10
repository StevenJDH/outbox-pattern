/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.api.rest.resource;

import io.github.stevenjdh.examples.outboxpattern.api.rest.dto.response.PagedOrderResponseDTO;
import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

@QuarkusTest
class OrderResourceTest {
    
    @Test
    void testEndpoint() {
        var responseBody = given()
                .when().get("/api/v1/orders")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(PagedOrderResponseDTO.class);
        
        assertThat(responseBody).isNotNull()
                .satisfies(body -> {
                    assertThat(body.content()).isEmpty();
                    assertThat(body.page()).isEqualTo(0);
                    assertThat(body.size()).isEqualTo(10);
                    assertThat(body.totalElements()).isEqualTo(0);
                    assertThat(body.totalPages()).isEqualTo(0);
                    assertThat(body.hasPrevious()).isFalse();
                    assertThat(body.hasNext()).isFalse();
                });
    }
}
