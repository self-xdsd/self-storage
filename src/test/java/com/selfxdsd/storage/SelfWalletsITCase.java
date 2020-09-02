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

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Integration tests for {@link SelfWallets}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.8
 */
public final class SelfWalletsITCase {

    /**
     * SelfWallets can register a new Wallet in a Project.
     */
    @Test
    public void registersWallet() {
        final Storage storage = new SelfJooq(new H2Database());
        final Projects projects = storage.projects();
        final Wallets all = storage.wallets();

        final Project project = projects.getProjectById(
            "amihaiemil/amihaiemil.github.io", Provider.Names.GITHUB
        );
        MatcherAssert.assertThat(
            project,
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            all.ofProject(project),
            Matchers.emptyIterable()
        );

        final Wallet registered = all.register(
            project,
            Wallet.Type.FAKE,
            BigDecimal.valueOf(2_000_000),
            "fakew_123_am"
        );
        MatcherAssert.assertThat(
            registered.project(),
            Matchers.is(project)
        );
        MatcherAssert.assertThat(
            registered.active(),
            Matchers.is(Boolean.FALSE)
        );
        MatcherAssert.assertThat(
            registered.cash(),
            Matchers.equalTo(BigDecimal.valueOf(2_000_000))
        );

        MatcherAssert.assertThat(
            all.ofProject(project),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * A wallet can be activated.
     */
    @Test
    public void activatesWallet() {
        final Storage storage = new SelfJooq(new H2Database());
        final Projects projects = storage.projects();
        final Wallets all = storage.wallets();

        final Project project = projects.getProjectById(
            "vlad/test", Provider.Names.GITHUB
        );
        MatcherAssert.assertThat(
            project,
            Matchers.notNullValue()
        );
        all.register(
            project,
            Wallet.Type.FAKE,
            BigDecimal.valueOf(3_000_000),
            "fakew_123_vlad"
        );
        final Wallet inactive = all.ofProject(project).iterator().next();
        MatcherAssert.assertThat(
            inactive.active(),
            Matchers.is(Boolean.FALSE)
        );
        final Wallet activated = all.activate(inactive);
        MatcherAssert.assertThat(
            activated.active(),
            Matchers.is(Boolean.TRUE)
        );
        final Wallet selectedActive = all.ofProject(project).iterator().next();
        MatcherAssert.assertThat(
            selectedActive.active(),
            Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * Method ofProject can return empty if
     * the project has no wallets registered.
     */
    @Test
    public void ofProjectReturnsEmpty() {
        final Storage storage = new SelfJooq(new H2Database());
        final Projects projects = storage.projects();
        final Wallets all = storage.wallets();

        final Project project = projects.getProjectById(
            "mihai/test", Provider.Names.GITLAB
        );
        MatcherAssert.assertThat(
            project,
            Matchers.notNullValue()
        );

        final Wallets ofProject = all.ofProject(project);
        MatcherAssert.assertThat(
            ofProject,
            Matchers.emptyIterable()
        );
    }

    /**
     * Method ofProject returns a Project's wallets if they exist.
     */
    @Test
    public void ofProjectReturnsWallets() {
        final Storage storage = new SelfJooq(new H2Database());
        final Projects projects = storage.projects();
        final Wallets all = storage.wallets();

        final Project project = projects.getProjectById(
            "amihaiemil/docker-java-api", Provider.Names.GITHUB
        );
        MatcherAssert.assertThat(
            project,
            Matchers.notNullValue()
        );

        final Wallets ofProject = all.ofProject(project);
        MatcherAssert.assertThat(
            ofProject,
            Matchers.iterableWithSize(1)
        );
        final Wallet wallet = ofProject.iterator().next();

        MatcherAssert.assertThat(
            wallet.active(),
            Matchers.is(Boolean.TRUE)
        );
        MatcherAssert.assertThat(
            wallet.cash(),
            Matchers.equalTo(BigDecimal.valueOf(1_000_000_000))
        );
        MatcherAssert.assertThat(
            wallet.available().add(wallet.debt()),
            Matchers.equalTo(wallet.cash())
        );
        MatcherAssert.assertThat(
            wallet.project(),
            Matchers.is(project)
        );
        MatcherAssert.assertThat(
            wallet.type(),
            Matchers.equalTo(Wallet.Type.FAKE)
        );
    }

}
