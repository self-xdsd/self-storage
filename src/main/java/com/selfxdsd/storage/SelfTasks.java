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

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.StoredUser;
import com.selfxdsd.core.managers.StoredProjectManager;
import com.selfxdsd.core.projects.StoredProject;
import com.selfxdsd.core.tasks.StoredTask;
import org.jooq.Record;
import org.jooq.Result;

import java.util.Iterator;

import static com.selfxdsd.storage.generated.jooq.Tables.*;
import static com.selfxdsd.storage.generated.jooq.tables.SlfPmsXdsd.SLF_PMS_XDSD;
import static com.selfxdsd.storage.generated.jooq.tables.SlfProjectsXdsd.SLF_PROJECTS_XDSD;
import static com.selfxdsd.storage.generated.jooq.tables.SlfUsersXdsd.SLF_USERS_XDSD;

/**
 * All the tasks registered in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #37:30min Continue implementing and writing integration tests
 *  for the methods of SelfTasks.
 */
public final class SelfTasks implements Tasks {

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
    public SelfTasks(
        final Storage storage,
        final Database database
    ) {
        this.storage = storage;
        this.database = database;
    }

    @Override
    public Task getById(
        final String issueId,
        final String repoFullName,
        final String provider
    ) {
        try (final Database connected = this.database.connect()) {
            final Result<Record> result = connected.jooq()
                .select()
                .from(SLF_TASKS_XDSD)
                .join(SLF_PROJECTS_XDSD)
                .on(
                    SLF_PROJECTS_XDSD.REPO_FULLNAME.eq(
                        SLF_TASKS_XDSD.REPO_FULLNAME
                    ).and(
                        SLF_PROJECTS_XDSD.PROVIDER.eq(
                            SLF_TASKS_XDSD.PROVIDER
                        )
                    )
                ).join(SLF_USERS_XDSD)
                .on(
                    SLF_PROJECTS_XDSD.USERNAME.eq(SLF_USERS_XDSD.USERNAME).and(
                        SLF_PROJECTS_XDSD.PROVIDER.eq(SLF_USERS_XDSD.PROVIDER)
                    )
                ).join(SLF_PMS_XDSD)
                .on(
                    SLF_PROJECTS_XDSD.PMID.eq(SLF_PMS_XDSD.ID)
                )
                .where(
                    SLF_TASKS_XDSD.REPO_FULLNAME.eq(repoFullName).and(
                        SLF_TASKS_XDSD.PROVIDER.eq(provider).and(
                            SLF_TASKS_XDSD.ISSUEID.eq(issueId)
                        )
                    )
                )
                .fetch();
            if(!result.isEmpty()) {
                return this.taskFromRecord(result.get(0));
            }
        }
        return null;
    }

    @Override
    public Task register(final Issue issue) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Tasks ofProject(
        final String repoFullName,
        final String repoProvider
    ) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Tasks ofContributor(
        final String username,
        final String provider
    ) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Iterator<Task> iterator() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    /**
     * Build a Task from a JOOQ Record.
     * @param rec Record representing the Task's data.
     * @return Task.
     */
    private Task taskFromRecord(final Record rec) {
        final Project project = new StoredProject(
            new StoredUser(
                rec.getValue(SLF_USERS_XDSD.USERNAME),
                rec.getValue(SLF_USERS_XDSD.EMAIL),
                rec.getValue(SLF_USERS_XDSD.PROVIDER),
                this.storage
            ),
            rec.getValue(SLF_PROJECTS_XDSD.REPO_FULLNAME),
            new StoredProjectManager(
                rec.getValue(SLF_PMS_XDSD.ID),
                rec.getValue(SLF_PMS_XDSD.PROVIDER),
                rec.getValue(SLF_PMS_XDSD.ACCESS_TOKEN),
                this.storage
            ),
            this.storage
        );
        return new StoredTask(
            project,
            rec.getValue(SLF_TASKS_XDSD.ISSUEID),
            rec.getValue(SLF_TASKS_XDSD.ROLE),
            rec.getValue(SLF_TASKS_XDSD.PROVIDER),
            this.storage,
            rec.getValue(SLF_TASKS_XDSD.USERNAME),
            rec.getValue(SLF_TASKS_XDSD.ASSIGNED),
            rec.getValue(SLF_TASKS_XDSD.DEADLINE)
        );
    }
}
