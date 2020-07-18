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

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.InvoicedTask;
import com.selfxdsd.api.InvoicedTasks;
import com.selfxdsd.api.Task;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.contracts.invoices.InvoiceTasks;
import com.selfxdsd.core.contracts.invoices.StoredInvoicedTask;
import com.selfxdsd.core.tasks.StoredTask;
import org.jooq.Record;
import org.jooq.Result;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.selfxdsd.storage.generated.jooq.tables.SlfInvoicedtasksXdsd.SLF_INVOICEDTASKS_XDSD;

/**
 * Invoiced tasks in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.4
 */
public final class SelfInvoicedTasks implements InvoicedTasks {

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
    public SelfInvoicedTasks(
        final Storage storage,
        final Database database
    ) {
        this.storage = storage;
        this.database = database;
    }


    @Override
    public InvoicedTasks ofInvoice(final int invoiceId) {
        return new InvoiceTasks(
            invoiceId,
            () -> {
                final Result<Record> results = database.jooq()
                    .select()
                    .from(SLF_INVOICEDTASKS_XDSD)
                    .where(SLF_INVOICEDTASKS_XDSD.INVOICEID.eq(invoiceId))
                    .fetch();
                final List<InvoicedTask> tasks = new ArrayList<>();
                for (final Record rec : results) {
                    tasks.add(invoicedTaskFromRecord(rec));
                }
                return tasks.stream();
            },
            this.storage
        );
    }

    @Override
    public InvoicedTask register(
        final int invoiceId,
        final Task finished
    ) {
        final Record inserted = this.database.jooq()
            .insertInto(
                SLF_INVOICEDTASKS_XDSD,
                SLF_INVOICEDTASKS_XDSD.INVOICEID,
                SLF_INVOICEDTASKS_XDSD.REPO_FULLNAME,
                SLF_INVOICEDTASKS_XDSD.USERNAME,
                SLF_INVOICEDTASKS_XDSD.PROVIDER,
                SLF_INVOICEDTASKS_XDSD.ROLE,
                SLF_INVOICEDTASKS_XDSD.VALUE.cast(BigDecimal.class).as("value"),
                SLF_INVOICEDTASKS_XDSD.ISSUEID,
                SLF_INVOICEDTASKS_XDSD.ASSIGNED,
                SLF_INVOICEDTASKS_XDSD.DEADLINE,
                SLF_INVOICEDTASKS_XDSD.INVOICED
            ).values(
                invoiceId,
                finished.project().repoFullName(),
                finished.assignee().username(),
                finished.project().provider(),
                finished.role(),
                finished.value(),
                finished.issue().issueId(),
                finished.assignmentDate(),
                finished.deadline(),
                LocalDateTime.now()
            )
            .returningResult(
                SLF_INVOICEDTASKS_XDSD.ID,
                SLF_INVOICEDTASKS_XDSD.VALUE
            )
            .fetchOne();
        return new StoredInvoicedTask(
            inserted.getValue(SLF_INVOICEDTASKS_XDSD.ID),
            invoiceId,
            BigDecimal.valueOf(
                inserted.getValue(SLF_INVOICEDTASKS_XDSD.VALUE)
            ),
            finished,
            this.storage
        );
    }

    @Override
    public Iterator<InvoicedTask> iterator() {
        throw new UnsupportedOperationException(
            "You cannot iterate over all invoiced tasks. "
          + "Call #ofInvoice(...) first."
        );
    }

    /**
     * Build an InvoicedTask from a DB Record.
     * @param rec Record.
     * @return InvoicedTask.
     * @todo #75:60min At the moment, the Contract is read for each
     *  InvoicedTask. Instead, we should take the Contract from the Invoice.
     *  This way, we will read the Contract only once, instead of n times.
     */
    private InvoicedTask invoicedTaskFromRecord(final Record rec) {
        final InvoicedTask task = new StoredInvoicedTask(
            rec.getValue(SLF_INVOICEDTASKS_XDSD.ID),
            rec.getValue(SLF_INVOICEDTASKS_XDSD.INVOICEID),
            BigDecimal.valueOf(
                rec.getValue(SLF_INVOICEDTASKS_XDSD.VALUE)
            ),
            new StoredTask(
                this.storage.contracts().findById(
                    new Contract.Id(
                        rec.getValue(SLF_INVOICEDTASKS_XDSD.REPO_FULLNAME),
                        rec.getValue(SLF_INVOICEDTASKS_XDSD.USERNAME),
                        rec.getValue(SLF_INVOICEDTASKS_XDSD.PROVIDER),
                        rec.getValue(SLF_INVOICEDTASKS_XDSD.ROLE)
                    )
                ),
                rec.getValue(SLF_INVOICEDTASKS_XDSD.ISSUEID),
                this.storage,
                rec.getValue(SLF_INVOICEDTASKS_XDSD.ASSIGNED),
                rec.getValue(SLF_INVOICEDTASKS_XDSD.DEADLINE),
                30
            ),
            this.storage
        );
        return task;
    }
}
