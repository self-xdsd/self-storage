/**
 * Copyright (c) 2020, Self XDSD Contributors
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.selfxdsd.storage;

import com.selfxdsd.api.ProjectManager;
import com.selfxdsd.api.ProjectManagers;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.managers.StoredProjectManager;
import org.jooq.Record;
import org.jooq.Result;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.selfxdsd.storage.generated.jooq.tables.SlfPmsXdsd.SLF_PMS_XDSD;

/**
 * Project managers in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #20:30min Refactor {@link PagedIterator} into its on file and test it.
 *  This way it could be used by other iterables that might need paging
 *  (i.e. {@link SelfUsers}).
 */
public final class SelfPms implements ProjectManagers {
    /**
     * Parent Storage.
     */
    private final Storage storage;

    /**
     * Database.
     */
    private final Database database;

    /**
     * Ctor.
     * @param storage Parent Storage.
     * @param database Database.
     */
    public SelfPms(
        final Storage storage,
        final Database database
    ) {
        this.storage = storage;
        this.database = database;
    }

    @Override
    public ProjectManager getById(final int projectManagerId) {
        try (final Database connected = this.database.connect()) {
            final Result<Record> result = connected.jooq()
                .select()
                .from(SLF_PMS_XDSD)
                .where(SLF_PMS_XDSD.ID.eq(projectManagerId))
                .fetch();
            if(result.size() > 0) {
                return buildProjectManager(result.get(0));
            }
        }
        return null;
    }

    @Override
    public ProjectManager pick(final String provider) {
        try (final Database connected = this.database.connect()) {
            final Result<Record> result = connected.jooq()
                .select()
                .from(SLF_PMS_XDSD)
                .where(SLF_PMS_XDSD.PROVIDER.eq(provider))
                .limit(1)
                .fetch();
            if(result.size() > 0) {
                return this.buildProjectManager(result.get(0));
            }
        }
        return null;
    }

    @Override
    public ProjectManager register(
        final String provider,
        final String accessToken) {
        try (final Database connected = this.database.connect()) {
            final int pmId = connected.jooq()
                .insertInto(SLF_PMS_XDSD,
                    SLF_PMS_XDSD.PROVIDER,
                    SLF_PMS_XDSD.ACCESS_TOKEN)
                .values(provider, accessToken)
                .execute();
            if(pmId > 0){
                return new StoredProjectManager(pmId,
                    provider,
                    accessToken,
                    storage);
            }
        }
        throw new IllegalStateException("Something went wrong while"
            + " inserting a PM into database.");
    }

    @Override
    public Iterator<ProjectManager> iterator() {
        final int maxRecords;
        try (final Database connected = this.database.connect()) {
            maxRecords = connected.jooq().fetchCount(SLF_PMS_XDSD);
        }
        return new PagedIterator<>(100, maxRecords) {
            @Override
            List<ProjectManager> fetchNextPage(final int offset,
                                               final int pageSize) {
                try (final Database connected =
                         SelfPms.this.database.connect()) {
                    return connected.jooq()
                        .select()
                        .from(SLF_PMS_XDSD)
                        .limit(pageSize)
                        .offset(offset)
                        .fetch()
                        .stream()
                        .map(SelfPms.this::buildProjectManager)
                        .collect(Collectors.toList());
                }
            }
        };
    }

    /**
     * Builds a PM from a {@link Record}.
     * @param record Record.
     * @return ProjectManager.
     */
    private ProjectManager buildProjectManager(final Record record){
        return new StoredProjectManager(
            record.getValue(SLF_PMS_XDSD.ID),
            record.getValue(SLF_PMS_XDSD.PROVIDER),
            record.get(SLF_PMS_XDSD.ACCESS_TOKEN),
            this.storage
        );
    }

    /**
     * A paged iterator based on offset page fetching.
     * <br/>
     * Upon calling next(), if the internal page index is at the end,
     * a new page will be fetched from data source.
     * with {@link PagedIterator#fetchNextPage(int, int)}.
     * @param <E> The type of elements returned by this iterator.
     */
    private abstract static class PagedIterator<E> implements Iterator<E> {
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
         * @param maxRecords Maximum records.
         */
        PagedIterator(final int pageSize, final int maxRecords) {
            this.pageSize = pageSize;
            this.maxRecords = maxRecords;
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
            final int index = currPosition % maxRecords;
            if (index == 0) {
                page = fetchNextPage(currPosition, pageSize);
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
}
