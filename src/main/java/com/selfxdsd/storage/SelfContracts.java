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
import com.selfxdsd.core.contracts.StoredContract;
import com.selfxdsd.core.contributors.StoredContributor;
import com.selfxdsd.core.managers.StoredProjectManager;
import com.selfxdsd.core.projects.StoredProject;
import org.jooq.Record;
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
 * @todo #32:30min Continue implementing and writing integration tests
 *  for the methods of SelfContracts.
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
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Contracts ofContributor(final Contributor contributor) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Contract addContract(
        final String repoFullName,
        final String contributorUsername,
        final String provider,
        final BigDecimal hourlyRate,
        final String role
    ) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Contract findById(final Contract.Id id) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Iterator<Contract> iterator() {
        final int maxRecords;
        try (final Database connected = this.database.connect()) {
            maxRecords = connected.jooq().fetchCount(SLF_CONTRACTS_XDSD);
        }
        return PagedIterator.create(
            100,
            maxRecords,
            (offset, size) -> {
                //@checkstyle LineLength (50 lines)
                try (final Database connected = SelfContracts.this.database.connect()) {
                    return connected.jooq()
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
                        )
                        .limit(size)
                        .offset(offset)
                        .fetch()
                        .stream()
                        .map(SelfContracts.this::buildContract)
                        .collect(Collectors.toList());
                }
            }
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
                rec.getValue(SLF_USERS_XDSD.PROVIDER),
                this.storage
            ),
            rec.getValue(SLF_PROJECTS_XDSD.REPO_FULLNAME),
            rec.getValue(SLF_PROJECTS_XDSD.WEBHOOK_TOKEN),
            new StoredProjectManager(
                rec.getValue(SLF_PMS_XDSD.ID),
                rec.getValue(SLF_PMS_XDSD.USERNAME),
                rec.getValue(SLF_PMS_XDSD.PROVIDER),
                rec.getValue(SLF_PMS_XDSD.ACCESS_TOKEN),
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
