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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Integration tests for {@link SelfInvoices}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.4
 */
public final class SelfInvoicesITCase {

    /**
     * SelfInvoices can return an Invoice by its id.
     */
    @Test
    public void returnsFoundInvoice() {
        final Invoices invoices = new SelfJooq(
            new H2Database()
        ).invoices();
        final Invoice found = invoices.getById(1);
        MatcherAssert.assertThat(
            found.invoiceId(),
            Matchers.equalTo(1)
        );
        MatcherAssert.assertThat(
            found.createdAt(),
            Matchers.lessThanOrEqualTo(LocalDateTime.now())
        );
        MatcherAssert.assertThat(
            found.billedBy(),
            Matchers.equalTo("Contributor john at github.")
        );
        MatcherAssert.assertThat(
            found.contract().contractId(),
            Matchers.equalTo(
                new Contract.Id(
                    "amihaiemil/docker-java-api",
                    "john",
                    "github",
                    "DEV"
                )
            )
        );
    }

    /**
     * SelfInvoices can return null if an Invoice is not found.
     */
    @Test
    public void returnsNullForMissing() {
        final Invoices invoices = new SelfJooq(
            new H2Database()
        ).invoices();
        final Invoice found = invoices.getById(1003);
        MatcherAssert.assertThat(
            found, Matchers.nullValue()
        );
    }

    /**
     * SelfInvoices shouldn't be able to create a new invoice on a missing
     * Contract.
     */
    @Test (expected = IllegalStateException.class)
    public void cannotCreateNewInvoiceOnMissingContract() {
        final Invoices invoices = new SelfJooq(
            new H2Database()
        ).invoices();
        invoices.createNewInvoice(
            new Contract.Id(
                "john/missing",
                "mihai",
                "github",
                "QA"
            )
        );
    }

    /**
     * SelfInvoices should be able to create a new Invoice on
     * an existing Contract.
     */
    @Test
    public void createsNewInvoice() {
        final Invoices invoices = new SelfJooq(
            new H2Database()
        ).invoices();
        final Contract.Id contractId = new Contract.Id(
            "amihaiemil/docker-java-api",
            "alexandra",
            "github",
            "DEV"
        );
        final Invoice created = invoices.createNewInvoice(contractId);
        MatcherAssert.assertThat(
            created.invoiceId(),
            Matchers.greaterThan(2)
        );
        MatcherAssert.assertThat(
            created.contract().contractId(),
            Matchers.equalTo(contractId)
        );
        MatcherAssert.assertThat(
            created.createdAt(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            created.paymentTime(),
            Matchers.nullValue()
        );
        MatcherAssert.assertThat(
            created.transactionId(),
            Matchers.nullValue()
        );
        MatcherAssert.assertThat(
            created.tasks(),
            Matchers.emptyIterable()
        );
        MatcherAssert.assertThat(
            created.totalAmount(),
            Matchers.equalTo(BigDecimal.valueOf(0))
        );
    }

    /**
     * SelfInvoices shouldn't be able iterate.
     */
    @Test (expected = UnsupportedOperationException.class)
    public void cannotIterate() {
        final Invoices invoices = new SelfJooq(
            new H2Database()
        ).invoices();
        invoices.iterator();
    }

    /**
     * SelfInvoices shouldn't be able to return any active Invoice.
     */
    @Test (expected = UnsupportedOperationException.class)
    public void cannotReturnActive() {
        final Invoices invoices = new SelfJooq(
            new H2Database()
        ).invoices();
        invoices.active();
    }

    /**
     * SelfInvoices can return invoices of a Contract.Id.
     */
    @Test
    public void returnsInvoicesOfContract() {
        final Invoices invoices = new SelfJooq(new H2Database()).invoices();
        final Contract.Id id = new Contract.Id(
            "amihaiemil/docker-java-api",
            "john",
            "github",
            "DEV"
        );
        final Iterable<Invoice> ofContract = () -> invoices.ofContract(id)
            .iterator();
        MatcherAssert.assertThat(ofContract, Matchers.iterableWithSize(1));
    }

    /**
     * SelfInvoices returns empty iterable if there are not invoices for
     * a contract id.
     */
    @Test
    public void returnsEmptyIfInvoicesOfContractNotFound() {
        final Invoices invoices = new SelfJooq(new H2Database()).invoices();
        final Contract.Id id = new Contract.Id(
            "vlad/test",
            "maria",
            "github",
            "DEV"
        );
        final Iterable<Invoice> ofContract = () -> invoices.ofContract(id)
            .iterator();
        MatcherAssert.assertThat(ofContract, Matchers.emptyIterable());
    }

    /**
     * SelfInvoices shouldn't mark as paid an Invoice which is not actually
     * paid.
     */
    @Test (expected = IllegalArgumentException.class)
    public void registerAsPaidRejectsUnpaidInvoice() {
        final Invoices invoices = new SelfJooq(new H2Database()).invoices();
        final Invoice unpaid = invoices.getById(1);
        invoices.registerAsPaid(unpaid, BigDecimal.valueOf(0));
    }

    /**
     * SelfInvoices can register a paid Invoice.
     */
    @Test
    public void registerAsPaidWorks() {
        final Invoices invoices = new SelfJooq(new H2Database()).invoices();
        final Invoice unpaid = invoices.getById(4);
        final Invoice paid = new Invoice() {
            @Override
            public int invoiceId() {
                return unpaid.invoiceId();
            }

            @Override
            public InvoicedTask register(
                final Task task,
                final BigDecimal commission
            ) {
                throw new IllegalStateException(
                    "Can't register a new Task on paid Invoice!"
                );
            }

            @Override
            public Contract contract() {
                return unpaid.contract();
            }

            @Override
            public LocalDateTime createdAt() {
                return unpaid.createdAt();
            }

            @Override
            public LocalDateTime paymentTime() {
                return LocalDateTime.now();
            }

            @Override
            public String transactionId() {
                return "transaction12345";
            }

            @Override
            public String billedBy() {
                return unpaid.billedBy();
            }

            @Override
            public String billedTo() {
                return unpaid.billedTo();
            }

            @Override
            public InvoicedTasks tasks() {
                return unpaid.tasks();
            }

            @Override
            public BigDecimal totalAmount() {
                return unpaid.totalAmount();
            }

            @Override
            public BigDecimal amount() {
                return unpaid.amount();
            }

            @Override
            public BigDecimal commission() {
                return unpaid.commission();
            }

            @Override
            public boolean isPaid() {
                return true;
            }

            @Override
            public PlatformInvoice platformInvoice() {
                return unpaid.platformInvoice();
            }

            @Override
            public File toPdf() throws IOException {
                return unpaid.toPdf();
            }
        };
        MatcherAssert.assertThat(
            invoices.registerAsPaid(paid, BigDecimal.valueOf(0)),
            Matchers.is(Boolean.TRUE)
        );

        final Invoice paidSelected = invoices.getById(4);
        MatcherAssert.assertThat(
            paidSelected.billedBy(),
            Matchers.equalTo("Contributor alexandra at github.")
        );
        MatcherAssert.assertThat(
            paidSelected.billedTo(),
            Matchers.equalTo("Project vlad/test at github.")
        );
    }
}
