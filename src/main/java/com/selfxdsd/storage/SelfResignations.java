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
import com.selfxdsd.core.contributors.StoredContributor;
import com.selfxdsd.core.tasks.StoredResignation;
import com.selfxdsd.core.tasks.TaskResignations;
import org.jooq.Record;
import org.jooq.Result;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.selfxdsd.storage.generated.jooq.Tables.*;

/**
 * All the Resignations in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.8
 * @todo #129:30min Implement the methods of this class using
 *  JOOQ and write integration tests for them.
 */
public final class SelfResignations implements Resignations {

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
    public SelfResignations(
        final Storage storage,
        final Database database
    ) {
        this.storage = storage;
        this.database = database;
    }

    @Override
    public Resignations ofTask(final Task task) {
        final Project project = task.project();
        final List<Resignation> ofTask = new ArrayList<>();
        final Result<Record> result = this.database
            .jooq()
            .select()
            .from(SLF_RESIGNATIONS_XDSD)
            .where(
                SLF_RESIGNATIONS_XDSD.REPO_FULLNAME.eq(
                    project.repoFullName()
                ).and(
                    SLF_RESIGNATIONS_XDSD.PROVIDER.eq(project.provider())
                        .and(SLF_RESIGNATIONS_XDSD.ISSUEID.eq(task.issueId()))
                )
            ).fetch();
        for(final Record rec : result) {
            ofTask.add(
                new StoredResignation(
                    task,
                    new StoredContributor(
                        rec.getValue(SLF_RESIGNATIONS_XDSD.USERNAME),
                        rec.getValue(SLF_RESIGNATIONS_XDSD.PROVIDER),
                        this.storage
                    ),
                    rec.getValue(SLF_RESIGNATIONS_XDSD.TIMESTAMP),
                    rec.getValue(SLF_RESIGNATIONS_XDSD.REASON)
                )
            );
        }
        return new TaskResignations(
            task,
            () -> ofTask.stream(),
            this.storage
        );
    }

    @Override
    public Resignation register(
        final Task task, final String reason
    ) {
        final Contributor assignee = task.assignee();
        if(assignee == null) {
            throw new IllegalStateException(
                "Task is not assigned, cannot register a resignation."
            );
        }
        final Project project = task.project();
        final LocalDateTime timestamp = LocalDateTime.now();
        final int inserted = this.database.jooq().insertInto(
            SLF_RESIGNATIONS_XDSD,
            SLF_RESIGNATIONS_XDSD.REPO_FULLNAME,
            SLF_RESIGNATIONS_XDSD.PROVIDER,
            SLF_RESIGNATIONS_XDSD.USERNAME,
            SLF_RESIGNATIONS_XDSD.ISSUEID,
            SLF_RESIGNATIONS_XDSD.TIMESTAMP,
            SLF_RESIGNATIONS_XDSD.REASON
        ).values(
            project.repoFullName(),
            project.provider(),
            assignee.username(),
            task.issueId(),
            timestamp,
            reason
        ).execute();
        if(inserted != 1) {
            throw new IllegalStateException(
                "Something went wrong while trying to register "
                + "a resignation for Task #" + task.issueId() + " "
                + "from Project " + project.repoFullName() + " at "
                + project.provider() + ". "
                + "Assignee is " + assignee.username() + "."
            );
        } else {
            return new StoredResignation(
                task,
                assignee,
                timestamp,
                reason
            );
        }
    }

    @Override
    public Iterator<Resignation> iterator() {
        throw new UnsupportedOperationException(
            "You cannot iterate over all Resignations in Self."
        );
    }
}
