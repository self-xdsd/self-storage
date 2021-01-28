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
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Integration tests for {@link SelfInvoicedTasks}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.4
 */
public final class SelfInvoicedTasksITCase {

    /**
     * We should not be able to iterate over all the
     * invoiced tasks in Self.
     */
    @Test (expected = UnsupportedOperationException.class)
    public void cannotIterate() {
        final InvoicedTasks tasks = new SelfJooq(
            new H2Database()
        ).invoicedTasks();
        tasks.iterator();
    }

    /**
     * SelfInvoicedTasks should return en existing Invoice's tasks.
     */
    @Test
    public void returnsTasksOfInvoice() {
        final Storage storage = new SelfJooq(new H2Database());
        final Invoices invoices = storage.invoices();
        final InvoicedTasks tasks = storage.invoicedTasks();

        final InvoicedTasks ofInvoiceOne = tasks.ofInvoice(
            invoices.getById(1)
        );
        for(final InvoicedTask invoiced : ofInvoiceOne) {
            final Task task = invoiced.task();
            MatcherAssert.assertThat(
                invoiced.invoice().invoiceId(),
                Matchers.equalTo(1)
            );
            MatcherAssert.assertThat(
                task.project().repoFullName(),
                Matchers.equalTo("amihaiemil/docker-java-api")
            );
            MatcherAssert.assertThat(
                task.project().provider(),
                Matchers.equalTo(Provider.Names.GITHUB)
            );
            MatcherAssert.assertThat(
                task.assignee().username(),
                Matchers.equalTo("john")
            );
            MatcherAssert.assertThat(
                task.role(),
                Matchers.equalTo(Contract.Roles.DEV)
            );
        }
    }

    /**
     * SelfInvoicedTask can register/Invoice a task.
     * @checkstyle ExecutableStatementCount (100 lines)
     */
    @Test
    public void registersTask() {
        final Storage storage = new SelfJooq(new H2Database());
        final Task toInvoice = storage.tasks().getById(
            "126", "amihaiemil/docker-java-api",
            Provider.Names.GITHUB, Boolean.FALSE
        );
        MatcherAssert.assertThat(
            toInvoice,
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            toInvoice.assignee().username(),
            Matchers.equalTo("alexandra")
        );
        MatcherAssert.assertThat(
            toInvoice.estimation(),
            Matchers.equalTo(60)
        );
        final InvoicedTasks tasks = new SelfJooq(
            new H2Database()
        ).invoicedTasks();
        final InvoicedTask invoiced = tasks.register(
            toInvoice.contract().invoices().active(),
            toInvoice,
            BigDecimal.valueOf(100)
        );

        MatcherAssert.assertThat(invoiced.task(), Matchers.is(toInvoice));
        MatcherAssert.assertThat(
            invoiced.invoicedTaskId(),
            Matchers.greaterThanOrEqualTo(1)
        );
        MatcherAssert.assertThat(
            invoiced.task().isPullRequest(),
            Matchers.is(Boolean.FALSE)
        );
        MatcherAssert.assertThat(
            invoiced.value(),
            Matchers.equalTo(BigDecimal.valueOf(15000))
        );
        MatcherAssert.assertThat(
            invoiced.value(),
            Matchers.equalTo(BigDecimal.valueOf(15000))
        );
        MatcherAssert.assertThat(
            invoiced.commission(),
            Matchers.equalTo(BigDecimal.valueOf(100))
        );
        MatcherAssert.assertThat(
            invoiced.totalAmount(),
            Matchers.equalTo(BigDecimal.valueOf(15100))
        );
        MatcherAssert.assertThat(
            invoiced.task().estimation(),
            Matchers.equalTo(60)
        );
        MatcherAssert.assertThat(
            invoiced.invoice().invoiceId(),
            Matchers.greaterThanOrEqualTo(1)
        );
        final Task removed = storage.tasks().getById(
            "126", "amihaiemil/docker-jaba-api",
            Provider.Names.GITHUB, Boolean.FALSE
        );
        MatcherAssert.assertThat(
            removed,
            Matchers.nullValue()
        );
    }

}
