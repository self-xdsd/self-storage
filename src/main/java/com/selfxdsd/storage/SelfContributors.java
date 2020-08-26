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
 * @checkstyle ExecutableStatementCount (500 lines)
 */
public final class SelfContributors extends BasePaged implements Contributors {

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
        this(storage, database, Page.all());
    }

    /**
     * Ctor.
     * @param storage Parent Storage.
     * @param database Database.
     * @param page Current Page.
     */
    private SelfContributors(final Storage storage,
                             final Database database,
                             final Page page){
        super(page, () -> database.jooq().fetchCount(SLF_CONTRIBUTORS_XDSD));
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
        final Page page = super.current();
        final Record rec;
        if (page.getSize() == Integer.MAX_VALUE) {
            //we are in "all" page, it's safe to query whole table.
            rec = this.database.jooq()
                .select()
                .from(SLF_CONTRIBUTORS_XDSD)
                .where(
                    SLF_CONTRIBUTORS_XDSD.USERNAME.eq(username).and(
                        SLF_CONTRIBUTORS_XDSD.PROVIDER.eq(provider)
                    )
                )
                .stream()
                .findFirst()
                .orElse(null);
        } else {
            //we "extract" the page from table than we search the contributor
            //on that page using streams.
            rec = this.database.jooq()
                .select()
                .from(SLF_CONTRIBUTORS_XDSD)
                .limit(page.getSize())
                .offset((page.getNumber() - 1) * page.getSize())
                .stream()
                .filter(r -> r.getValue(SLF_CONTRIBUTORS_XDSD.USERNAME)
                    .equals(username) && r
                    .getValue(SLF_CONTRIBUTORS_XDSD.PROVIDER).equals(provider))
                .findFirst()
                .orElse(null);
        }
        if (rec != null) {
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
        final Project project = this.storage.projects()
                .getProjectById(repoFullName, repoProvider);
        if (project == null) {
            return new EmptyContributors(this);
        }
        final Page page = super.current();
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
            .limit(page.getSize())
            .offset((page.getNumber()  - 1) * page.getSize())
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
                    key, () -> contributors.get(key).stream(), this.storage
                ),
                this.storage
            );
            ofProject.add(withContracts);
        }
        return new ProjectContributors(project, ofProject::stream,
                this.storage);
    }

    @Override
    public Contributors page(final Page page) {
        return new SelfContributors(this.storage, this.database, page);
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
        final Page page = super.current();
        return this.database.jooq()
            .select()
            .from(SLF_CONTRIBUTORS_XDSD)
            .limit(page.getSize())
            .offset((page.getNumber() - 1) * page.getSize())
            .stream()
            .map(rec -> (Contributor) new StoredContributor(
                rec.getValue(SLF_CONTRIBUTORS_XDSD.USERNAME),
                rec.getValue(SLF_CONTRIBUTORS_XDSD.PROVIDER),
                this.storage
            ))
            .iterator();
    }

    /**
     * Empty representation of Contributors.
     */
    static final class EmptyContributors extends BasePaged
            implements Contributors {

        /**
         * Contributors delegate used just for register a new Contributor.
         */
        private final Contributors contributors;

        /**
         * Ctor.
         * @param contributors Contributors delegate.
         */
        private EmptyContributors(final Contributors contributors) {
            this(contributors, Page.all());
        }

        /**
         * Ctor.
         * @param contributors Contributors delegate.
         * @param page Current Page.
         */
        private EmptyContributors(final Contributors contributors,
                                  final Page page) {
            super(page, () -> 0);
            this.contributors = contributors;
        }

        @Override
        public Contributor register(final String username,
                                    final String provider) {
            return this.contributors.register(username, provider);
        }

        @Override
        public Contributor getById(final String username,
                                   final String provider) {
            return null;
        }

        @Override
        public Contributors ofProject(final String repoFullName,
                                      final String repoProvider) {
            return this;
        }

        @Override
        public Contributors page(final Page page) {
            return new EmptyContributors(this.contributors, page);
        }

        @Override
        public Contributor elect(final Task task) {
            return null;
        }

        @Override
        public Iterator<Contributor> iterator() {
            return Collections.emptyIterator();
        }

    }
}
