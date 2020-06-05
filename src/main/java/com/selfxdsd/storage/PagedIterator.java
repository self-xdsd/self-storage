package com.selfxdsd.storage;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

/**
 * A paged iterator based on offset page fetching.
 * <br/>
 * Upon calling next(), if the internal page index is at the end,
 * a new page will be fetched from data source.
 * with {@link PagedIterator#fetchNextPage(int, int)}.
 * @param <E> The type of elements returned by this iterator.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
abstract class PagedIterator<E> implements Iterator<E> {
    /**
     * Current iterating position. 0-index based.
     */
    private int currPosition;
    /**
     * Current page.
     */
    private List<E> page = List.of();
    /**
     * Page size.
     */
    private final int pageSize;
    /**
     * Total count of records.
     */
    private final int maxRecords;
    /**
     * Ctor.
     * @param pageSize Page size.
     * @param maxRecords Total records.
     */
    private PagedIterator(final int pageSize, final int maxRecords) {
        this.pageSize = pageSize;
        this.maxRecords = maxRecords;
    }

    /**
     * Creates a concrete {@link PagedIterator}.
     * @param pageSize Page size.
     * @param maxRecords Total records.
     * @param fetchNextPage Next page from data source.
     * @param <E> The type of elements returned by this iterator.
     * @return PagedIterator.
     */
    static <E> PagedIterator<E> create(final int pageSize,
           final int maxRecords,
           final BiFunction<Integer, Integer, List<E>> fetchNextPage) {
        return new PagedIterator<E>(pageSize, maxRecords) {
            @Override
            List<E> fetchNextPage(final int offset, final int pageSize) {
                return fetchNextPage.apply(offset, pageSize);
            }
        };
    }

    @Override
    public boolean hasNext() {
        return currPosition < maxRecords;
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more records");
        }
        final int index = currPosition % pageSize;
        if (index == 0) {
            List<E> fetchedPage = fetchNextPage(currPosition, pageSize);
            if (fetchedPage.size() > pageSize) {
                throw new IllegalStateException("Fetched size page is greater "
                    + "than the initial page size. " + fetchedPage.size()
                    + " > " + pageSize + ".");
            }
            this.page = fetchedPage;
        }
        if (page.isEmpty()) {
            throw new NoSuchElementException("No more records");
        }
        currPosition++;
        return page.get(index);
    }

    /**
     * Fetches next page to be iterated.
     * @param offset Offset
     * @param pageSize Page size
     * @return Paged List.
     */
    abstract List<E> fetchNextPage(final int offset, final int pageSize);
}
