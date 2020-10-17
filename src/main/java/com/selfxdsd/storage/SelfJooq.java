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

/**
 * Self Storage implemented with jOOQ.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class SelfJooq implements Storage {

    /**
     * The Database we're working with.
     */
    private Database database;

    /**
     * Constructor. Working with MySql by default.
     */
    public SelfJooq() {
        this(
            new MySql(
                "DB URL",
                "user",
                "pwd"
            )
        );
    }

    /**
     * Ctor.
     * @param database Database.
     */
    public SelfJooq(final Database database) {
        this.database = database.connect();
    }

    @Override
    public Users users() {
        return new SelfUsers(this, this.database);
    }

    @Override
    public ProjectManagers projectManagers() {
        return new SelfPms(this, this.database);
    }

    @Override
    public Projects projects() {
        return new SelfProjects(this, this.database);
    }

    @Override
    public Wallets wallets() {
        return new SelfWallets(this, this.database);
    }

    @Override
    public Contracts contracts() {
        return new SelfContracts(this, this.database);
    }

    @Override
    public Invoices invoices() {
        return new SelfInvoices(this, this.database);
    }

    @Override
    public InvoicedTasks invoicedTasks() {
        return new SelfInvoicedTasks(this, this.database);
    }

    @Override
    public Contributors contributors() {
        return new SelfContributors(this, this.database);
    }

    @Override
    public Tasks tasks() {
        return new SelfTasks(this, this.database);
    }

    @Override
    public Resignations resignations() {
        return new SelfResignations(this, this.database);
    }

    @Override
    public PayoutMethods payoutMethods() {
        return new SelfPayoutMethods(this, this.database);
    }

    @Override
    public PaymentMethods paymentMethods() {
        return new SelfPaymentMethods(this, this.database);
    }

    @Override
    public void close() {
        this.database.close();
    }
}
