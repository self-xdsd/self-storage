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
import com.selfxdsd.api.storage.Storage;
import org.jooq.Record;
import org.jooq.Result;

import java.net.URI;
import static com.selfxdsd.storage.generated.jooq.Tables.SLF_JSONSTORAGE_XDSD;

/**
 * Storage for JsonResources in Self XDSD (we store JSONs received from
 * providers' API for making conditional requests using E-Tag).
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @since 0.0.70
 * @version $Id$
 */
public final class SelfJsonStorage implements JsonStorage {

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
    public SelfJsonStorage(
        final Storage storage,
        final Database database
    ) {
        this.storage = storage;
        this.database = database;
    }

    @Override
    public String getEtag(final URI uri) {
        final Result<Record> result = this.database.jooq()
            .select()
            .from(SLF_JSONSTORAGE_XDSD)
            .where(SLF_JSONSTORAGE_XDSD.URL.eq(uri.toString()))
            .fetch();
        if(result.size() > 0) {
            return result.get(0).get(SLF_JSONSTORAGE_XDSD.ETAG);
        }
        return null;
    }

    @Override
    public String getResourceBody(final URI uri) {
        final Result<Record> result = this.database.jooq()
            .select()
            .from(SLF_JSONSTORAGE_XDSD)
            .where(SLF_JSONSTORAGE_XDSD.URL.eq(uri.toString()))
            .fetch();
        if(result.size() > 0) {
            return result.get(0).get(SLF_JSONSTORAGE_XDSD.JSONBODY);
        }
        return null;
    }

    @Override
    public void store(final URI uri, final String etag, final String resource) {
        if(uri.toString().length() < 1024
            && !etag.isBlank() && !resource.isBlank()) {
            if(this.getEtag(uri) == null) {
                this.database.jooq().insertInto(
                    SLF_JSONSTORAGE_XDSD,
                    SLF_JSONSTORAGE_XDSD.URL,
                    SLF_JSONSTORAGE_XDSD.ETAG,
                    SLF_JSONSTORAGE_XDSD.JSONBODY
                ).values(
                    uri.toString(),
                    etag,
                    resource
                ).execute();
            } else {
                this.database.jooq()
                    .update(SLF_JSONSTORAGE_XDSD)
                    .set(SLF_JSONSTORAGE_XDSD.ETAG, etag)
                    .set(SLF_JSONSTORAGE_XDSD.JSONBODY, resource)
                    .where(SLF_JSONSTORAGE_XDSD.URL.eq(uri.toString()))
                    .execute();
            }
        }
    }
}
