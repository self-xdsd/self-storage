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

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * MySql Database.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class MySql implements Database {

    /**
     * DB Url.
     */
    private final String dbUrl;

    /**
     * DB User.
     */
    private final String username;

    /**
     * DB Password.
     */
    private final String password;

    /**
     * JDBC Connection.
     */
    private Connection connection;

    /**
     * Constructor to obtain an unconnected instance.
     * @param dbUrl DB Url.
     * @param username DB User.
     * @param password DB Password.
     */
    public MySql(
        final String dbUrl,
        final String username,
        final String password
    ) {
        this.dbUrl = dbUrl;
        this.username = username;
        this.password = password;
        this.connection = null;
    }

    @Override
    public MySql connect() {
        if(this.connection == null) {
            try {
                this.connection = DriverManager.getConnection(
                    this.dbUrl,
                    this.username,
                    this.password
                );
            } catch (final SQLException exception) {
                throw new IllegalStateException(
                    "Could not connect to the DB",
                    exception
                );
            }
        }
        return this;
    }

    @Override
    public DSLContext jooq() {
        if(this.connection == null) {
            throw new IllegalStateException("You need to connect first!");
        }
        return DSL.using(connection, SQLDialect.MYSQL);
    }

    @Override
    public void close() {
        if(this.connection != null) {
            try {
                this.connection.close();
            } catch (final SQLException exception) {
                throw new IllegalStateException(
                    "Could not close the DB Connection",
                    exception
                );
            }
        }
    }

    @Override
    public String dbms() {
        return Dbms.MY_SQL;
    }
}
