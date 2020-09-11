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
import com.selfxdsd.core.contributors.ContributorPayoutMethods;
import com.selfxdsd.core.contributors.StripePayoutMethod;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.Record;
import org.jooq.Result;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static com.selfxdsd.storage.generated.jooq.Tables.SLF_PAYOUTMETHODS_XDSD;

/**
 * All the PayoutMethods in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.9
 */
public final class SelfPayoutMethods implements PayoutMethods {

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
    public SelfPayoutMethods(
        final Storage storage,
        final Database database
    ) {
        this.storage = storage;
        this.database = database;
    }

    @Override
    public PayoutMethod register(
        final Contributor contributor,
        final String type,
        final String identifier
    ) {
        if(PayoutMethod.Type.STRIPE.equalsIgnoreCase(type)) {
            final InsertOnDuplicateStep<?> insert = this.database.jooq()
                .insertInto(
                    SLF_PAYOUTMETHODS_XDSD,
                    SLF_PAYOUTMETHODS_XDSD.USERNAME,
                    SLF_PAYOUTMETHODS_XDSD.PROVIDER,
                    SLF_PAYOUTMETHODS_XDSD.TYPE,
                    SLF_PAYOUTMETHODS_XDSD.ACTIVE,
                    SLF_PAYOUTMETHODS_XDSD.IDENTIFIER
                ).values(
                    contributor.username(),
                    contributor.provider(),
                    type,
                    Boolean.FALSE,
                    identifier
                );
            if (this.database.dbms().equals(Database.Dbms.MY_SQL)) {
                insert.onDuplicateKeyIgnore().execute();
            } else {
                insert.execute();
            }
            return new StripePayoutMethod(
                contributor,
                identifier,
                Boolean.FALSE
            );
        } else {
            throw new UnsupportedOperationException(
                "Only Stripe payout methods are supported at the moment."
            );
        }
    }

    @Override
    public PayoutMethods ofContributor(
        final Contributor contributor
    ) {
        final List<PayoutMethod> ofCountributor = new ArrayList<>();
        final Result<Record> result = this.database
            .jooq()
            .select()
            .from(SLF_PAYOUTMETHODS_XDSD)
            .where(
                SLF_PAYOUTMETHODS_XDSD.USERNAME.eq(contributor.username()).and(
                    SLF_PAYOUTMETHODS_XDSD.PROVIDER.eq(contributor.provider())
                )
            ).fetch();
        for(final Record rec : result) {
            ofCountributor.add(this.payoutMethodFromRecord(contributor, rec));
        }
        return new ContributorPayoutMethods(
            contributor, ofCountributor, this.storage
        );
    }

    @Override
    public PayoutMethod active() {
        throw new UnsupportedOperationException(
            "You cannot get the active PayoutMethod "
            + "out of all PayoutMethods in Self. "
            + "Call #ofContributor(...) first."
        );
    }

    @Override
    public PayoutMethod activate(final PayoutMethod payoutMethod) {
        return null;
    }

    @Override
    public Iterator<PayoutMethod> iterator() {
        throw new UnsupportedOperationException(
            "You cannot iterate over all PayoutMethods in Self. "
            + "Call #ofContributor(...) first."
        );
    }

    /**
     * Build a PayoutMethod from a JOOQ record.
     * @param contributor Contributor owning the PayoutMethod.
     * @param record Record.
     * @return Wallet.
     */
    private PayoutMethod payoutMethodFromRecord(
        final Contributor contributor, final Record record
    ) {
        final String type = record.getValue(SLF_PAYOUTMETHODS_XDSD.TYPE);
        if(type.equalsIgnoreCase(Wallet.Type.STRIPE)) {
            return new StripePayoutMethod(
                contributor,
                record.getValue(SLF_PAYOUTMETHODS_XDSD.IDENTIFIER),
                record.getValue(SLF_PAYOUTMETHODS_XDSD.ACTIVE)
            );
        } else {
            throw new UnsupportedOperationException(
                "Only Stripe payout methods are supported so far."
            );
        }
    }
}
