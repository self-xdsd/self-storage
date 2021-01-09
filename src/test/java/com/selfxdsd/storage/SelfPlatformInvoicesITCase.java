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
import com.selfxdsd.api.PlatformInvoice;
import com.selfxdsd.api.PlatformInvoices;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Iterator;

/**
 * Integration tests for {@link SelfPlatformInvoices}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.23
 */
public final class SelfPlatformInvoicesITCase {

    /**
     * Method getById can return the found PlatformInvoice.
     * Also, the found PlatformInvoice has no corresponding Invoice.
     */
    @Test
    public void getByIdReturnsFound() {
        final PlatformInvoices invoices = new SelfJooq(
            new H2Database()
        ).platformInvoices();
        final PlatformInvoice found = invoices.getById(1);

        MatcherAssert.assertThat(
            found,
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            found.invoice(),
            Matchers.nullValue()
        );
        MatcherAssert.assertThat(
            found.commission(),
            Matchers.equalTo(BigDecimal.valueOf(100))
        );
        MatcherAssert.assertThat(
            found.vat(),
            Matchers.equalTo(BigDecimal.valueOf(19))
        );
        MatcherAssert.assertThat(
            found.billedTo(),
            Matchers.equalTo("mihai")
        );
        MatcherAssert.assertThat(
            found.createdAt(),
            Matchers.equalTo(
                LocalDateTime.of(2021, Month.JANUARY, 9, 0, 0, 0)
            )
        );
        MatcherAssert.assertThat(
            found.paymentTime(),
            Matchers.equalTo(
                LocalDateTime.of(2021, Month.JANUARY, 9, 0, 0, 0)
            )
        );
        MatcherAssert.assertThat(
            found.transactionId(),
            Matchers.equalTo("transaction123")
        );
    }

    /**
     * Method getById returns null if the PlatformInvoice does not
     * exist.
     */
    @Test
    public void getByIdReturnsNullOnMissing() {
        final PlatformInvoices invoices = new SelfJooq(
            new H2Database()
        ).platformInvoices();
        final PlatformInvoice missing = invoices.getById(12345);

        MatcherAssert.assertThat(
            missing,
            Matchers.nullValue()
        );
    }

    /**
     * Method getByPayment can return the found PlatformInvoice.
     * Also, the found PlatformInvoice has no corresponding Invoice.
     */
    @Test
    public void getByPaymentReturnsFound() {
        final PlatformInvoices invoices = new SelfJooq(
            new H2Database()
        ).platformInvoices();
        final PlatformInvoice found = invoices.getByPayment(
            "transaction123",
            LocalDateTime.of(2021, Month.JANUARY, 9, 0, 0, 0)
        );

        MatcherAssert.assertThat(
            found,
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            found.invoice(),
            Matchers.nullValue()
        );
        MatcherAssert.assertThat(
            found.commission(),
            Matchers.equalTo(BigDecimal.valueOf(100))
        );
        MatcherAssert.assertThat(
            found.vat(),
            Matchers.equalTo(BigDecimal.valueOf(19))
        );
        MatcherAssert.assertThat(
            found.billedTo(),
            Matchers.equalTo("mihai")
        );
        MatcherAssert.assertThat(
            found.createdAt(),
            Matchers.equalTo(
                LocalDateTime.of(2021, Month.JANUARY, 9, 0, 0, 0)
            )
        );
        MatcherAssert.assertThat(
            found.paymentTime(),
            Matchers.equalTo(
                LocalDateTime.of(2021, Month.JANUARY, 9, 0, 0, 0)
            )
        );
        MatcherAssert.assertThat(
            found.transactionId(),
            Matchers.equalTo("transaction123")
        );
    }

    /**
     * Method getByPayment returns null if the PlatformInvoice does not
     * exist.
     */
    @Test
    public void getByPaymentReturnsNullOnMissing() {
        final PlatformInvoices invoices = new SelfJooq(
            new H2Database()
        ).platformInvoices();
        final PlatformInvoice missing = invoices.getByPayment(
            "transaction45678910",
            LocalDateTime.now()
        );

        MatcherAssert.assertThat(
            missing,
            Matchers.nullValue()
        );
    }

    /**
     * An existing PlatformInvoice can return its corresponding Invoice.
     */
    @Test
    public void hasCorrespondingInvoice() {
        final PlatformInvoices invoices = new SelfJooq(
            new H2Database()
        ).platformInvoices();
        final PlatformInvoice found = invoices.getById(2);
        final Invoice corresponding = found.invoice();

        MatcherAssert.assertThat(
            found.transactionId(),
            Matchers.equalTo(corresponding.transactionId())
        );
        MatcherAssert.assertThat(
            found.paymentTime(),
            Matchers.equalTo(corresponding.paymentTime())
        );
        MatcherAssert.assertThat(
            found.commission(),
            Matchers.equalTo(corresponding.commission())
        );
    }

    /**
     * SelfPlatformInvoices can be iterated.
     */
    @Test
    public void canBeIterated() {
        final PlatformInvoices invoices = new SelfJooq(
            new H2Database()
        ).platformInvoices();
        MatcherAssert.assertThat(
            invoices,
            Matchers.iterableWithSize(2)
        );
        final Iterator<PlatformInvoice> iterator = invoices.iterator();
        final PlatformInvoice first = iterator.next();
        final PlatformInvoice second = iterator.next();
        MatcherAssert.assertThat(
            first.id(),
            Matchers.equalTo(1)
        );
        MatcherAssert.assertThat(
            second.id(),
            Matchers.equalTo(2)
        );
    }
}
