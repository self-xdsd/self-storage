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

import com.selfxdsd.api.Invoice;
import com.selfxdsd.api.Payment;
import com.selfxdsd.api.Payments;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.contracts.invoices.InvoicePayments;
import com.selfxdsd.core.contracts.invoices.StoredPayment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;

import static com.selfxdsd.storage.generated.jooq.Tables.SLF_PAYMENTS_XDSD;

/**
 * All the Payments in Self XDSD.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.67
 */
public final class SelfPayments implements Payments {

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
    public SelfPayments(
        final Storage storage,
        final Database database
    ) {
        this.storage = storage;
        this.database = database;
    }

    @Override
    public Payment register(
        final Invoice invoice,
        final String transactionId,
        final LocalDateTime timestamp,
        final BigDecimal value,
        final String status,
        final String failReason
    ) {
        final int inserted = this.database.jooq().insertInto(
            SLF_PAYMENTS_XDSD,
            SLF_PAYMENTS_XDSD.INVOICEID,
            SLF_PAYMENTS_XDSD.TRANSACTIONID,
            SLF_PAYMENTS_XDSD.PAYMENT_TIMESTAMP,
            SLF_PAYMENTS_XDSD.VALUE,
            SLF_PAYMENTS_XDSD.STATUS,
            SLF_PAYMENTS_XDSD.FAILREASON
        ).values(
            invoice.invoiceId(),
            transactionId,
            timestamp,
            value.toBigIntegerExact(),
            status,
            failReason
        ).execute();
        if(inserted != 1) {
            throw new IllegalStateException(
                "Something went wrong while trying to register "
                + "a Payment for Invoice " + invoice.invoiceId() + ". "
            );
        } else {
            return new StoredPayment(
                invoice,
                transactionId,
                timestamp,
                value,
                status,
                failReason,
                this.storage
            );
        }
    }

    @Override
    public Payments ofInvoice(final Invoice invoice) {
        return new InvoicePayments(
            invoice,
            () -> SelfPayments.this.database.jooq()
                .select()
                .from(SLF_PAYMENTS_XDSD)
                .where(SLF_PAYMENTS_XDSD.INVOICEID.eq(invoice.invoiceId()))
                .stream()
                .map(
                    rec -> new StoredPayment(
                        invoice,
                        rec.getValue(SLF_PAYMENTS_XDSD.TRANSACTIONID),
                        rec.getValue(SLF_PAYMENTS_XDSD.PAYMENT_TIMESTAMP),
                        BigDecimal.valueOf(
                            rec.getValue(SLF_PAYMENTS_XDSD.VALUE).longValue()
                        ),
                        rec.getValue(SLF_PAYMENTS_XDSD.STATUS),
                        rec.get(SLF_PAYMENTS_XDSD.FAILREASON),
                        this.storage
                    )
                ),
            this.storage
        );
    }

    @Override
    public Iterator<Payment> iterator() {
        throw new UnsupportedOperationException(
            "You cannot iterate over all the Payments in Self!"
        );
    }

}
