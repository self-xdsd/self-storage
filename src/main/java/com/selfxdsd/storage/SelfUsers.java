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
import org.jooq.Record;
import org.jooq.Result;

import java.util.Iterator;

import static com.selfxdsd.storage.generated.jooq.tables.SlfUsersXdsd.SLF_USERS_XDSD;

/**
 * All the Users in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class SelfUsers implements Users {

    /**
     * Database.
     */
    private final Database database;

    /**
     * Ctor.
     * @param database Database.
     */
    public SelfUsers(final Database database) {
        this.database = database;
    }

    @Override
    public User signUp(final User user) {
        return null;
    }

    @Override
    public User user(final String username, final String provider) {
        try (final Database db = this.database.connect()) {
            final Result<Record> result = db.jooq()
                .select()
                .from(SLF_USERS_XDSD)
                .where(
                    SLF_USERS_XDSD.USERNAME.eq(username).and(
                        SLF_USERS_XDSD.PROVIDER.eq(provider)
                    )
                )
                .fetch();
            if(!result.isEmpty()) {
                Record r = result.get(0);

                String usr = r.getValue(SLF_USERS_XDSD.USERNAME);
                String prv = r.getValue(SLF_USERS_XDSD.PROVIDER);
                String email = r.getValue(SLF_USERS_XDSD.EMAIL);
                String avatar = r.getValue(SLF_USERS_XDSD.AVATAR);

                System.out.println("----------------");
                System.out.println("Username: " + usr);
                System.out.println("Provider: " + prv);
                System.out.println("E-Mail: " + email);
                System.out.println("Avatar: " + avatar);
                System.out.println("----------------");            }
        }
        return null;
    }

    @Override
    public Iterator<User> iterator() {
        return null;
    }
}
