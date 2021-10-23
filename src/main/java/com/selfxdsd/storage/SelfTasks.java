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

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.StoredUser;
import com.selfxdsd.core.contracts.StoredContract;
import com.selfxdsd.core.contributors.StoredContributor;
import com.selfxdsd.core.managers.StoredProjectManager;
import com.selfxdsd.core.projects.StoredProject;
import com.selfxdsd.core.tasks.*;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectOnConditionStep;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.selfxdsd.storage.generated.jooq.Tables.*;
import static com.selfxdsd.storage.generated.jooq.tables.SlfPmsXdsd.SLF_PMS_XDSD;
import static com.selfxdsd.storage.generated.jooq.tables.SlfProjectsXdsd.SLF_PROJECTS_XDSD;
import static com.selfxdsd.storage.generated.jooq.tables.SlfUsersXdsd.SLF_USERS_XDSD;

/**
 * All the tasks registered in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #312:60min Implement and test method updateEstimation(...).
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
        final String provider,
        final boolean isPullRequest
    ) {
        final Result<Record> result = this.selectTasks(this.database)
            .where(
                SLF_TASKS_XDSD.REPO_FULLNAME.eq(repoFullName).and(
                    SLF_TASKS_XDSD.PROVIDER.eq(provider).and(
                        SLF_TASKS_XDSD.ISSUEID.eq(issueId).and(
                            SLF_TASKS_XDSD.ISPULLREQUEST.eq(isPullRequest)
                        )
                    )
                )
            )
            .fetch();
        if(!result.isEmpty()) {
            return this.taskFromRecord(result.get(0));
        }
        return null;
    }

    @Override
    public Task register(final Issue issue) {
        final Project project = this.storage.projects().getProjectById(
            issue.repoFullName(), issue.provider()
        );
        if (project == null) {
            throw new IllegalStateException(
                "Project not found, can't register Issue."
            );
        } else {
            final boolean isPullRequest = issue.isPullRequest();
            final int estimation = issue.estimation().minutes();
            final String role = issue.role();
            this.database.jooq().insertInto(
                SLF_TASKS_XDSD,
                SLF_TASKS_XDSD.REPO_FULLNAME,
                SLF_TASKS_XDSD.ISSUEID,
                SLF_TASKS_XDSD.PROVIDER,
                SLF_TASKS_XDSD.ISPULLREQUEST,
                SLF_TASKS_XDSD.ROLE,
                SLF_TASKS_XDSD.ESTIMATION_MINUTES
            ).values(
                issue.repoFullName(),
                issue.issueId(),
                issue.provider(),
                isPullRequest,
                role,
                estimation
            ).execute();
            return new StoredTask(
                project,
                issue.issueId(),
                role,
                estimation,
                isPullRequest,
                this.storage
            );
        }
    }

    @Override
    public Task assign(
        final Task task,
        final Contract contract,
        final int days
    ) {
        final Project proj = task.project();
        final Contract.Id contractId = contract.contractId();
        final Contract.Id assignee = new Contract.Id(
            proj.repoFullName(),
            contractId.getContributorUsername(),
            proj.provider(),
            task.role()
        );
        if(!contractId.equals(assignee)) {
            throw new IllegalArgumentException(
                "Given Task cannot be assigned to the given Contract. "
              + "The project or the role is not a match."
            );
        }
        final String issueId = task.issueId();
        final LocalDateTime assigned = LocalDateTime.now();
        final int updated = this.database.jooq().update(SLF_TASKS_XDSD)
            .set(
                SLF_TASKS_XDSD.USERNAME,
                assignee.getContributorUsername()
            ).set(
                SLF_TASKS_XDSD.ASSIGNED,
                assigned
            ).set(
                SLF_TASKS_XDSD.DEADLINE,
                assigned.plusDays(days)
            ).where(
                SLF_TASKS_XDSD.ISSUEID.eq(issueId).and(
                    SLF_TASKS_XDSD.REPO_FULLNAME.eq(proj.repoFullName()).and(
                        SLF_TASKS_XDSD.PROVIDER.eq(proj.provider()).and(
                            SLF_TASKS_XDSD.ISPULLREQUEST.eq(
                                task.isPullRequest()
                            )
                        )
                    )
                )
            ).execute();
        if(updated == 1) {
            return new StoredTask(
                contract,
                issueId,
                this.storage,
                assigned,
                assigned.plusDays(days),
                task.estimation(),
                task.isPullRequest()
            );
        }
        return null;
    }

    @Override
    public Task unassign(final Task task) {
        final Project proj = task.project();
        final String issueId = task.issueId();
        final int updated = this.database.jooq().update(SLF_TASKS_XDSD)
            .set(SLF_TASKS_XDSD.USERNAME, (String) null)
            .set(SLF_TASKS_XDSD.ASSIGNED, (LocalDateTime) null)
            .set(SLF_TASKS_XDSD.DEADLINE, (LocalDateTime) null)
            .where(
                SLF_TASKS_XDSD.ISSUEID.eq(issueId).and(
                    SLF_TASKS_XDSD.REPO_FULLNAME.eq(proj.repoFullName()).and(
                        SLF_TASKS_XDSD.PROVIDER.eq(proj.provider()).and(
                            SLF_TASKS_XDSD.ISPULLREQUEST.eq(
                                task.isPullRequest()
                            )
                        )
                    )
                )
            ).execute();
        if(updated == 1) {
            return new StoredTask(
                proj,
                issueId,
                task.role(),
                task.estimation(),
                task.isPullRequest(),
                this.storage
            );
        }
        return null;
    }

    @Override
    public Task updateEstimation(final Task task, final int estimation) {
        return null;
    }

    @Override
    public Tasks ofProject(
        final String repoFullName,
        final String repoProvider
    ) {
        return new ProjectTasks(
            repoFullName,
            repoProvider,
            new Supplier<Stream<Task>>() {
                @Override
                public Stream<Task> get() {
                    final List<Task> ofProject = new ArrayList<>();
                    final Result<Record> result = selectTasks(database)
                        .where(
                            SLF_TASKS_XDSD.REPO_FULLNAME.eq(repoFullName).and(
                                SLF_TASKS_XDSD.PROVIDER.eq(repoProvider)
                            )
                        )
                        .fetch();
                    for(final Record rec : result) {
                        ofProject.add(
                            taskFromRecord(rec)
                        );
                    }
                    return ofProject.stream();
                }
            },
            this.storage
        );
    }

    @Override
    public Tasks ofContributor(
        final String username,
        final String provider
    ) {
        return new ContributorTasks(
            username,
            provider,
            () -> {
                final List<Task> ofContributor = new ArrayList<>();
                final Result<Record> result = this.selectTasks(this.database)
                    .where(
                        SLF_TASKS_XDSD.USERNAME.eq(username).and(
                            SLF_TASKS_XDSD.PROVIDER.eq(provider)
                        )
                    )
                    .fetch();
                for(final Record rec : result) {
                    ofContributor.add(
                        this.taskFromRecord(rec)
                    );
                }
                return ofContributor.stream();
            },
            this.storage
        );
    }

    @Override
    public Tasks ofContract(final Contract.Id id) {
        return new ContractTasks(
            id,
            () -> {
                final List<Task> ofContract = new ArrayList<>();
                final Result<Record> result = this.selectTasks(this.database)
                    .where(
                        SLF_TASKS_XDSD.USERNAME.eq(
                            id.getContributorUsername()
                        ).and(
                            SLF_TASKS_XDSD.REPO_FULLNAME.eq(
                                id.getRepoFullName()
                            ).and(
                                SLF_TASKS_XDSD.ROLE.eq(id.getRole()).and(
                                    SLF_TASKS_XDSD.PROVIDER.eq(id.getProvider())
                                )
                            )
                        )
                    )
                    .fetch();
                for(final Record rec : result) {
                    ofContract.add(
                        this.taskFromRecord(rec)
                    );
                }
                return ofContract.stream();
            },
            this.storage
        );
    }

    @Override
    public Tasks unassigned() {
        return new UnassignedTasks(
            () -> {
                final List<Task> unassigned = new ArrayList<>();
                final Result<Record> result = this.selectTasks(this.database)
                    .where(SLF_TASKS_XDSD.USERNAME.isNull())
                    .limit(100)
                    .fetch();
                for(final Record rec : result) {
                    unassigned.add(
                        this.taskFromRecord(rec)
                    );
                }
                return unassigned.stream();
            },
            this.storage
        );
    }

    @Override
    public boolean remove(final Task task) {
        final Project proj = task.project();
        final String issueId = task.issueId();
        final int deleted = this.database.jooq().deleteFrom(SLF_TASKS_XDSD)
            .where(
                SLF_TASKS_XDSD.ISSUEID.eq(issueId).and(
                    SLF_TASKS_XDSD.REPO_FULLNAME.eq(proj.repoFullName()).and(
                        SLF_TASKS_XDSD.PROVIDER.eq(proj.provider()).and(
                            SLF_TASKS_XDSD.ISPULLREQUEST.eq(
                                task.isPullRequest()
                            )
                        )
                    )
                )
            ).execute();
        return deleted == 1;
    }

    @Override
    public Iterator<Task> iterator() {
        final int maxRecords = this.database.jooq().fetchCount(SLF_TASKS_XDSD);
        return PagedIterator.create(
            100,
            maxRecords,
            (offset, size) -> this.selectTasks(this.database)
                .limit(size)
                .offset(offset)
                .fetch()
                .stream()
                .map(SelfTasks.this::taskFromRecord)
                .collect(Collectors.toList())
        );
    }

    /**
     * Built the jooq SELECT/JOIN clause.
     * A Task is linked to a Project and to a Contract, so we select the tasks,
     * JOINED with Projects (also with Users + PMs to have the whole Project),
     * and JOINED with Contracts (also with Contributors).
     * @param connected Connected Database instance.
     * @return JOOQ SELECT, to which we will apply the WHERE clause.
     * @checkstyle LineLength (100 lines)
     */
    private SelectOnConditionStep<Record> selectTasks(final Database connected){
        return connected.jooq()
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
            ).leftJoin(SLF_CONTRACTS_XDSD)
            .on(
                SLF_TASKS_XDSD.ROLE.eq(SLF_CONTRACTS_XDSD.ROLE).and(
                    SLF_TASKS_XDSD.REPO_FULLNAME.eq(SLF_CONTRACTS_XDSD.REPO_FULLNAME).and(
                        SLF_TASKS_XDSD.PROVIDER.eq(SLF_CONTRACTS_XDSD.PROVIDER).and(
                            SLF_TASKS_XDSD.USERNAME.eq(SLF_CONTRACTS_XDSD.USERNAME)
                        )
                    )
                )
            ).leftJoin(SLF_CONTRIBUTORS_XDSD)
            .on(
                SLF_CONTRACTS_XDSD.USERNAME.eq(SLF_CONTRIBUTORS_XDSD.USERNAME).and(
                    SLF_CONTRACTS_XDSD.PROVIDER.eq(SLF_CONTRIBUTORS_XDSD.PROVIDER)
                )
            );
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
                rec.getValue(SLF_USERS_XDSD.ROLE),
                rec.getValue(SLF_USERS_XDSD.PROVIDER),
                this.storage
            ),
            rec.getValue(SLF_PROJECTS_XDSD.REPO_FULLNAME),
            rec.getValue(SLF_PROJECTS_XDSD.WEBHOOK_TOKEN),
            new StoredProjectManager(
                rec.getValue(SLF_PMS_XDSD.ID),
                rec.getValue(SLF_PMS_XDSD.USERID),
                rec.getValue(SLF_PMS_XDSD.USERNAME),
                rec.getValue(SLF_PMS_XDSD.PROVIDER),
                rec.getValue(SLF_PMS_XDSD.ACCESS_TOKEN),
                rec.getValue(SLF_PMS_XDSD.COMMISSION).doubleValue(),
                rec.getValue(SLF_PMS_XDSD.CONTRIBUTORCOMMISSION).doubleValue(),
                this.storage
            ),
            this.storage
        );
        final Task task;
        if(rec.getValue(SLF_TASKS_XDSD.USERNAME) == null) {
            task = new StoredTask(
                project,
                rec.getValue(SLF_TASKS_XDSD.ISSUEID),
                rec.getValue(SLF_TASKS_XDSD.ROLE),
                rec.getValue(SLF_TASKS_XDSD.ESTIMATION_MINUTES),
                rec.getValue(SLF_TASKS_XDSD.ISPULLREQUEST),
                this.storage
            );
        } else {
            task = new StoredTask(
                new StoredContract(
                    project,
                    new StoredContributor(
                        rec.getValue(SLF_CONTRIBUTORS_XDSD.USERNAME),
                        rec.getValue(SLF_CONTRIBUTORS_XDSD.PROVIDER),
                        this.storage
                    ),
                    BigDecimal.valueOf(
                        rec.getValue(SLF_CONTRACTS_XDSD.HOURLY_RATE)
                    ),
                    rec.getValue(SLF_CONTRACTS_XDSD.ROLE),
                    rec.getValue(SLF_CONTRACTS_XDSD.MARKEDFORREMOVAL),
                    this.storage
                ),
                rec.getValue(SLF_TASKS_XDSD.ISSUEID),
                this.storage,
                rec.getValue(SLF_TASKS_XDSD.ASSIGNED),
                rec.getValue(SLF_TASKS_XDSD.DEADLINE),
                rec.getValue(SLF_TASKS_XDSD.ESTIMATION_MINUTES),
                rec.getValue(SLF_TASKS_XDSD.ISPULLREQUEST)
            );
        }
        return task;
    }
}
