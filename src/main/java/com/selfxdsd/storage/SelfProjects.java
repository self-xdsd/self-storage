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
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.BasePaged;
import com.selfxdsd.core.StoredUser;
import com.selfxdsd.core.managers.StoredProjectManager;
import com.selfxdsd.core.projects.PmProjects;
import com.selfxdsd.core.projects.StoredProject;
import com.selfxdsd.core.projects.UserProjects;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.Iterator;

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
            Page.all()
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
        super(page, () -> database.jooq().fetchCount(SLF_PROJECTS_XDSD));
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
        final Page page = super.current();
        final DSLContext jooq = this.database.connect().jooq();
        return new PmProjects(
            projectManagerId,
            () -> this.database.connect().jooq()
                .select()
                .from(jooq.select()
                    .from(SLF_PROJECTS_XDSD)
                    .limit(page.getSize())
                    .offset((page.getNumber() - 1) * page.getSize())
                    .asTable("projects_page"))
                .join(SLF_USERS_XDSD)
                .on(SLF_USERS_XDSD.USERNAME
                    .eq(DSL.field("projects_page.username")
                        .cast(String.class))
                    .and(SLF_USERS_XDSD.PROVIDER
                        .eq(DSL.field("projects_page.provider")
                            .cast(String.class))))
                .join(SLF_PMS_XDSD)
                .on(DSL.field("projects_page.pmid").eq(SLF_PMS_XDSD.ID))
                .where(DSL.field("projects_page.pmid").eq(projectManagerId))
                .stream()
                .map(rec -> projectFromRecord(rec, true)));
    }

    @Override
    public Projects ownedBy(final User user) {
        final Page page = super.current();
        final DSLContext jooq = this.database.connect().jooq();
        return new UserProjects(
            user,
            () -> jooq
                .select()
                .from(jooq.select()
                    .from(SLF_PROJECTS_XDSD)
                    .limit(page.getSize())
                    .offset((page.getNumber() - 1) * page.getSize())
                    .asTable("projects_page"))
                .join(SLF_USERS_XDSD)
                .on(SLF_USERS_XDSD.USERNAME
                    .eq(DSL.field("projects_page.username")
                        .cast(String.class))
                    .and(SLF_USERS_XDSD.PROVIDER
                        .eq(DSL.field("projects_page.provider")
                            .cast(String.class))))
                .join(SLF_PMS_XDSD)
                .on(DSL.field("projects_page.pmid").eq(SLF_PMS_XDSD.ID))
                .where(SLF_USERS_XDSD.USERNAME
                    .eq(user.username())
                    .and(DSL.field("projects_page.provider")
                        .eq(user.provider().name())))
                .stream()
                .map(rec -> projectFromRecord(rec, true)));
    }

    @Override
    public Project getProjectById(
        final String repoFullName,
        final String repoProvider
    ) {
        final Page page = super.current();
        final SelectOnConditionStep<Record> select = this.database.jooq()
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
                );
        final Project project;
        if (page.getSize() == Integer.MAX_VALUE) {
            project = select
                .where(
                    SLF_PROJECTS_XDSD.REPO_FULLNAME.eq(repoFullName).and(
                        SLF_PROJECTS_XDSD.PROVIDER.eq(repoProvider)
                    )
                )
                .stream()
                .map(rec -> projectFromRecord(rec, false))
                .findFirst()
                .orElse(null);
        } else {
            project = select
                .stream()
                .filter(r -> r.getValue(SLF_PROJECTS_XDSD.REPO_FULLNAME)
                    .equals(repoFullName)
                    && r.getValue(SLF_PROJECTS_XDSD.PROVIDER)
                    .equals(repoProvider))
                .map(rec -> projectFromRecord(rec, false))
                .findFirst()
                .orElse(null);
        }
        return project;
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
        final DSLContext jooq = this.database.jooq();
        return jooq
            .select()
            .from(jooq.select()
                .from(SLF_PROJECTS_XDSD)
                .limit(page.getSize())
                .offset((page.getNumber() - 1) * page.getSize())
                .asTable("projects_page"))
            .join(SLF_USERS_XDSD)
            .on(DSL.field("projects_page.username")
                    .eq(SLF_USERS_XDSD.USERNAME)
                    .and(DSL.field("projects_page.provider")
                        .eq(SLF_USERS_XDSD.PROVIDER)))
            .join(SLF_PMS_XDSD)
            .on(DSL.field("projects_page.pmid").eq(SLF_PMS_XDSD.ID))
            .stream()
            .map(rec -> projectFromRecord(rec, true))
            .iterator();
    }

    /**
     * Build a Project from a JOOQ Record.
     * @param rec Record representing the Project's data.
     * @param isFromPagedTable Marks that record is from projects paged table.
     * @return Project.
     */
    private Project projectFromRecord(final Record rec,
                                      final boolean isFromPagedTable){
        final Field<?> webhookField;
        final Field<?> repoFullNameField;
        if (isFromPagedTable) {
            webhookField = DSL.field(DSL
                .name("projects_page", "webhook_token"));
            repoFullNameField = DSL.field(DSL
                .name("projects_page", "repo_fullname"));
        } else {
            webhookField = SLF_PROJECTS_XDSD.WEBHOOK_TOKEN;
            repoFullNameField = SLF_PROJECTS_XDSD.REPO_FULLNAME;
        }
        final User owner = new StoredUser(
            rec.get(SLF_USERS_XDSD.USERNAME),
            rec.get(SLF_USERS_XDSD.EMAIL),
            rec.get(SLF_USERS_XDSD.ROLE),
            rec.get(SLF_USERS_XDSD.PROVIDER),
            this.storage
        );
        return new StoredProject(
            owner,
            rec.get(repoFullNameField).toString(),
            rec.get(webhookField).toString(),
            new StoredProjectManager(
                rec.get(SLF_PMS_XDSD.ID),
                rec.get(SLF_PMS_XDSD.USERID),
                rec.get(SLF_PMS_XDSD.USERNAME),
                rec.get(SLF_PMS_XDSD.PROVIDER),
                rec.get(SLF_PMS_XDSD.ACCESS_TOKEN),
                rec.get(SLF_PMS_XDSD.COMMISSION).doubleValue(),
                this.storage
            ),
            this.storage
        );
    }


}
