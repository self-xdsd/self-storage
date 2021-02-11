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
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Iterator;

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

    /**
     * We shouldn't be able to iterate over all ApiTokens in Self.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void iteratorUnsupported() {
        new SelfJooq(
            new H2Database()
        ).apiTokens().iterator();
    }

    /**
     * Method ofUser returns the existing tokens of a User.
     */
    @Test
    public void userTokensFoundCanBeIterated() {
        final Storage storage = new SelfJooq(
            new H2Database()
        );
        final ApiTokens all = storage.apiTokens();
        final ApiTokens ofMihai = all.ofUser(
            storage.users().user("amihaiemil", Provider.Names.GITHUB)
        );
        MatcherAssert.assertThat(
            ofMihai,
            Matchers.iterableWithSize(
                Matchers.greaterThanOrEqualTo(2)
            )
        );
        final Iterator<ApiToken> iterator = ofMihai.iterator();
        MatcherAssert.assertThat(
            iterator.next().token(),
            Matchers.equalTo("apiToken123")
        );
        MatcherAssert.assertThat(
            iterator.next().token(),
            Matchers.equalTo("apiToken456")
        );
    }

    /**
     * Method ofUser can return an empty iterable.
     */
    @Test
    public void userTokensIsEmpty() {
        final Storage storage = new SelfJooq(
            new H2Database()
        );
        final ApiTokens all = storage.apiTokens();
        final ApiTokens empty = all.ofUser(
            storage.users().user("maria", Provider.Names.GITHUB)
        );
        MatcherAssert.assertThat(
            empty,
            Matchers.emptyIterable()
        );
    }

    /**
     * Method ofUser returns itself for the same user.
     */
    @Test
    public void userTokensReturnsItselfForSameUser() {
        final Storage storage = new SelfJooq(
            new H2Database()
        );
        final ApiTokens all = storage.apiTokens();
        final User mihai = storage.users().user(
            "amihaiemil", Provider.Names.GITHUB
        );
        final ApiTokens ofMihai = all.ofUser(mihai);
        MatcherAssert.assertThat(
            ofMihai.ofUser(mihai),
            Matchers.is(ofMihai)
        );
    }

    /**
     * A Users' ApiTokens complains if we call #ofUser(...) with another user.
     */
    @Test(expected = IllegalStateException.class)
    public void userTokensComplainsForDifferentUser() {
        final Storage storage = new SelfJooq(
            new H2Database()
        );
        final ApiTokens all = storage.apiTokens();
        final User mihai = storage.users().user(
            "amihaiemil", Provider.Names.GITHUB
        );
        final User vlad = storage.users().user(
            "vlad", Provider.Names.GITHUB
        );
        final ApiTokens ofMihai = all.ofUser(mihai);
        ofMihai.ofUser(vlad);
    }

    /**
     * A Users' ApiTokens returns a token by its ID, if the token is owned
     * by the same user.
     */
    @Test
    public void userTokensReturnsTokenById() {
        final Storage storage = new SelfJooq(
            new H2Database()
        );
        final ApiTokens all = storage.apiTokens();
        final User mihai = storage.users().user(
            "amihaiemil", Provider.Names.GITHUB
        );
        final ApiTokens ofMihai = all.ofUser(mihai);
        final ApiToken first = ofMihai.getById("apiToken123");
        MatcherAssert.assertThat(
            first,
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            first.name(),
            Matchers.equalTo("Mihai Token 1")
        );
        MatcherAssert.assertThat(
            first.token(),
            Matchers.equalTo("apiToken123")
        );
        MatcherAssert.assertThat(
            first.expiration(),
            Matchers.equalTo(LocalDateTime.of(2022, 1, 1, 0, 0, 0))
        );
        MatcherAssert.assertThat(
            first.owner().username(),
            Matchers.equalTo("amihaiemil")
        );
        MatcherAssert.assertThat(
            first.owner().provider().name(),
            Matchers.equalTo(Provider.Names.GITHUB)
        );
    }

    /**
     * A Users' ApiTokens returns null if getById(...) finds no token.
     */
    @Test
    public void userTokensReturnsNullMissingTokenById() {
        final Storage storage = new SelfJooq(
            new H2Database()
        );
        final ApiTokens all = storage.apiTokens();
        final User mihai = storage.users().user(
            "amihaiemil", Provider.Names.GITHUB
        );
        final ApiTokens ofMihai = all.ofUser(mihai);
        final ApiToken missing = ofMihai.getById("missing");
        MatcherAssert.assertThat(
            missing,
            Matchers.nullValue()
        );
    }

    /**
     * A Users' ApiTokens returns null if getById(...) finds a token which
     * is owned by someone else.
     */
    @Test
    public void userTokensReturnsNullUnknownTokenById() {
        final Storage storage = new SelfJooq(
            new H2Database()
        );
        final ApiTokens all = storage.apiTokens();
        final User mihai = storage.users().user(
            "amihaiemil", Provider.Names.GITHUB
        );
        final ApiTokens ofMihai = all.ofUser(mihai);
        final ApiToken johnToken = ofMihai.getById("apiToken789");
        MatcherAssert.assertThat(
            johnToken,
            Matchers.nullValue()
        );
    }

    /**
     * It can register a new ApiToken for a User.
     */
    @Test
    public void registersNewApiTokenForUser() {
        final Storage storage = new SelfJooq(
            new H2Database()
        );
        final ApiTokens all = storage.apiTokens();
        final User mihai = storage.users().user(
            "amihaiemil", Provider.Names.GITHUB
        );
        final ApiTokens ofMihai = all.ofUser(mihai);
        MatcherAssert.assertThat(
            ofMihai,
            Matchers.iterableWithSize(2)
        );
        final ApiToken registered = all.register(
            "Mihai Token 3",
            "apiToken334455",
            LocalDateTime.of(2022, 2, 1, 0, 0, 0),
            mihai
        );
        MatcherAssert.assertThat(
            ofMihai,
            Matchers.iterableWithSize(3)
        );
        MatcherAssert.assertThat(
            registered,
            Matchers.equalTo(ofMihai.getById("apiToken334455"))
        );
    }

}
