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

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Contracts;
import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.Provider;
import com.selfxdsd.storage.generated.jooq.tables.SlfContractsXdsd;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

/**
 * Integration tests for {@link SelfContracts}.
 * Read the package-info.java if you want to run these tests manually.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class SelfContractsITCase {

    /**
     * SelfContracts.findById returns the found Contract.
     */
    @Test
    public void returnsFoundContract() {
        final Contracts all = new SelfJooq(new H2Database()).contracts();
        final Contract johnDev = all.findById(
            new Contract.Id(
                "amihaiemil/docker-java-api",
                "john",
                Provider.Names.GITHUB,
                Contract.Roles.DEV
            )
        );
        MatcherAssert.assertThat(johnDev, Matchers.notNullValue());
        MatcherAssert.assertThat(
            johnDev.project().repoFullName(),
            Matchers.equalTo("amihaiemil/docker-java-api")
        );
        MatcherAssert.assertThat(
            johnDev.contributor().username(),
            Matchers.equalTo("john")
        );
        MatcherAssert.assertThat(
            johnDev.role(),
            Matchers.equalTo(Contract.Roles.DEV)
        );
    }

    /**
     * SelfContracts.findById returns null if the Contract is not found.
     */
    @Test
    public void returnsNullForNotFound() {
        final Contracts all = new SelfJooq(new H2Database()).contracts();
        final Contract missing = all.findById(
            new Contract.Id(
                "amihaiemil/docker-java-api",
                "john_doe_missing",
                Provider.Names.GITHUB,
                Contract.Roles.DEV
            )
        );
        MatcherAssert.assertThat(missing, Matchers.nullValue());
    }

    /**
     * SelfContracts is iterable.
     */
    @Test
    public void canBeIterated() {
        final Contracts all = new SelfJooq(new H2Database()).contracts();
        MatcherAssert.assertThat(
            all,
            Matchers.iterableWithSize(
                Matchers.greaterThan(0)
            )
        );
    }

    /**
     * SelfContracts can add new contract.
     */
    @Test
    public void addsContract(){
        final H2Database database = new H2Database();
        final Contracts all = new SelfJooq(database).contracts();
        final Contract contract = all
            .addContract("amihaiemil/docker-java-api", "bob",
                Provider.Names.GITHUB, BigDecimal.TEN,
                Contract.Roles.DEV);
        MatcherAssert.assertThat(contract.project().repoFullName(),
            Matchers.equalTo("amihaiemil/docker-java-api"));
        MatcherAssert.assertThat(contract.project().provider(),
            Matchers.equalTo(Provider.Names.GITHUB));
        MatcherAssert.assertThat(contract.contributor().username(),
            Matchers.equalTo("bob"));
        MatcherAssert.assertThat(contract.hourlyRate(),
            Matchers.equalTo(BigDecimal.TEN));
        MatcherAssert.assertThat(contract.role(),
            Matchers.equalTo(Contract.Roles.DEV));

        //cleanup
        database.connect().jooq()
            .delete(SlfContractsXdsd.SLF_CONTRACTS_XDSD)
            .where(SlfContractsXdsd.SLF_CONTRACTS_XDSD.USERNAME.eq("bob"))
            .execute();
    }

    /**
     * Throws IllegalStateException when contributor with key
     * contributorUsername + provider is not found.
     */
    @Test(expected = IllegalStateException.class)
    public void throwsOnAddContractWhenContributorNotFound(){
        new SelfJooq(new H2Database()).contracts()
            .addContract("amihaiemil/docker-java-api", "dan",
                Provider.Names.GITHUB, BigDecimal.TEN,
                Contract.Roles.DEV);
    }

    /**
     * Throws IllegalStateException if project with key repoFullName + provider
     * is not found.
     */
    @Test(expected = IllegalStateException.class)
    public void throwsOnAddContractWhenProjectNotFound(){
        new SelfJooq(new H2Database()).contracts()
            .addContract("amihaiemil/other", "bob",
                Provider.Names.GITHUB, BigDecimal.TEN,
                Contract.Roles.DEV);
    }

    /**
     * Throws IllegalStateException if contract is already into database.
     */
    @Test(expected = IllegalStateException.class)
    public void throwsOnAddContractWhenContractDuplicate(){
        new SelfJooq(new H2Database()).contracts()
            .addContract("amihaiemil/other", "john",
                Provider.Names.GITHUB, BigDecimal.TEN,
                Contract.Roles.DEV);
    }

    /**
     * Returns contracts of a given project.
     * See resource: insertData.sql.
     */
    @Test
    public void returnsContractsOfProject(){
        final Contracts ofProject = new SelfJooq(new H2Database()).contracts()
            .ofProject("amihaiemil/docker-java-api",
                Provider.Names.GITHUB);
        MatcherAssert.assertThat(ofProject, Matchers.iterableWithSize(4));
    }

    /**
     * Returns contracts of a given project.
     * See resource: insertData.sql.
     */
    @Test
    public void returnsContractsOfContributor(){
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(contributor.username()).thenReturn("john");
        Mockito.when(contributor.provider()).thenReturn(Provider.Names.GITHUB);
        final Contracts ofProject = new SelfJooq(new H2Database()).contracts()
            .ofContributor(contributor);
        MatcherAssert.assertThat(ofProject, Matchers.iterableWithSize(3));
    }

}
