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

import com.selfxdsd.api.ProjectManager;
import com.selfxdsd.api.ProjectManagers;
import com.selfxdsd.api.Provider;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration tests for {@link SelfPms}.
 * Read the package-info.java if you want to run these tests manually.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class SelfPmsITCase {

    /**
     * SelfPms.getById returns a found PM.
     */
    @Test
    public void returnsFoundPm() {
        final ProjectManagers pms = new SelfJooq(
            new H2Database()
        ).projectManagers();
        final ProjectManager found = pms.getById(1);
        MatcherAssert.assertThat(found.id(), Matchers.equalTo(1));
        MatcherAssert.assertThat(
            found.provider().name(),
            Matchers.equalTo(Provider.Names.GITHUB)
        );
    }

    /**
     * SelfPms.getById returns null if the PM is missing.
     */
    @Test
    public void returnsNullOnMissingPm() {
        final ProjectManagers pms = new SelfJooq(
            new H2Database()
        ).projectManagers();
        final ProjectManager found = pms.getById(321);
        MatcherAssert.assertThat(found, Matchers.nullValue());
    }

    /**
     * Picks a PM by provider name.
     */
    @Test
    public void picksPm() {
        final Database database = new H2Database();
        final ProjectManagers pms = new SelfJooq(
            new H2Database()
        ).projectManagers();

        final ProjectManager projectManager = pms.pick(Provider.Names.GITHUB);

        MatcherAssert.assertThat(projectManager, Matchers.notNullValue());
    }

    /**
     * Return null if there is no PM associated with provider name.
     */
    @Test
    public void pickReturnsNotFound() {
        final Database database = new H2Database();
        final ProjectManagers pms = new SelfJooq(
            new H2Database()
        ).projectManagers();

        final ProjectManager projectManager = pms.pick("some_provider");

        MatcherAssert.assertThat(projectManager, Matchers.nullValue());

    }

    /**
     * Register a PM into database.
     */
    @Test
    public void registersPm(){
        final ProjectManagers pms = new SelfJooq(
            new H2Database()
        ).projectManagers();
        final ProjectManager registered = pms
            .register("123", "zoeself", Provider.Names.GITLAB, "123gitlab");
        MatcherAssert.assertThat(registered.id(),
            Matchers.greaterThan(0));
        MatcherAssert.assertThat(registered.userId(),
            Matchers.equalTo(123));
        MatcherAssert.assertThat(registered.provider().name(),
            Matchers.equalTo(Provider.Names.GITLAB));
    }

    /**
     * Iterates over PMSs.
     */
    @Test
    public void iteratesPms(){
        final ProjectManagers pms = new SelfJooq(
            new H2Database()
        ).projectManagers();
        MatcherAssert.assertThat(
            pms,
            Matchers.iterableWithSize(
                Matchers.greaterThanOrEqualTo(1)
            )
        );
    }
}
