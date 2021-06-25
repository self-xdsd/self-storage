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

import com.selfxdsd.api.CachedResource;
import com.selfxdsd.api.Resource;
import com.selfxdsd.api.storage.JsonStorage;
import com.selfxdsd.api.storage.Storage;
import org.jooq.Record;
import org.jooq.Result;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.StringReader;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public CachedResource getResource(final URI uri) {
        final Result<Record> result = this.database.jooq()
            .select()
            .from(SLF_JSONSTORAGE_XDSD)
            .where(SLF_JSONSTORAGE_XDSD.URL.eq(uri.toString()))
            .fetch();
        if(result.size() > 0) {
            final Record record = result.get(0);
            return new CachedResource() {
                @Override
                public URI uri() {
                    return uri;
                }

                @Override
                public String etag() {
                    return record.get(SLF_JSONSTORAGE_XDSD.ETAG);
                }

                @Override
                public LocalDateTime creationDate() {
                    return null;
                }

                @Override
                public int statusCode() {
                    return 200;
                }

                @Override
                public JsonObject asJsonObject() {
                    return Json.createReader(
                        new StringReader(
                            record.get(SLF_JSONSTORAGE_XDSD.JSONBODY)
                        )
                    ).readObject();
                }

                @Override
                public JsonArray asJsonArray() {
                    return Json.createReader(
                        new StringReader(
                            record.get(SLF_JSONSTORAGE_XDSD.JSONBODY)
                        )
                    ).readArray();
                }

                @Override
                public String body() {
                    return record.get(SLF_JSONSTORAGE_XDSD.JSONBODY);
                }

                @Override
                public Map<String, List<String>> headers() {
                    return new HashMap<>();
                }

            };
        }
        return null;
    }

    @Override
    public CachedResource storeResource(
        final URI uri,
        final Resource resource
    ) {
        final String etag = resource.etag();
        final String body = resource.body();
        if(uri.toString().length() < 1024
            && !etag.isBlank() && !body.isBlank()) {
            this.database.jooq().insertInto(
                SLF_JSONSTORAGE_XDSD,
                SLF_JSONSTORAGE_XDSD.URL,
                SLF_JSONSTORAGE_XDSD.ETAG,
                SLF_JSONSTORAGE_XDSD.JSONBODY
            ).values(
                uri.toString(),
                etag,
                body
            ).execute();
        }

        final LocalDateTime created = LocalDateTime.now();
        return new CachedResource() {
            @Override
            public URI uri() {
                return uri;
            }

            @Override
            public String etag() {
                return resource.etag();
            }

            @Override
            public LocalDateTime creationDate() {
                return created;
            }

            @Override
            public int statusCode() {
                return resource.statusCode();
            }

            @Override
            public JsonObject asJsonObject() {
                return resource.asJsonObject();
            }

            @Override
            public JsonArray asJsonArray() {
                return resource.asJsonArray();
            }

            @Override
            public String body() {
                return resource.body();
            }

            @Override
            public Map<String, List<String>> headers() {
                return resource.headers();
            }
        };
    }

    @Override
    public CachedResource updateResource(
        final URI uri,
        final Resource resource
    ) {
        final String etag = resource.etag();
        final String body = resource.body();
        if(uri.toString().length() < 1024
            && !etag.isBlank() && !body.isBlank()) {
            this.database.jooq()
                .update(SLF_JSONSTORAGE_XDSD)
                .set(SLF_JSONSTORAGE_XDSD.ETAG, etag)
                .set(SLF_JSONSTORAGE_XDSD.JSONBODY, body)
                .where(SLF_JSONSTORAGE_XDSD.URL.eq(uri.toString()))
                .execute();
        }
        return new CachedResource() {
            @Override
            public URI uri() {
                return uri;
            }

            @Override
            public String etag() {
                return resource.etag();
            }

            @Override
            public LocalDateTime creationDate() {
                return null;
            }

            @Override
            public int statusCode() {
                return resource.statusCode();
            }

            @Override
            public JsonObject asJsonObject() {
                return resource.asJsonObject();
            }

            @Override
            public JsonArray asJsonArray() {
                return resource.asJsonArray();
            }

            @Override
            public String body() {
                return resource.body();
            }

            @Override
            public Map<String, List<String>> headers() {
                return resource.headers();
            }
        };
    }
}
