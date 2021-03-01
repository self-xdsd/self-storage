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

import com.selfxdsd.api.PaymentMethod;
import com.selfxdsd.api.PaymentMethods;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.Wallet;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.projects.StripePaymentMethod;
import com.selfxdsd.core.projects.WalletPaymentMethods;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.selfxdsd.storage.generated.jooq.Tables.*;

/**
 * PaymentMethods (of project Wallets) in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.13
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
        final PaymentMethod registered;
        final List<String> allowed = Arrays.asList(
            Wallet.Type.STRIPE
        );
        final Project project = wallet.project();
        if(allowed.contains(wallet.type())) {
            final int inserted = this.database.jooq().insertInto(
                SLF_PAYMENTMETHODS_XDSD,
                SLF_PAYMENTMETHODS_XDSD.REPO_FULLNAME,
                SLF_PAYMENTMETHODS_XDSD.PROVIDER,
                SLF_PAYMENTMETHODS_XDSD.TYPE,
                SLF_PAYMENTMETHODS_XDSD.IDENTIFIER,
                SLF_PAYMENTMETHODS_XDSD.ACTIVE
            ).values(
                project.repoFullName(),
                project.provider(),
                wallet.type(),
                identifier,
                Boolean.FALSE
            ).execute();
            if(inserted != 1) {
                throw new IllegalStateException(
                    "Something went wrong while trying to register "
                    + "a new payment method."
                );
            } else {
                registered = new StripePaymentMethod(
                    this.storage,
                    identifier,
                    wallet,
                    Boolean.FALSE
                );
            }
        } else {
            throw new UnsupportedOperationException(
                "Only payment methods for Wallets of type "
                + allowed + " can be registered at the moment!"
            );
        }
        return registered;
    }

    @Override
    public boolean remove(final PaymentMethod paymentMethod) {
        if(paymentMethod.active()) {
            throw new IllegalArgumentException(
                "You cannot remove an active PaymentMethod!"
            );
        }
        final Wallet wallet = paymentMethod.wallet();
        final Project project = wallet.project();
        final int deleted = this.database.jooq()
            .deleteFrom(SLF_PAYMENTMETHODS_XDSD)
            .where(
                SLF_PAYMENTMETHODS_XDSD.REPO_FULLNAME.eq(
                    project.repoFullName()
                ).and(
                    SLF_PAYMENTMETHODS_XDSD.PROVIDER.eq(
                        project.provider()
                    ).and(
                        SLF_PAYMENTMETHODS_XDSD.TYPE.eq(wallet.type())
                    )
                ).and(
                    SLF_PAYMENTMETHODS_XDSD.IDENTIFIER.eq(
                        paymentMethod.identifier()
                    )
                )
            ).execute();
        return deleted == 1;
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
                new StripePaymentMethod(
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
        final Wallet wallet = paymentMethod.wallet();
        final Project project = wallet.project();
        final DSLContext jooq = this.database.jooq();
        jooq.transaction(
            (configuration) -> {
                jooq.update(SLF_PAYMENTMETHODS_XDSD)
                    .set(SLF_PAYMENTMETHODS_XDSD.ACTIVE, Boolean.FALSE)
                    .where(
                        SLF_PAYMENTMETHODS_XDSD.REPO_FULLNAME.eq(
                            project.repoFullName()
                        ).and(
                            SLF_PAYMENTMETHODS_XDSD.PROVIDER.eq(
                                project.provider()
                            ).and(
                                SLF_PAYMENTMETHODS_XDSD.TYPE.eq(wallet.type())
                            )
                        ).and(
                            SLF_PAYMENTMETHODS_XDSD.IDENTIFIER.notEqual(
                                paymentMethod.identifier()
                            )
                        )
                    ).execute();
                jooq.update(SLF_PAYMENTMETHODS_XDSD)
                    .set(SLF_PAYMENTMETHODS_XDSD.ACTIVE, Boolean.TRUE)
                    .where(
                        SLF_PAYMENTMETHODS_XDSD.REPO_FULLNAME.eq(
                            project.repoFullName()
                        ).and(
                            SLF_PAYMENTMETHODS_XDSD.PROVIDER.eq(
                                project.provider()
                            ).and(
                                SLF_PAYMENTMETHODS_XDSD.TYPE.eq(wallet.type())
                            )
                        ).and(
                            SLF_PAYMENTMETHODS_XDSD.IDENTIFIER.eq(
                                paymentMethod.identifier()
                            )
                        )
                    ).execute();
            }
        );
        return new PaymentMethod() {
            @Override
            public String identifier() {
                return paymentMethod.identifier();
            }

            @Override
            public Wallet wallet() {
                return paymentMethod.wallet();
            }

            @Override
            public boolean active() {
                return Boolean.TRUE;
            }

            @Override
            public PaymentMethod activate() {
                return paymentMethod.activate();
            }

            @Override
            public PaymentMethod deactivate() {
                return paymentMethod.deactivate();
            }

            @Override
            public JsonObject json() {
                return paymentMethod.json();
            }

            @Override
            public boolean remove() {
                return paymentMethod.remove();
            }
        };
    }

    @Override
    public PaymentMethod deactivate(final PaymentMethod paymentMethod) {
        final Wallet wallet = paymentMethod.wallet();
        final Project project = wallet.project();
        this.database.jooq().update(SLF_PAYMENTMETHODS_XDSD)
            .set(SLF_PAYMENTMETHODS_XDSD.ACTIVE, Boolean.FALSE)
            .where(
                SLF_PAYMENTMETHODS_XDSD.REPO_FULLNAME.eq(
                    project.repoFullName()
                ).and(
                    SLF_PAYMENTMETHODS_XDSD.PROVIDER.eq(
                        project.provider()
                    ).and(
                        SLF_PAYMENTMETHODS_XDSD.TYPE.eq(wallet.type())
                    )
                ).and(
                    SLF_PAYMENTMETHODS_XDSD.IDENTIFIER.eq(
                        paymentMethod.identifier()
                    )
                )
            ).execute();
        return new PaymentMethod() {
            @Override
            public String identifier() {
                return paymentMethod.identifier();
            }

            @Override
            public Wallet wallet() {
                return paymentMethod.wallet();
            }

            @Override
            public boolean active() {
                return Boolean.FALSE;
            }

            @Override
            public PaymentMethod activate() {
                return paymentMethod.activate();
            }

            @Override
            public PaymentMethod deactivate() {
                return paymentMethod.deactivate();
            }

            @Override
            public JsonObject json() {
                return paymentMethod.json();
            }

            @Override
            public boolean remove() {
                return paymentMethod.remove();
            }
        };
    }

    @Override
    public Iterator<PaymentMethod> iterator() {
        throw new UnsupportedOperationException(
            "You cannot iterate over all PaymentMethods in Self. "
            + "Call #ofWallet(...) first."
        );
    }
}
