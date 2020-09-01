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

import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jooq.DSLContext;
import org.junit.Test;
import org.mockito.Mockito;

import static com.selfxdsd.storage.generated.jooq.Tables.SLF_CONTRIBUTORS_XDSD;
import static com.selfxdsd.storage.generated.jooq.tables.SlfProjectsXdsd.SLF_PROJECTS_XDSD;

/**
 * Unit tests for {@link SelfJooq}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class SelfJooqTestCase {

    /**
     * SelfJooq can return the Users.
     */
    @Test
    public void returnsUsers() {
        final Storage storage = new SelfJooq(Mockito.mock(Database.class));
        MatcherAssert.assertThat(
            storage.users(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(SelfUsers.class)
            )
        );
    }

    /**
     * SelfJooq can return the Projects.
     */
    @Test
    public void returnsProjects() {
        final Database database = Mockito.mock(Database.class);
        final DSLContext jooq = Mockito.mock(DSLContext.class);
        Mockito.when(jooq.fetchCount(SLF_PROJECTS_XDSD))
            .thenReturn(10);
        Mockito.when(database.connect()).thenReturn(database);
        Mockito.when(database.jooq()).thenReturn(jooq);

        final Storage storage = new SelfJooq(database);
        MatcherAssert.assertThat(
            storage.projects(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(SelfProjects.class)
            )
        );
    }

    /**
     * SelfJooq can return the Contributors.
     */
    @Test
    public void returnsContributors() {
        final Database database = Mockito.mock(Database.class);
        final DSLContext jooq = Mockito.mock(DSLContext.class);
        Mockito.when(jooq.fetchCount(SLF_CONTRIBUTORS_XDSD))
            .thenReturn(Integer.MAX_VALUE);
        Mockito.when(database.connect()).thenReturn(database);
        Mockito.when(database.jooq()).thenReturn(jooq);
        final Storage storage = new SelfJooq(database);
        MatcherAssert.assertThat(
            storage.contributors(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(SelfContributors.class)
            )
        );
    }

    /**
     * SelfJooq can return the Contracts.
     */
    @Test
    public void returnsContracts() {
        final Storage storage = new SelfJooq(Mockito.mock(Database.class));
        MatcherAssert.assertThat(
            storage.contracts(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(SelfContracts.class)
            )
        );
    }

    /**
     * SelfJooq can return the Invoices.
     */
    @Test
    public void returnsInvoices() {
        final Storage storage = new SelfJooq(Mockito.mock(Database.class));
        MatcherAssert.assertThat(
            storage.invoices(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(SelfInvoices.class)
            )
        );
    }

    /**
     * SelfJooq can return the InvoicedTasks.
     */
    @Test
    public void returnsInvoicedTasks() {
        final Storage storage = new SelfJooq(Mockito.mock(Database.class));
        MatcherAssert.assertThat(
            storage.invoicedTasks(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(SelfInvoicedTasks.class)
            )
        );
    }

    /**
     * SelfJooq can return the Tasks.
     */
    @Test
    public void returnsTasks() {
        final Storage storage = new SelfJooq(Mockito.mock(Database.class));
        MatcherAssert.assertThat(
            storage.tasks(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(SelfTasks.class)
            )
        );
    }

    /**
     * SelfJooq can return the Resignations.
     */
    @Test
    public void returnsResignations() {
        final Storage storage = new SelfJooq(Mockito.mock(Database.class));
        MatcherAssert.assertThat(
            storage.resignations(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(SelfResignations.class)
            )
        );
    }

    /**
     * SelfJooq can return the Wallets.
     */
    @Test
    public void returnsWallets() {
        final Storage storage = new SelfJooq(Mockito.mock(Database.class));
        MatcherAssert.assertThat(
            storage.wallets(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(SelfWallets.class)
            )
        );
    }
}
