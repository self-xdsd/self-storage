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

import com.selfxdsd.api.User;
import com.selfxdsd.api.Users;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.StoredUser;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.selfxdsd.storage.generated.jooq.tables.SlfUsersXdsd.SLF_USERS_XDSD;

/**
 * All the Users in Self.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class SelfUsers implements Users {

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
     *
     * @param storage Parent Storage.
     * @param database Database.
     */
    public SelfUsers(
        final Storage storage,
        final Database database
    ) {
        this.storage = storage;
        this.database = database;
    }

    @Override
    public User signUp(
        final String username,
        final String email,
        final String provider
    ) {
        try (final Database db = this.database.connect()) {
            final DSLContext jooq = db.jooq();
            User dbUser = userFromDb(
                jooq, username, provider
            );
            if (dbUser == null) {
                jooq.insertInto(
                    SLF_USERS_XDSD,
                    SLF_USERS_XDSD.USERNAME,
                    SLF_USERS_XDSD.PROVIDER,
                    SLF_USERS_XDSD.EMAIL
                ).values(
                    username,
                    provider,
                    email
                ).execute();
            } else if (!dbUser.email().equals(email)) {
                jooq.update(SLF_USERS_XDSD)
                        .set(SLF_USERS_XDSD.EMAIL, email)
                        .where(SLF_USERS_XDSD.USERNAME.eq(username))
                        .execute();
            } else {
                return dbUser;
            }
            return this.userFromDb(jooq, username, provider);
        }
    }

    @Override
    public User user(final String username, final String provider) {
        try (final Database db = this.database.connect()) {
            return userFromDb(db.jooq(), username, provider);
        }
    }

    /**
     * Get a user from database by user name and provider.
     *
     * @param jooq DslContext.
     * @param username User name.
     * @param provider Provider
     * @return User or null if not found.
     */
    private User userFromDb(
        final DSLContext jooq,
        final String username,
        final String provider
    ) {
        final Result<Record> result = jooq
                .select()
                .from(SLF_USERS_XDSD)
                .where(SLF_USERS_XDSD.USERNAME.eq(username)
                        .and(SLF_USERS_XDSD.PROVIDER.eq(provider)))
                .fetch();
        if (!result.isEmpty()) {
            final Record rec = result.get(0);
            final User found = new StoredUser(
                rec.getValue(SLF_USERS_XDSD.USERNAME),
                rec.getValue(SLF_USERS_XDSD.EMAIL),
                rec.getValue(SLF_USERS_XDSD.PROVIDER),
                this.storage
            );
            return found;
        }
        return null;
    }

    @Override
    public Iterator<User> iterator() {
        final List<User> users = new ArrayList<>();
        try (final Database db = this.database.connect()) {
            final Result<Record> result = db.jooq()
                .select()
                .from(SLF_USERS_XDSD)
                .limit(100)
                .fetch();
            for (final Record res : result) {
                users.add(
                    new StoredUser(
                        res.getValue(SLF_USERS_XDSD.USERNAME),
                        res.getValue(SLF_USERS_XDSD.EMAIL),
                        res.getValue(SLF_USERS_XDSD.PROVIDER),
                        this.storage
                    )
                );
            }
        }
        return users.iterator();
    }
}
