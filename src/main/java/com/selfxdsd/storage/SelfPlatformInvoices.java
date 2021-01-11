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

import com.selfxdsd.api.PlatformInvoice;
import com.selfxdsd.api.PlatformInvoices;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.BasePaged;
import com.selfxdsd.core.StoredPlatformInvoice;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;
import static com.selfxdsd.storage.generated.jooq.Tables.SLF_PLATFORMINVOICES_XDSD;

/**
 * All the PlatformInvoices in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.23
 */
public final class SelfPlatformInvoices
    extends BasePaged implements PlatformInvoices {

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
    public SelfPlatformInvoices(
        final Storage storage,
        final Database database
    ) {
        this(
            storage,
            database,
            Page.all()
        );
    }

    /**
     * Ctor for paging.
     * @param storage Storage.
     * @param database Database.
     * @param page Page we're on.
     * @checkstyle LineLength (20 lines)
     */
    private SelfPlatformInvoices(
        final Storage storage,
        final Database database,
        final Page page
    ) {
        super(page, () -> database.jooq().fetchCount(SLF_PLATFORMINVOICES_XDSD));
        this.storage = storage;
        this.database = database;
    }

    @Override
    public PlatformInvoice getById(final int id) {
        final Result<Record> result = this.database.jooq()
            .select()
            .from(SLF_PLATFORMINVOICES_XDSD)
            .where(SLF_PLATFORMINVOICES_XDSD.ID.eq(id))
            .fetch();
        final PlatformInvoice found;
        if(result.isNotEmpty()) {
            found = this.buildFromRecord(result.get(0));
        } else {
            found = null;
        }
        return found;
    }

    @Override
    public PlatformInvoice getByPayment(
        final String transactionId,
        final LocalDateTime paymentTime
    ) {
        final Result<Record> result = this.database.jooq()
            .select()
            .from(SLF_PLATFORMINVOICES_XDSD)
            .where(
                SLF_PLATFORMINVOICES_XDSD.TRANSACTIONID.eq(transactionId)
            ).and(
                SLF_PLATFORMINVOICES_XDSD.PAYMENT_TIMESTAMP.eq(paymentTime)
            ).fetch();
        final PlatformInvoice found;
        if(result.isNotEmpty()) {
            found = this.buildFromRecord(result.get(0));
        } else {
            found = null;
        }
        return found;
    }

    @Override
    public Iterator<PlatformInvoice> iterator() {
        final Page page = super.current();
        final DSLContext jooq = this.database.jooq();
        return jooq
            .select()
            .from(
                jooq.select()
                    .from(SLF_PLATFORMINVOICES_XDSD)
                    .limit(page.getSize())
                    .offset((page.getNumber() - 1) * page.getSize())
                    .asTable("platforminvoices_page")
            ).stream()
            .map(rec -> buildFromRecord(rec))
            .iterator();
    }

    /**
     * Build a PlatformInvoice from a JOOQ Record.
     * @param record JOOQ Record.
     * @return PlatformInvoice.
     */
    private PlatformInvoice buildFromRecord(final Record record) {
        final int correspondingId;
        final Integer corresponding = record.getValue(
            SLF_PLATFORMINVOICES_XDSD.INVOICEID
        );
        if(corresponding == null) {
            correspondingId = -1;
        } else {
            correspondingId = corresponding.intValue();
        }
        final PlatformInvoice found = new StoredPlatformInvoice(
            record.getValue(SLF_PLATFORMINVOICES_XDSD.ID),
            record.getValue(SLF_PLATFORMINVOICES_XDSD.CREATEDAT),
            record.getValue(SLF_PLATFORMINVOICES_XDSD.BILLEDTO),
            BigDecimal.valueOf(
                record.getValue(
                    SLF_PLATFORMINVOICES_XDSD.COMMISSION
                ).longValue()
            ),
            BigDecimal.valueOf(
                record.getValue(SLF_PLATFORMINVOICES_XDSD.VAT).longValue()
            ),
            record.getValue(SLF_PLATFORMINVOICES_XDSD.TRANSACTIONID),
            record.getValue(SLF_PLATFORMINVOICES_XDSD.PAYMENT_TIMESTAMP),
            correspondingId,
            BigDecimal.valueOf(
                record.getValue(SLF_PLATFORMINVOICES_XDSD.EURTORON).longValue()
            ),
            this.storage
        );
        return found;
    }
}
