/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.shared.pagination;

import java.util.List;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;

public record PagedResult<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    boolean hasPrevious,
    boolean hasNext
) {
    public int totalPages() {
        return (int) Math.ceil((double) totalElements / size);
    }
    
    public <U> PagedResult<U> map(Function<? super T, ? extends U> converter) {
        List<U> mappedContent = content.stream()
                .map(converter)
                // Using this instead of toList() because it will preserve type inferred by map()
                // and remove the need for casting and having an unchecked cast warning.
                .collect(toList());
        
        return new PagedResult<>(
            mappedContent,
            page,
            size,
            totalElements,
            hasPrevious,
            hasNext
        );
    }
}
