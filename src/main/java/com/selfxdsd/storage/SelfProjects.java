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
import com.selfxdsd.core.BasePaged;
import com.selfxdsd.core.StoredUser;
import com.selfxdsd.core.managers.StoredProjectManager;
import com.selfxdsd.core.projects.PmProjects;
import com.selfxdsd.core.projects.StoredProject;
import com.selfxdsd.core.projects.UserProjects;
import org.jooq.Record;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.selfxdsd.storage.generated.jooq.tables.SlfPmsXdsd.SLF_PMS_XDSD;
import static com.selfxdsd.storage.generated.jooq.tables.SlfProjectsXdsd.SLF_PROJECTS_XDSD;
import static com.selfxdsd.storage.generated.jooq.tables.SlfUsersXdsd.SLF_USERS_XDSD;

/**
 * All the projects in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class SelfProjects extends BasePaged implements Projects {

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
    public SelfProjects(
        final Storage storage,
        final Database database
    ) {
        this(
            storage,
            database,
            new Page(1, 10)
        );
    }

    /**
     * Ctor for paging.
     * @param storage Storage.
     * @param database Database.
     * @param page Page we're on.
     */
    private SelfProjects(
        final Storage storage,
        final Database database,
        final Page page) {
        super(page,  database.jooq().fetchCount(SLF_PROJECTS_XDSD));
        this.storage = storage;
        this.database = database;
    }

    @Override
    public Project register(
        final Repo repo,
        final ProjectManager manager,
        final String webHookToken
    ) {
        if(manager == null
            || this.storage.projectManagers().getById(manager.id()) == null) {
            throw new IllegalArgumentException(
                "PM is missing, cannot register project!"
            );
        }
        this.database.jooq().insertInto(
            SLF_PROJECTS_XDSD,
            SLF_PROJECTS_XDSD.REPO_FULLNAME,
            SLF_PROJECTS_XDSD.PROVIDER,
            SLF_PROJECTS_XDSD.USERNAME,
            SLF_PROJECTS_XDSD.WEBHOOK_TOKEN,
            SLF_PROJECTS_XDSD.PMID
        ).values(
            repo.fullName(),
            repo.provider(),
            repo.owner().username(),
            webHookToken,
            manager.id()
        ).execute();
        return new StoredProject(
            repo.owner(),
            repo.fullName(),
            webHookToken,
            manager,
            this.storage
        );
    }

    @Override
    public Projects assignedTo(final int projectManagerId) {
        return new PmProjects(
            projectManagerId,
            () -> {
                final List<Project> assigned = new ArrayList<>();
                final Result<Record> result = this.database.connect().jooq()
                    .select()
                    .from(SLF_PROJECTS_XDSD)
                    .join(SLF_USERS_XDSD)
                    .on(
                        SLF_USERS_XDSD.USERNAME.eq(SLF_PROJECTS_XDSD.USERNAME)
                            .and(
                                 SLF_USERS_XDSD.PROVIDER.eq(
                                     SLF_PROJECTS_XDSD.PROVIDER
                                 )
                            )
                    ).join(SLF_PMS_XDSD)
                    .on(
                        SLF_PROJECTS_XDSD.PMID.eq(SLF_PMS_XDSD.ID)
                    )
                    .where(
                        SLF_PROJECTS_XDSD.PMID.eq(projectManagerId)
                    )
                    .fetch();
                for(final Record rec : result) {
                    assigned.add(this.projectFromRecord(rec));
                }
                return assigned.stream();
            }
        );
    }

    @Override
    public Projects ownedBy(final User user) {
        return new UserProjects(
            user,
            () -> {
                final List<Project> owned = new ArrayList<>();
                final Result<Record> result = this.database.connect().jooq()
                    .select()
                    .from(SLF_PROJECTS_XDSD)
                    .join(SLF_USERS_XDSD)
                    .on(
                        SLF_USERS_XDSD.USERNAME.eq(SLF_PROJECTS_XDSD.USERNAME)
                            .and(
                                SLF_USERS_XDSD.PROVIDER.eq(
                                    SLF_PROJECTS_XDSD.PROVIDER
                                )
                            )
                    ).join(SLF_PMS_XDSD)
                    .on(
                        SLF_PROJECTS_XDSD.PMID.eq(SLF_PMS_XDSD.ID)
                    )
                    .where(
                        SLF_PROJECTS_XDSD.USERNAME.eq(user.username()).and(
                            SLF_PROJECTS_XDSD.PROVIDER.eq(
                                user.provider().name()
                            )
                        )
                    )
                    .fetch();
                for(final Record rec : result) {
                    owned.add(this.projectFromRecord(rec));
                }
                return owned.stream();
            }
        );
    }

    @Override
    public Project getProjectById(
        final String repoFullName,
        final String repoProvider
    ) {
        final Result<Record> result = this.database.jooq()
            .select()
            .from(SLF_PROJECTS_XDSD)
            .join(SLF_USERS_XDSD)
            .on(
                SLF_PROJECTS_XDSD.USERNAME.eq(SLF_USERS_XDSD.USERNAME).and(
                    SLF_PROJECTS_XDSD.PROVIDER.eq(SLF_USERS_XDSD.PROVIDER)
                )
            )
            .join(SLF_PMS_XDSD)
            .on(
                SLF_PROJECTS_XDSD.PMID.eq(SLF_PMS_XDSD.ID)
            )
            .where(
                SLF_PROJECTS_XDSD.REPO_FULLNAME.eq(repoFullName).and(
                    SLF_PROJECTS_XDSD.PROVIDER.eq(repoProvider)
                )
            )
            .fetch();
        if(!result.isEmpty()) {
            return this.projectFromRecord(result.get(0));
        }
        return null;
    }

    @Override
    public Projects page(final Page page) {
        return new SelfProjects(
            this.storage,
            this.database,
            page
        );
    }

    @Override
    public Iterator<Project> iterator() {
        final Page page = super.current();
        return this.database.jooq()
            .select()
            .from(SLF_PROJECTS_XDSD)
            .join(SLF_USERS_XDSD)
            .on(
                SLF_PROJECTS_XDSD.USERNAME.eq(SLF_USERS_XDSD.USERNAME).and(
                    SLF_PROJECTS_XDSD.PROVIDER.eq(SLF_USERS_XDSD.PROVIDER)
                )
            )
            .join(SLF_PMS_XDSD)
            .on(
                SLF_PROJECTS_XDSD.PMID.eq(SLF_PMS_XDSD.ID)
            )
            .limit(page.getSize())
            .offset((page.getNumber()  - 1) * page.getSize())
            .fetch()
            .stream()
            .map(this::projectFromRecord)
            .iterator();
    }

    /**
     * Build a Project from a JOOQ Record.
     * @param rec Record representing the Project's data.
     * @return Project.
     */
    private Project projectFromRecord(final Record rec) {
        final User owner = new StoredUser(
            rec.getValue(SLF_USERS_XDSD.USERNAME),
            rec.getValue(SLF_USERS_XDSD.EMAIL),
            rec.getValue(SLF_USERS_XDSD.ROLE),
            rec.getValue(SLF_USERS_XDSD.PROVIDER),
            this.storage
        );
        final Project built = new StoredProject(
            owner,
            rec.getValue(SLF_PROJECTS_XDSD.REPO_FULLNAME),
            rec.getValue(SLF_PROJECTS_XDSD.WEBHOOK_TOKEN),
            new StoredProjectManager(
                rec.getValue(SLF_PMS_XDSD.ID),
                rec.getValue(SLF_PMS_XDSD.USERID),
                rec.getValue(SLF_PMS_XDSD.USERNAME),
                rec.getValue(SLF_PMS_XDSD.PROVIDER),
                rec.getValue(SLF_PMS_XDSD.ACCESS_TOKEN),
                this.storage
            ),
            this.storage
        );
        return built;
    }
}
