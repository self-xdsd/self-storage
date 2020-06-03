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

import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.Contributors;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.contributors.StoredContributor;
import org.jooq.Record;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        try (final Database connected = this.database.connect()) {
            connected.jooq().insertInto(
                SLF_CONTRIBUTORS_XDSD,
                SLF_CONTRIBUTORS_XDSD.USERNAME,
                SLF_CONTRIBUTORS_XDSD.PROVIDER
            ).values(
                username,
                provider
            ).execute();
        }
        return new StoredContributor(username, provider, this.storage);
    }

    @Override
    public Contributor getById(
        final String username,
        final String provider
    ) {
        try (final Database connected = this.database.connect()) {
            final Result<Record> result = connected.jooq()
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
        }
        return null;
    }

    @Override
    public Contributors ofProject(
        final String repoFullName,
        final String repoProvider
    ) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Iterator<Contributor> iterator() {
        final List<Contributor> contributors = new ArrayList<>();
        try (final Database connected = this.database.connect()) {
            final Result<Record> result = connected.jooq()
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
        }
        return contributors.iterator();
    }
}
