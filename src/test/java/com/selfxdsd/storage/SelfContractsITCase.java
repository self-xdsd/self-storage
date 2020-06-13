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
import com.selfxdsd.api.Contracts;
import com.selfxdsd.api.Provider;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration tests for {@link SelfContracts}.
 * Read the package-info.java if you want to run these tests manually.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class SelfContractsITCase {

    /**
     * SelfContracts.findById returns the found Contract.
     */
    @Test
    public void returnsFoundContract() {
        final Contracts all = new SelfJooq(new H2Database()).contracts();
        final Contract johnDev = all.findById(
            new Contract.Id(
                "amihaiemil/docker-java-api",
                "john",
                Provider.Names.GITHUB,
                Contract.Roles.DEV
            )
        );
        MatcherAssert.assertThat(johnDev, Matchers.notNullValue());
        MatcherAssert.assertThat(
            johnDev.project().repoFullName(),
            Matchers.equalTo("amihaiemil/docker-java-api")
        );
        MatcherAssert.assertThat(
            johnDev.contributor().username(),
            Matchers.equalTo("john")
        );
        MatcherAssert.assertThat(
            johnDev.role(),
            Matchers.equalTo(Contract.Roles.DEV)
        );
    }

    /**
     * SelfContracts.findById returns null if the Contract is not found.
     */
    @Test
    public void returnsNullForNotFound() {
        final Contracts all = new SelfJooq(new H2Database()).contracts();
        final Contract missing = all.findById(
            new Contract.Id(
                "amihaiemil/docker-java-api",
                "john_doe_missing",
                Provider.Names.GITHUB,
                Contract.Roles.DEV
            )
        );
        MatcherAssert.assertThat(missing, Matchers.nullValue());
    }

    /**
     * SelfContracts is iterable.
     */
    @Test
    public void canBeIterated() {
        final Contracts all = new SelfJooq(new H2Database()).contracts();
        MatcherAssert.assertThat(
            all,
            Matchers.iterableWithSize(
                Matchers.greaterThan(0)
            )
        );
    }

}
