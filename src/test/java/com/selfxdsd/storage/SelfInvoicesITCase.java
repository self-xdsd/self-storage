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
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Integration tests for {@link SelfInvoices}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.4
 * @checkstyle JavaNCSS (500 lines)
 * @checkstyle MethodLength (500 lines)
 */
public final class SelfInvoicesITCase {

    /**
     * SelfInvoices can return an Invoice by its id. The Invoice should
     * also have its latest Payment.
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
        MatcherAssert.assertThat(
            found.eurToRon(),
            Matchers.greaterThanOrEqualTo(BigDecimal.valueOf(450))
        );
        MatcherAssert.assertThat(
            found.isPaid(),
            Matchers.is(Boolean.FALSE)
        );
        final Payment latest = found.latest();
        MatcherAssert.assertThat(
            latest.status(),
            Matchers.equalTo("FAILED")
        );
        MatcherAssert.assertThat(
            latest.failReason(),
            Matchers.equalTo("Failed Payment 2")
        );
    }

    /**
     * SelfInvoices can return an Invoice by its id. The Invoice has
     * no payments.
     */
    @Test
    public void returnsFoundInvoiceNoPayments() {
        final Invoices invoices = new SelfJooq(
            new H2Database()
        ).invoices();
        final Invoice found = invoices.getById(4);
        MatcherAssert.assertThat(
            found.invoiceId(),
            Matchers.equalTo(4)
        );
        MatcherAssert.assertThat(
            found.createdAt(),
            Matchers.lessThanOrEqualTo(LocalDateTime.now())
        );
        MatcherAssert.assertThat(
            found.contract().contractId(),
            Matchers.equalTo(
                new Contract.Id(
                    "vlad/test",
                    "alexandra",
                    "github",
                    "DEV"
                )
            )
        );
        MatcherAssert.assertThat(
            found.isPaid(),
            Matchers.is(Boolean.FALSE)
        );
        MatcherAssert.assertThat(
            found.latest(),
            Matchers.nullValue()
        );
        MatcherAssert.assertThat(
            found.payments(),
            Matchers.emptyIterable()
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
            created.latest(),
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
        MatcherAssert.assertThat(
            created.eurToRon(),
            Matchers.greaterThanOrEqualTo(BigDecimal.valueOf(450))
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
        final Invoices ofJohn = invoices.ofContract(id);
        MatcherAssert.assertThat(
            ofJohn,
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            ofJohn.iterator().next().latest().failReason(),
            Matchers.equalTo("Failed Payment 2")
        );
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
        invoices.registerAsPaid(
            unpaid,
            BigDecimal.valueOf(0),
            BigDecimal.valueOf(0)
        );
    }

    /**
     * SelfInvoices can register a paid Invoice (with a real wallet)
     * and also insert a PlatformInvoice.
     */
    @Test
    @Ignore
    public void registerAsPaidWorksForRealWallet() {
        final Invoices invoices = new SelfJooq(new H2Database()).invoices();
        final Invoice unpaid = invoices.getById(5);
        final LocalDateTime paymentTime = LocalDateTime.now();
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
            public Payment latest() {
                return new Payment() {
                    @Override
                    public Invoice invoice() {
                        return unpaid;
                    }

                    @Override
                    public PlatformInvoice platformInvoice() {
                        throw new UnsupportedOperationException("Not needed.");
                    }

                    @Override
                    public String transactionId() {
                        return "transaction12345";
                    }

                    @Override
                    public LocalDateTime paymentTime() {
                        return paymentTime;
                    }

                    @Override
                    public BigDecimal value() {
                        return unpaid.totalAmount();
                    }

                    @Override
                    public String status() {
                        return Status.SUCCESSFUL;
                    }

                    @Override
                    public String failReason() {
                        return "";
                    }
                };
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
            public String billedByCountry() {
                return unpaid.billedByCountry();
            }

            @Override
            public String billedToCountry() {
                return unpaid.billedToCountry();
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
            public BigDecimal eurToRon() {
                return unpaid.eurToRon();
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
            public Payments payments() {
                return unpaid.payments();
            }

            @Override
            public void toPdf(final OutputStream stream) throws IOException {
                unpaid.toPdf(stream);
            }
        };
        MatcherAssert.assertThat(
            invoices.registerAsPaid(
                paid,
                BigDecimal.valueOf(15),
                BigDecimal.valueOf(487)
            ),
            Matchers.nullValue()
        );

        final Invoice paidSelected = invoices.getById(5);
        MatcherAssert.assertThat(
            paidSelected.billedBy(),
            Matchers.equalTo("Contributor alexandra at github.")
        );
        MatcherAssert.assertThat(
            paidSelected.billedTo(),
            Matchers.equalTo("Project vlad/test at github.")
        );
        MatcherAssert.assertThat(
            paidSelected.billedToCountry(),
            Matchers.equalTo("")
        );
        MatcherAssert.assertThat(
            paidSelected.billedByCountry(),
            Matchers.equalTo("")
        );
        MatcherAssert.assertThat(
            paidSelected.eurToRon(),
            Matchers.greaterThanOrEqualTo(BigDecimal.valueOf(450))
        );
        final PlatformInvoice platformInvoice = paidSelected.platformInvoice();
        MatcherAssert.assertThat(
            platformInvoice.commission(),
            Matchers.equalTo(paidSelected.commission())
        );
        MatcherAssert.assertThat(
            platformInvoice.vat(),
            Matchers.equalTo(BigDecimal.valueOf(15))
        );
        MatcherAssert.assertThat(
            platformInvoice.billedTo(),
            Matchers.equalTo(paidSelected.billedBy())
        );
    }
}
