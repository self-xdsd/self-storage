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
import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.Contributors;
import com.selfxdsd.api.Task;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.contracts.ContributorContracts;
import com.selfxdsd.core.contracts.StoredContract;
import com.selfxdsd.core.contributors.ProjectContributors;
import com.selfxdsd.core.contributors.StoredContributor;
import org.jooq.Record;
import org.jooq.Result;

import java.math.BigDecimal;
import java.util.*;

import static com.selfxdsd.storage.generated.jooq.Tables.SLF_CONTRACTS_XDSD;
import static com.selfxdsd.storage.generated.jooq.Tables.SLF_CONTRIBUTORS_XDSD;

/**
 * All the contributors registered in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class SelfContributors implements Contributors {

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
    public SelfContributors(
        final Storage storage,
        final Database database
    ) {
        this.storage = storage;
        this.database = database;
    }

    @Override
    public Contributor register(
        final String username,
        final String provider
    ) {
        this.database.jooq().insertInto(
            SLF_CONTRIBUTORS_XDSD,
            SLF_CONTRIBUTORS_XDSD.USERNAME,
            SLF_CONTRIBUTORS_XDSD.PROVIDER
        ).values(
            username,
            provider
        ).execute();
        return new StoredContributor(username, provider, this.storage);
    }

    @Override
    public Contributor getById(
        final String username,
        final String provider
    ) {
        final Result<Record> result = this.database.jooq()
            .select()
            .from(SLF_CONTRIBUTORS_XDSD)
            .where(
                SLF_CONTRIBUTORS_XDSD.USERNAME.eq(username).and(
                    SLF_CONTRIBUTORS_XDSD.PROVIDER.eq(provider)
                )
            )
            .fetch();
        if (result.size() > 0) {
            final Record rec = result.get(0);
            return new StoredContributor(
                rec.getValue(SLF_CONTRIBUTORS_XDSD.USERNAME),
                rec.getValue(SLF_CONTRIBUTORS_XDSD.PROVIDER),
                this.storage
            );
        }
        return null;
    }

    @Override
    public Contributors ofProject(
        final String repoFullName,
        final String repoProvider
    ) {
        final Map<Contributor, List<Contract>> contributors = new HashMap<>();
        final Result<Record> result = this.database.jooq()
            .select()
            .from(SLF_CONTRIBUTORS_XDSD)
            .join(SLF_CONTRACTS_XDSD)
            .on(
                SLF_CONTRACTS_XDSD.USERNAME.eq(SLF_CONTRIBUTORS_XDSD.USERNAME)
                    .and(
                        SLF_CONTRACTS_XDSD.PROVIDER.eq(
                            SLF_CONTRIBUTORS_XDSD.PROVIDER
                        )
                    )
            )
            .where(
                SLF_CONTRACTS_XDSD.REPO_FULLNAME.eq(repoFullName).and(
                    SLF_CONTRACTS_XDSD.PROVIDER.eq(repoProvider)
                )
            )
            .fetch();
        boolean firstOccurence;
        for(final Record rec : result) {
            firstOccurence = true;
            final Contributor found = new StoredContributor(
                rec.getValue(SLF_CONTRIBUTORS_XDSD.USERNAME),
                rec.getValue(SLF_CONTRIBUTORS_XDSD.PROVIDER),
                this.storage
            );
            for(final Contributor key : contributors.keySet()) {
                if(key.username().equals(found.username())
                    && key.provider().equals(found.provider())){
                    final List<Contract> contracts = contributors.get(key);
                    contracts.add(
                        new StoredContract(
                            new Contract.Id(
                                rec.getValue(SLF_CONTRACTS_XDSD.REPO_FULLNAME),
                                rec.getValue(SLF_CONTRACTS_XDSD.USERNAME),
                                rec.getValue(SLF_CONTRACTS_XDSD.PROVIDER),
                                rec.getValue(SLF_CONTRACTS_XDSD.ROLE)
                            ),
                            BigDecimal.valueOf(
                                rec.getValue(SLF_CONTRACTS_XDSD.HOURLY_RATE)
                            ),
                            this.storage
                        )
                    );
                    firstOccurence = false;
                }
            }
            if(firstOccurence) {
                final List<Contract> contracts = new ArrayList<>();
                contracts.add(
                    new StoredContract(
                        new Contract.Id(
                            rec.getValue(SLF_CONTRACTS_XDSD.REPO_FULLNAME),
                            rec.getValue(SLF_CONTRACTS_XDSD.USERNAME),
                            rec.getValue(SLF_CONTRACTS_XDSD.PROVIDER),
                            rec.getValue(SLF_CONTRACTS_XDSD.ROLE)
                        ),
                        BigDecimal.valueOf(
                                rec.getValue(SLF_CONTRACTS_XDSD.HOURLY_RATE)
                        ),
                        this.storage
                    )
                );
                contributors.put(found, contracts);
            }
        }
        final List<Contributor> ofProject = new ArrayList<>();
        for(final Contributor key : contributors.keySet()) {
            final Contributor withContracts = new StoredContributor(
                key.username(),
                key.provider(),
                new ContributorContracts(
                    key, contributors.get(key), this.storage
                ),
                this.storage
            );
            ofProject.add(withContracts);
        }
        return new ProjectContributors(
            repoFullName, repoProvider, ofProject, this.storage
        );
    }

    @Override
    public Contributor elect(final Task task) {
        throw new UnsupportedOperationException(
            "You can only elect a Contributor out of a Project's contributors."
                + " Call #ofProject(...) first."
        );
    }

    @Override
    public Iterator<Contributor> iterator() {
        final List<Contributor> contributors = new ArrayList<>();
        final Result<Record> result = this.database.jooq()
            .select()
            .from(SLF_CONTRIBUTORS_XDSD)
            .limit(100)
            .fetch();
        for (final Record res : result) {
            contributors.add(
                new StoredContributor(
                    res.getValue(SLF_CONTRIBUTORS_XDSD.USERNAME),
                    res.getValue(SLF_CONTRIBUTORS_XDSD.PROVIDER),
                    this.storage
                )
            );
        }
        return contributors.iterator();
    }
}
