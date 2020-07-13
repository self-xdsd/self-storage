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
import com.selfxdsd.api.Invoice;
import com.selfxdsd.api.Invoices;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

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
            found.contractId(),
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

}
