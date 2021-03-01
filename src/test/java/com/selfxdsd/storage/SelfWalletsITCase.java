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
import com.selfxdsd.core.projects.FakeWallet;
import com.selfxdsd.core.projects.StripeWallet;
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
     * SelfWallets can register a new Fake Wallet for a Project.
     */
    @Test
    public void registersFakeWallet() {
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

        final Wallet registered = all.register(
            project,
            Wallet.Type.FAKE,
            BigDecimal.valueOf(2_000_000),
            "fakew_123_am"
        );
        MatcherAssert.assertThat(
            registered,
            Matchers.instanceOf(FakeWallet.class)
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
            Matchers.iterableWithSize(
                Matchers.greaterThanOrEqualTo(1)
            )
        );
    }

    /**
     * SelfWallets can register a new Stripe Wallet for a Project.
     */
    @Test
    public void registersStripeWallet() {
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

        final Wallet registered = all.register(
            project,
            Wallet.Type.STRIPE,
            BigDecimal.valueOf(1000),
            "stripe_123_am"
        );
        MatcherAssert.assertThat(
            registered,
            Matchers.instanceOf(StripeWallet.class)
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
            Matchers.equalTo(BigDecimal.valueOf(1000))
        );

        MatcherAssert.assertThat(
            all.ofProject(project),
            Matchers.iterableWithSize(
                Matchers.greaterThanOrEqualTo(1)
            )
        );
    }

    /**
     * SelfWallets.register(...) rejects a wallet of an unknown type.
     */
    @Test (expected = UnsupportedOperationException.class)
    public void registerRejectsUnknownType() {
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

        final Wallet registered = all.register(
            project,
            "PAYPAL",
            BigDecimal.valueOf(1000),
            "stripe_123_am"
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
            Matchers.iterableWithSize(Matchers.greaterThanOrEqualTo(2))
        );
        final Wallet wallet = ofProject.active();

        MatcherAssert.assertThat(
            wallet.active(),
            Matchers.is(Boolean.TRUE)
        );
        MatcherAssert.assertThat(
            wallet.cash(),
            Matchers.equalTo(BigDecimal.valueOf(10000))
        );
        MatcherAssert.assertThat(
            wallet.project(),
            Matchers.is(project)
        );
        MatcherAssert.assertThat(
            wallet.type(),
            Matchers.equalTo(Wallet.Type.STRIPE)
        );
    }

    /**
     * Method updateCash for a real Wallet.
     */
    @Test
    public void updatesCash(){
        final Storage storage = new SelfJooq(new H2Database());
        final Projects projects = storage.projects();
        final Wallets all = storage.wallets();
        final Project project = projects.getProjectById(
            "amihaiemil/docker-java-api", Provider.Names.GITHUB
        );
        final Wallet wallet = all.ofProject(project).active();

        MatcherAssert.assertThat(wallet.cash(),
            Matchers.equalTo(BigDecimal.valueOf(10000)));

        final Wallet updated = wallet.updateCash(BigDecimal.valueOf(8500));
        MatcherAssert.assertThat(updated.cash(),
            Matchers.equalTo(BigDecimal.valueOf(8500)));

        //revert
        wallet.updateCash(BigDecimal.valueOf(10000));
    }

    /**
     * It can remove a Wallet.
     */
    @Test
    public void removesWallet() {
        final Storage storage = new SelfJooq(new H2Database());
        final Projects projects = storage.projects();
        final Wallets all = storage.wallets();
        final Project project = projects.getProjectById(
            "johndoe/stripe_repo", Provider.Names.GITHUB
        );
        final Wallets wallets = all.ofProject(project);

        MatcherAssert.assertThat(
            wallets,
            Matchers.iterableWithSize(2)
        );

        Wallet fakeWallet = null;
        for(final Wallet wallet : wallets) {
            if(wallet.type().equalsIgnoreCase("FAKE")) {
                fakeWallet = wallet;
                break;
            }
        }
        MatcherAssert.assertThat(
            all.remove(fakeWallet),
            Matchers.is(Boolean.TRUE)
        );

        MatcherAssert.assertThat(
            all.ofProject(project),
            Matchers.iterableWithSize(1)
        );
    }
}
