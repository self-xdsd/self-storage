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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;

/**
 * Integration tests for {@link SelfPayments}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.67
 */
public final class SelfPaymentsITCase {

    /**
     * It can register a new Payment for an Invoice.
     */
    @Test
    public void registersNewPayment() {
        final Storage storage = new SelfJooq(new H2Database());
        final Invoice invoice = storage.invoices().getById(5);
        MatcherAssert.assertThat(
            invoice,
            Matchers.notNullValue()
        );
        final LocalDateTime timestamp = LocalDateTime.now();
        final Payment failed = storage.payments().register(
            invoice,
            "transaction123",
            timestamp,
            BigDecimal.TEN,
            Payment.Status.FAILED,
            "A failed Payment for test..."
        );
        MatcherAssert.assertThat(
            failed.invoice(),
            Matchers.is(invoice)
        );
        MatcherAssert.assertThat(
            failed.status(),
            Matchers.equalTo(Payment.Status.FAILED)
        );
        MatcherAssert.assertThat(
            failed.failReason(),
            Matchers.equalTo("A failed Payment for test...")
        );
        MatcherAssert.assertThat(
            failed.paymentTime(),
            Matchers.equalTo(timestamp)
        );
        MatcherAssert.assertThat(
            failed.transactionId(),
            Matchers.equalTo("transaction123")
        );
        MatcherAssert.assertThat(
            failed.value(),
            Matchers.equalTo(BigDecimal.TEN)
        );

    }

    /**
     * It can return the Payments of an Invoice.
     */
    @Test
    public void returnsPaymentsOfInvoice() {
        final Storage storage = new SelfJooq(new H2Database());
        final Invoice invoice = storage.invoices().getById(2);
        MatcherAssert.assertThat(
            invoice,
            Matchers.notNullValue()
        );

        final Payments ofInvoice = storage.payments().ofInvoice(invoice);

        MatcherAssert.assertThat(
            ofInvoice,
            Matchers.iterableWithSize(2)
        );

        final Iterator<Payment> payments = ofInvoice.iterator();
        final Payment first = payments.next();
        final Payment second = payments.next();

        MatcherAssert.assertThat(
            first.invoice(),
            Matchers.is(invoice)
        );
        MatcherAssert.assertThat(
            first.transactionId(),
            Matchers.equalTo("transaction123")
        );
        MatcherAssert.assertThat(
            first.paymentTime(),
            Matchers.equalTo(LocalDateTime.of(2021, 3, 1, 0, 0, 0))
        );
        MatcherAssert.assertThat(
            first.value(),
            Matchers.equalTo(BigDecimal.valueOf(1000))
        );
        MatcherAssert.assertThat(
            first.status(),
            Matchers.equalTo(Payment.Status.FAILED)
        );
        MatcherAssert.assertThat(
            first.failReason(),
            Matchers.equalTo("Failed Payment 1")
        );

        MatcherAssert.assertThat(
            second.invoice(),
            Matchers.is(invoice)
        );
        MatcherAssert.assertThat(
            second.transactionId(),
            Matchers.equalTo("transaction456")
        );
        MatcherAssert.assertThat(
            second.paymentTime(),
            Matchers.equalTo(LocalDateTime.of(2021, 3, 2, 0, 0, 0))
        );
        MatcherAssert.assertThat(
            second.value(),
            Matchers.equalTo(BigDecimal.valueOf(1000))
        );
        MatcherAssert.assertThat(
            second.status(),
            Matchers.equalTo(Payment.Status.FAILED)
        );
        MatcherAssert.assertThat(
            second.failReason(),
            Matchers.equalTo("Failed Payment 2")
        );
    }

}
