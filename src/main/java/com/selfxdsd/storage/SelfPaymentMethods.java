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

import com.selfxdsd.api.PaymentMethod;
import com.selfxdsd.api.PaymentMethods;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.Wallet;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.projects.StoredPaymentMethod;
import com.selfxdsd.core.projects.WalletPaymentMethods;
import org.jooq.Record;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.selfxdsd.storage.generated.jooq.Tables.SLF_PAYMENTMETHODS_XDSD;
import static com.selfxdsd.storage.generated.jooq.Tables.SLF_WALLETS_XDSD;

/**
 * PaymentMethods (of project Wallets) in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.13
 * @todo #163:30min Continue implementing the methods of this class.
 *  Don't forget about integration tests.
 */
public final class SelfPaymentMethods implements PaymentMethods {

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
    public SelfPaymentMethods(
        final Storage storage,
        final Database database
    ) {
        this.storage = storage;
        this.database = database;
    }

    @Override
    public PaymentMethod register(
        final Wallet wallet,
        final String identifier
    ) {
        return null;
    }

    @Override
    public boolean remove(final PaymentMethod paymentMethod) {
        return false;
    }

    @Override
    public PaymentMethods ofWallet(final Wallet wallet) {
        final List<PaymentMethod> ofWallet = new ArrayList<>();
        final Project project = wallet.project();
        final Result<Record> result = this.database
            .jooq()
            .select()
            .from(SLF_PAYMENTMETHODS_XDSD)
            .where(
                SLF_PAYMENTMETHODS_XDSD.PROVIDER.eq(project.provider()).and(
                    SLF_PAYMENTMETHODS_XDSD.REPO_FULLNAME.eq(
                        project.repoFullName()
                    ).and(SLF_PAYMENTMETHODS_XDSD.TYPE.eq(wallet.type()))
                )
            ).fetch();
        for(final Record rec : result) {
            ofWallet.add(
                new StoredPaymentMethod(
                    this.storage,
                    rec.getValue(SLF_PAYMENTMETHODS_XDSD.IDENTIFIER),
                    wallet,
                    rec.getValue(SLF_WALLETS_XDSD.ACTIVE)
                )
            );
        }
        return new WalletPaymentMethods(
            wallet,
            () -> ofWallet.stream(),
            this.storage
        );
    }

    @Override
    public PaymentMethod active() {
        throw new UnsupportedOperationException(
            "You cannot get the active PaymentMethod out of all of them. "
            + "Call #ofWallet(...) first."
        );
    }

    @Override
    public PaymentMethod activate(final PaymentMethod paymentMethod) {
        return null;
    }

    @Override
    public Iterator<PaymentMethod> iterator() {
        throw new UnsupportedOperationException(
            "You cannot iterate over all PaymentMethods in Self. "
            + "Call #ofWallet(...) first."
        );
    }
}
