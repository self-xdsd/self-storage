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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        final InvoicedTasks tasks = new SelfJooq(
            new H2Database()
        ).invoicedTasks();
        final InvoicedTasks ofInvoiceOne = tasks.ofInvoice(1);
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
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(project.repoFullName())
            .thenReturn("amihaiemil/docker-java-api");
        final Contributor john = Mockito.mock(Contributor.class);
        Mockito.when(john.username()).thenReturn("john");
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("456");

        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.project()).thenReturn(project);
        Mockito.when(task.assignee()).thenReturn(john);
        Mockito.when(task.issue()).thenReturn(issue);
        Mockito.when(task.value()).thenReturn(BigDecimal.valueOf(18500));
        Mockito.when(task.role()).thenReturn(Contract.Roles.DEV);
        Mockito.when(task.assignmentDate()).thenReturn(LocalDateTime.now());
        Mockito.when(task.deadline()).thenReturn(
            LocalDateTime.now().plusDays(10)
        );

        final InvoicedTasks tasks = new SelfJooq(
            new H2Database()
        ).invoicedTasks();
        final InvoicedTask invoiced = tasks.register(1, task);

        MatcherAssert.assertThat(invoiced.task(), Matchers.is(task));
        MatcherAssert.assertThat(
            invoiced.invoicedTaskId(),
            Matchers.greaterThan(1)
        );
        MatcherAssert.assertThat(
            invoiced.value(),
            Matchers.equalTo(BigDecimal.valueOf(18500))
        );
        MatcherAssert.assertThat(
            invoiced.invoice().invoiceId(),
            Matchers.equalTo(1)
        );
    }

}
