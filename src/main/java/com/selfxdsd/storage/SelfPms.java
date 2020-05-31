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

import static com.selfxdsd.storage.generated.jooq.tables.SlfPmsXdsd.SLF_PMS_XDSD;

/**
 * Project managers in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #19:30min Continue implementing and writing integration tests
 *  for SelfPms's methods.
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
                final Record rec = result.get(0);
                final ProjectManager found = new StoredProjectManager(
                    rec.getValue(SLF_PMS_XDSD.ID),
                    rec.getValue(SLF_PMS_XDSD.PROVIDER),
                    rec.get(SLF_PMS_XDSD.ACCESS_TOKEN),
                    this.storage
                );
                return found;
            }
        }
        return null;
    }

    @Override
    public ProjectManager pick(final String provider) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public ProjectManager register(
        final String provider,
        final String accessToken
    ) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Iterator<ProjectManager> iterator() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }
}
