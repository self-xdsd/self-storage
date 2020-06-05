package com.selfxdsd.storage;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.mockito.Mockito.*;

/**
 * Unit tests for @{{@link PagedIterator}}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class PagedIteratorTestCase {

    /**
     * Iterates by fetching pages from data source.
     * Should fetch 3 pages with sizes: 2, 2, 1
     */
    @SuppressWarnings("unchecked")
    @Test
    public void iteratesByFetchingPages() {
        final List<Integer> dataSource = List.of(1, 2, 3, 4, 5);
        BiFunction<Integer, Integer, List<Integer>> pageFetcher =
            Mockito.mock(BiFunction.class);
        final PagedIterator<Integer> iterator = PagedIterator
            .create(2, dataSource.size(), pageFetcher);
        final Iterable<Integer> iterable = () -> iterator;

        when(pageFetcher.apply(anyInt(), anyInt())).then(inv -> {
            final int start = (Integer) inv.getArguments()[0];
            final int end = Math.min(start + (Integer) inv.getArguments()[1],
                dataSource.size());
            return dataSource.subList(start, end);
        });
        assertThat(StreamSupport
            .stream(iterable.spliterator(), false)
            .collect(Collectors.toList()), iterableWithSize(5));
        verify(pageFetcher, times(3))
            .apply(anyInt(), anyInt());
    }

    /**
     * Throws when are no more items to iterate.
     */
    @Test(expected = NoSuchElementException.class)
    public void throwsWhenAreNoMoreItemsToIterate() {
        final List<Integer> dataSource = List.of(1, 2, 3, 4, 5);
        final PagedIterator<Integer> iterator = PagedIterator
            .create(2, dataSource.size(), (offset, size) -> {
                final int start = offset;
                final int end = Math.min(start + size, dataSource.size());
                return dataSource.subList(start, end);
            });
        iterator.forEachRemaining(item -> {
        });
        iterator.next();
    }

    /**
     * Throws when page is empty.
     */
    @Test(expected = NoSuchElementException.class)
    public void throwsWhenPageIsEmpty() {
        final PagedIterator<Integer> iterator = PagedIterator
            .create(2, 5, (offset, size) -> List.of());
        iterator.next();
    }

    /**
     * Throws when fetched page is too big.
     */
    @Test(expected = IllegalStateException.class)
    public void throwsWhenFetchedPagesIsTooBig() {
        final PagedIterator<Integer> iterator = PagedIterator
            .create(1, 5, (offset, size) -> List.of(1, 2));
        iterator.next();
    }
}
