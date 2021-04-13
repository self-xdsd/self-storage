/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
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
import java.util.stream.Collectors;

import static com.selfxdsd.storage.generated.jooq.tables.SlfPmsXdsd.SLF_PMS_XDSD;

/**
 * Project managers in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
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
        final Result<Record> result = this.database.jooq()
            .select()
            .from(SLF_PMS_XDSD)
            .where(SLF_PMS_XDSD.ID.eq(projectManagerId))
            .fetch();
        if(result.size() > 0) {
            return buildProjectManager(result.get(0));
        }
        return null;
    }

    @Override
    public ProjectManager getByUsername(
        final String username,
        final String provider
    ) {
        final Result<Record> result = this.database.jooq()
            .select()
            .from(SLF_PMS_XDSD)
            .where(
                SLF_PMS_XDSD.USERNAME.eq(username).and(
                    SLF_PMS_XDSD.PROVIDER.eq(provider)
                )
            ).fetch();
        if(result.size() > 0) {
            return buildProjectManager(result.get(0));
        }
        return null;
    }

    @Override
    public ProjectManager pick(final String provider) {
        final Result<Record> result = this.database.jooq()
            .select()
            .from(SLF_PMS_XDSD)
            .where(SLF_PMS_XDSD.PROVIDER.eq(provider))
            .limit(1)
            .fetch();
        if(result.size() > 0) {
            return this.buildProjectManager(result.get(0));
        }
        return null;
    }

    @Override
    public ProjectManager register(
        final String userId,
        final String username,
        final String provider,
        final String accessToken,
        final double projectCommission,
        final double contributorCommission
    ) {
        final int pmId = this.database.jooq()
            .insertInto(
                SLF_PMS_XDSD,
                SLF_PMS_XDSD.USERID,
                SLF_PMS_XDSD.USERNAME,
                SLF_PMS_XDSD.PROVIDER,
                SLF_PMS_XDSD.ACCESS_TOKEN,
                SLF_PMS_XDSD.COMMISSION.cast(Double.class).as("commission"),
                SLF_PMS_XDSD.CONTRIBUTORCOMMISSION.cast(Double.class)
                    .as("contributorCommission")
            )
            .values(
                userId, username, provider, accessToken,
                projectCommission, contributorCommission
            ).returning(SLF_PMS_XDSD.ID)
            .fetchOne()
            .getValue(SLF_PMS_XDSD.ID);
        if(pmId > 0){
            return new StoredProjectManager(
                pmId,
                userId,
                username,
                provider,
                accessToken,
                projectCommission,
                contributorCommission,
                this.storage
            );
        }
        throw new IllegalStateException("Something went wrong while"
            + " inserting a PM into database.");
    }

    @Override
    public Iterator<ProjectManager> iterator() {
        final int maxRecords = this.database.jooq().fetchCount(SLF_PMS_XDSD);
        return PagedIterator.create(
            100,
            maxRecords,
            (offset, size) -> this.database.jooq()
                .select()
                .from(SLF_PMS_XDSD)
                .limit(size)
                .offset(offset)
                .fetch()
                .stream()
                .map(SelfPms.this::buildProjectManager)
                .collect(Collectors.toList())
        );
    }

    /**
     * Builds a PM from a {@link Record}.
     * @param record Record.
     * @return ProjectManager.
     */
    private ProjectManager buildProjectManager(final Record record){
        return new StoredProjectManager(
            record.getValue(SLF_PMS_XDSD.ID),
            record.getValue(SLF_PMS_XDSD.USERID),
            record.getValue(SLF_PMS_XDSD.USERNAME),
            record.getValue(SLF_PMS_XDSD.PROVIDER),
            record.get(SLF_PMS_XDSD.ACCESS_TOKEN),
            record.getValue(SLF_PMS_XDSD.COMMISSION).doubleValue(),
            record.getValue(SLF_PMS_XDSD.CONTRIBUTORCOMMISSION).doubleValue(),
            this.storage
        );
    }

}
