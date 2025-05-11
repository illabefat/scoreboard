package org.ilzi.scorecard.util.cache;

import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

class CachedReferenceTest {

    private final Supplier<String> supplier = mock(Supplier.class);
    private final CachedReference<String> cachedReference = new CachedReference<>(supplier);

    @Test
    void loads_value_using_loader() {
        // given
        given(supplier.get()).willReturn("value");

        // when
        final var result = cachedReference.get();
        final var result2 = cachedReference.get();

        // then
        assertThat(result).isEqualTo("value");
        assertThat(result2).isEqualTo("value");
        then(supplier).should(times(1)).get();
    }

    @Test
    void reloads_value_after_invalidation() {
        // given
        given(supplier.get()).willReturn("value");

        // when
        final var result = cachedReference.get();
        cachedReference.invalidate();
        final var result2 = cachedReference.get();

        // then
        assertThat(result).isEqualTo("value");
        assertThat(result2).isEqualTo("value");
        then(supplier).should(times(2)).get();
    }
}