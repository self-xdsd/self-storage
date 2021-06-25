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

import com.selfxdsd.api.Resource;
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
     * Returns found Resource based on URL.
     */
    @Test
    public void returnsFoundResource() {
        final JsonStorage jsonStorage = new SelfJooq(new H2Database())
            .jsonStorage();
        final Resource found = jsonStorage.getResource(
            URI.create(
                "https://github.com/self-xdsd/self-storage/issues/123"
            )
        );
        MatcherAssert.assertThat(
            found, Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            found.etag(),
            Matchers.equalTo("etag123321")
        );
        MatcherAssert.assertThat(
            found.body(),
            Matchers.equalTo("{\"issueId\":\"123\"}")
        );
    }

    /**
     * Returns null resource if no record is found for the given URL.
     */
    @Test
    public void returnsNullMissingResource() {
        final JsonStorage jsonStorage = new SelfJooq(new H2Database())
            .jsonStorage();
        MatcherAssert.assertThat(
            jsonStorage.getResource(
                URI.create(
                    "https://github.com/self-xdsd/self-storage/issues/999"
                )
            ),
            Matchers.nullValue()
        );
    }

    /**
     * Method store doesn't insert anything if the etag and body are
     * blank.
     */
    @Test
    public void storeIgnoresBlankEtagAndBody() {
        final Database mockDb = Mockito.mock(Database.class);
        final JsonStorage jsonStorage = new SelfJooq(mockDb)
            .jsonStorage();
        final Resource res = Mockito.mock(Resource.class);
        Mockito.when(res.body()).thenReturn("");
        Mockito.when(res.etag()).thenReturn("");
        jsonStorage.storeResource(URI.create("https://github.com"), res);
        Mockito.verify(
            mockDb, Mockito.times(0)
        ).jooq();
    }

    /**
     * Method update doesn't do anything if the etag and body are
     * blank.
     */
    @Test
    public void updateIgnoresBlankEtagAndBody() {
        final Database mockDb = Mockito.mock(Database.class);
        final JsonStorage jsonStorage = new SelfJooq(mockDb)
            .jsonStorage();
        final Resource res = Mockito.mock(Resource.class);
        Mockito.when(res.body()).thenReturn("");
        Mockito.when(res.etag()).thenReturn("");
        jsonStorage.updateResource(URI.create("https://github.com"), res);
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

        final Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.etag()).thenReturn("etag456654");
        Mockito.when(resource.body())
            .thenReturn("{\"issueId\":\"" + issueId + "\"}");

        jsonStorage.storeResource(uri, resource);

        MatcherAssert.assertThat(
            jsonStorage.getResource(uri).etag(),
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

        final Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.etag()).thenReturn("etag456654");
        Mockito.when(resource.body())
            .thenReturn("{\"issueId\":\"" + issueId + "\"}");

        jsonStorage.storeResource(uri, resource);

        MatcherAssert.assertThat(
            jsonStorage.getResource(uri).etag(),
            Matchers.equalTo("etag456654")
        );

        final Resource updated = Mockito.mock(Resource.class);
        Mockito.when(updated.etag()).thenReturn("etag789987");
        Mockito.when(updated.body())
            .thenReturn("{\"body\":\"another body\"}");

        jsonStorage.updateResource(uri, updated);

        MatcherAssert.assertThat(
            jsonStorage.getResource(uri).etag(),
            Matchers.equalTo("etag789987")
        );

        MatcherAssert.assertThat(
            jsonStorage.getResource(uri).body(),
            Matchers.equalTo("{\"body\":\"another body\"}")
        );
    }
}
