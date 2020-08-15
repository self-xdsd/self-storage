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

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Contracts;
import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.StoredUser;
import com.selfxdsd.core.contracts.ContributorContracts;
import com.selfxdsd.core.contracts.ProjectContracts;
import com.selfxdsd.core.contracts.StoredContract;
import com.selfxdsd.core.contributors.StoredContributor;
import com.selfxdsd.core.managers.StoredProjectManager;
import com.selfxdsd.core.projects.StoredProject;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectOnConditionStep;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.stream.Collectors;

import static com.selfxdsd.storage.generated.jooq.Tables.*;
import static com.selfxdsd.storage.generated.jooq.tables.SlfPmsXdsd.SLF_PMS_XDSD;
import static com.selfxdsd.storage.generated.jooq.tables.SlfProjectsXdsd.SLF_PROJECTS_XDSD;
import static com.selfxdsd.storage.generated.jooq.tables.SlfUsersXdsd.SLF_USERS_XDSD;

/**
 * All the Contracts in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class SelfContracts implements Contracts {

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
    public SelfContracts(
        final Storage storage,
        final Database database
    ) {
        this.storage = storage;
        this.database = database;
    }

    @Override
    public Contracts ofProject(
        final String repoFullName,
        final String repoProvider
    ) {
        return new ProjectContracts(
            repoFullName,
            repoProvider,
            () -> this.selectContracts()
                    .where(
                        SLF_CONTRACTS_XDSD.REPO_FULLNAME.eq(repoFullName)
                            .and(
                                SLF_CONTRACTS_XDSD.PROVIDER.eq(repoProvider)
                            )
                    ).fetch()
                    .stream()
                    .map(this::buildContract)
                    .collect(Collectors.toList()).stream(),
            this.storage);
    }

    @Override
    public Contracts ofContributor(final Contributor contributor) {
        return new ContributorContracts(
            contributor,
            () -> this.selectContracts()
                .where(SLF_CONTRACTS_XDSD.USERNAME.eq(
                    contributor.username()
                ).and(
                    SLF_CONTRACTS_XDSD.PROVIDER.eq(contributor.provider())))
                .fetch()
                .stream()
                .map(this::buildContract)
                .collect(Collectors.toList())
                .stream(),
            this.storage
        );
    }

    @Override
    public Contract addContract(
        final String repoFullName,
        final String contributorUsername,
        final String provider,
        final BigDecimal hourlyRate,
        final String role
    ) {
        final DSLContext jooq = this.database.jooq();
        final Project project = this.storage.projects()
            .getProjectById(repoFullName, provider);
        if (project == null) {
            throw new IllegalStateException("Can't attach the Contract to"
                + " project. The Project with " + repoFullName
                + " and " + provider + " was not found.");
        }
        final Contributor contributor = this.storage.contributors()
            .getById(contributorUsername, provider);
        if (contributor == null) {
            throw new IllegalStateException("Can't attach the Contract to"
                + " contributor. The Contributor with "
                + contributorUsername + " and " + provider
                + " was not found.");
        }
        final int execute = jooq
            .insertInto(SLF_CONTRACTS_XDSD,
                SLF_CONTRACTS_XDSD.REPO_FULLNAME,
                SLF_CONTRACTS_XDSD.USERNAME,
                SLF_CONTRACTS_XDSD.PROVIDER,
                SLF_CONTRACTS_XDSD.HOURLY_RATE,
                SLF_CONTRACTS_XDSD.ROLE)
            .values(repoFullName, contributorUsername,
                provider, hourlyRate.longValueExact(), role)
            .execute();
        if (execute != 1) {
            throw new IllegalStateException("Something went wrong when "
                + "inserting Contract into database.");
        }
        return new StoredContract(project,
            contributor,
            hourlyRate,
            role,
            this.storage);
    }

    @Override
    public Contract findById(final Contract.Id id) {
        final Result<Record> result = this.selectContracts()
            .where(
                SLF_CONTRACTS_XDSD.REPO_FULLNAME.eq(id.getRepoFullName()).and(
                    SLF_CONTRACTS_XDSD.PROVIDER.eq(id.getProvider()).and(
                        SLF_CONTRACTS_XDSD.USERNAME.eq(
                            id.getContributorUsername()
                        ).and(SLF_CONTRACTS_XDSD.ROLE.eq(id.getRole()))
                    )
                )
            )
            .fetch();
        if(!result.isEmpty()) {
            return this.buildContract(result.get(0));
        }
        return null;
    }

    @Override
    public Iterator<Contract> iterator() {
        final int maxRecords = this.database.jooq()
            .fetchCount(SLF_CONTRACTS_XDSD);
        return PagedIterator.create(
            100,
            maxRecords,
            (offset, size) -> this.selectContracts()
                .limit(size)
                .offset(offset)
                .fetch()
                .stream()
                .map(SelfContracts.this::buildContract)
                .collect(Collectors.toList())
        );
    }

    /**
     * Built the jooq SELECT/JOIN clause.
     * A Contract is linked to a Project and to a Contributor, so we select
     * contracts, JOINED with Projects (also with Users + PMs to have the
     * whole Project), and JOINED with Contributors.
     * @return JOOQ SELECT, to which we will apply the WHERE clause.
     * @checkstyle LineLength (100 lines)
     */
    private SelectOnConditionStep<Record> selectContracts(){
        return this.database.jooq()
            .select()
            .from(SLF_CONTRACTS_XDSD)
            .join(SLF_CONTRIBUTORS_XDSD)
            .on(
                SLF_CONTRACTS_XDSD.USERNAME.eq(
                    SLF_CONTRIBUTORS_XDSD.USERNAME
                ).and(
                    SLF_CONTRACTS_XDSD.PROVIDER.eq(
                        SLF_CONTRIBUTORS_XDSD.PROVIDER
                    )
                )
            )
            .join(SLF_PROJECTS_XDSD)
            .on(
                SLF_CONTRACTS_XDSD.REPO_FULLNAME.eq(
                    SLF_PROJECTS_XDSD.REPO_FULLNAME
                ).and(
                    SLF_CONTRACTS_XDSD.PROVIDER.eq(
                        SLF_PROJECTS_XDSD.PROVIDER
                    )
                )
            )
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
    }

    /**
     * Builds a Contract from a {@link Record}.
     * @param rec Record.
     * @return Contract.
     */
    private Contract buildContract(final Record rec){
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
                BigDecimal.valueOf(0),
                this.storage
            ),
            this.storage
        );
        final Contributor contributor = new StoredContributor(
            rec.getValue(SLF_CONTRIBUTORS_XDSD.USERNAME),
            rec.getValue(SLF_CONTRIBUTORS_XDSD.PROVIDER),
            this.storage
        );
        return new StoredContract(
            project,
            contributor,
            BigDecimal.valueOf(
                rec.getValue(SLF_CONTRACTS_XDSD.HOURLY_RATE)
            ),
            rec.getValue(SLF_CONTRACTS_XDSD.ROLE),
            this.storage);
    }
}
