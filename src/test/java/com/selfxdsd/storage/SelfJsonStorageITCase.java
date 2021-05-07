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

import com.selfxdsd.api.storage.JsonStorage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.util.UUID;

/**
 * Integration tests for {@link SelfJsonStorage}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @since 0.0.70
 * @version $Id$
 */
public final class SelfJsonStorageITCase {

    /**
     * Returns found Etag based on URL.
     */
    @Test
    public void returnsFoundETag() {
        final JsonStorage jsonStorage = new SelfJooq(new H2Database())
            .jsonStorage();
        MatcherAssert.assertThat(
            jsonStorage.getEtag(
                URI.create(
                    "https://github.com/self-xdsd/self-storage/issues/123"
                )
            ),
            Matchers.equalTo("etag123321")
        );
    }

    /**
     * Returns null etag if no record is found for the given URL.
     */
    @Test
    public void returnsNullMissingEtag() {
        final JsonStorage jsonStorage = new SelfJooq(new H2Database())
            .jsonStorage();
        MatcherAssert.assertThat(
            jsonStorage.getEtag(
                URI.create(
                    "https://github.com/self-xdsd/self-storage/issues/999"
                )
            ),
            Matchers.nullValue()
        );
    }

    /**
     * Returns found body based on URL.
     */
    @Test
    public void returnsFoundBody() {
        final JsonStorage jsonStorage = new SelfJooq(new H2Database())
            .jsonStorage();
        MatcherAssert.assertThat(
            jsonStorage.getResourceBody(
                URI.create(
                    "https://github.com/self-xdsd/self-storage/issues/123"
                )
            ),
            Matchers.equalTo("{\"issueId\":\"123\"}")
        );
    }

    /**
     * Returns null body if no record is found for the given URL.
     */
    @Test
    public void returnsNullMissingBody() {
        final JsonStorage jsonStorage = new SelfJooq(new H2Database())
            .jsonStorage();
        MatcherAssert.assertThat(
            jsonStorage.getResourceBody(
                URI.create(
                    "https://github.com/self-xdsd/self-storage/issues/999"
                )
            ),
            Matchers.nullValue()
        );
    }

    /**
     * Method store doesn't insert/update anything if the etag and body are
     * blank.
     */
    @Test
    public void storeIgnoresBlankEtagAndBody() {
        final Database mockDb = Mockito.mock(Database.class);
        final JsonStorage jsonStorage = new SelfJooq(mockDb)
            .jsonStorage();
        jsonStorage.store(URI.create("https://github.com"), "", "");
        Mockito.verify(
            mockDb, Mockito.times(0)
        ).jooq();
    }

    /**
     * Method store inserts a new resource.
     */
    @Test
    public void storesNewResource() {
        final JsonStorage jsonStorage = new SelfJooq(new H2Database())
            .jsonStorage();
        final String issueId = UUID.randomUUID().toString();
        final URI uri = URI.create(
            "httos://github.com/self-xdsd/self-storage/issues/" + issueId
        );

        jsonStorage.store(
            uri, "etag456654", "{\"issueId\":\"" + issueId + "\"}"
        );

        MatcherAssert.assertThat(
            jsonStorage.getEtag(uri),
            Matchers.equalTo("etag456654")
        );
    }

    /**
     * Method store updates an existing resource.
     */
    @Test
    public void updatesExistingResource() {
        final JsonStorage jsonStorage = new SelfJooq(new H2Database())
            .jsonStorage();
        final String issueId = UUID.randomUUID().toString();
        final URI uri = URI.create(
            "httos://github.com/self-xdsd/self-storage/issues/" + issueId
        );

        jsonStorage.store(
            uri, "etag456654", "{\"body\":\"some body\"}"
        );

        MatcherAssert.assertThat(
            jsonStorage.getEtag(uri),
            Matchers.equalTo("etag456654")
        );

        MatcherAssert.assertThat(
            jsonStorage.getResourceBody(uri),
            Matchers.equalTo("{\"body\":\"some body\"}")
        );

        jsonStorage.store(
            uri, "etag789987", "{\"body\":\"another body\"}"
        );

        MatcherAssert.assertThat(
            jsonStorage.getEtag(uri),
            Matchers.equalTo("etag789987")
        );

        MatcherAssert.assertThat(
            jsonStorage.getResourceBody(uri),
            Matchers.equalTo("{\"body\":\"another body\"}")
        );
    }
}
