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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Integration tests for {@link SelfApiTokens}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.27
 */
public final class SelfApiTokensITCase {

    /**
     * Method getById(...) can return the found ApiToken.
     */
    @Test
    public void returnsFoundApiToken() {
        final ApiTokens all = new SelfJooq(
            new H2Database()
        ).apiTokens();
        final ApiToken found = all.getById("apiToken123");
        MatcherAssert.assertThat(
            found,
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            found.name(),
            Matchers.equalTo("Mihai Token 1")
        );
        MatcherAssert.assertThat(
            found.token(),
            Matchers.equalTo("apiToken123")
        );
        MatcherAssert.assertThat(
            found.expiration(),
            Matchers.equalTo(LocalDateTime.of(2022, 1, 1, 0, 0, 0))
        );
        final User owner = found.owner();
        MatcherAssert.assertThat(
            owner.username(),
            Matchers.equalTo("amihaiemil")
        );
        MatcherAssert.assertThat(
            owner.provider().name(),
            Matchers.equalTo(Provider.Names.GITHUB)
        );
    }

    /**
     * Method getById(...) returns null if the ApiToken is missing.
     */
    @Test
    public void returnsNullOnMissingApiToken() {
        final ApiTokens all = new SelfJooq(
            new H2Database()
        ).apiTokens();
        final ApiToken found = all.getById("missing123");
        MatcherAssert.assertThat(
            found,
            Matchers.nullValue()
        );
    }

}
