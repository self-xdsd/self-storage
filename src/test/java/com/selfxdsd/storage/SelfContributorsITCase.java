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
import com.selfxdsd.api.storage.Paged;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jooq.Record;
import org.jooq.Result;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.stream.IntStream;

import static com.selfxdsd.storage.generated.jooq.Tables.SLF_CONTRIBUTORS_XDSD;

/**
 * Integration tests for {@link SelfContributors}.
 * Read the package-info.java if you want to run these tests manually.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class SelfContributorsITCase {

    /**
     * SelfContributors can register a new contributor.
     */
    @Test
    public void registersNewContributor() {
        final H2Database testdb = new H2Database();
        final Contributors contributors = new SelfJooq(testdb).contributors();
        final Contributor registered = contributors.register(
            "amihaiemil", Provider.Names.GITHUB
        );
        MatcherAssert.assertThat(registered, Matchers.notNullValue());
        MatcherAssert.assertThat(
            registered.username(),
            Matchers.equalTo("amihaiemil")
        );
        MatcherAssert.assertThat(
            registered.provider(),
            Matchers.equalTo(Provider.Names.GITHUB)
        );

        final Result<Record> result = testdb.connect().jooq().select()
            .from(SLF_CONTRIBUTORS_XDSD)
            .where(
                SLF_CONTRIBUTORS_XDSD.USERNAME.eq("amihaiemil").and(
                    SLF_CONTRIBUTORS_XDSD.PROVIDER.eq(
                        Provider.Names.GITHUB
                    )
                )
            ).fetch();
        MatcherAssert.assertThat(result.isEmpty(), Matchers.is(false));

        //clean up
        testdb.connect().jooq()
            .delete(SLF_CONTRIBUTORS_XDSD)
            .where(SLF_CONTRIBUTORS_XDSD.USERNAME.eq("amihaiemil"))
            .execute();
    }

    /**
     * SelfContributors can return an existing contributor.
     */
    @Test
    public void returnsFoundContributor() {
        final Contributors contributors = new SelfJooq(
            new H2Database()
        ).contributors();
        final Contributor found = contributors.getById(
            "john", Provider.Names.GITHUB
        );
        MatcherAssert.assertThat(found, Matchers.notNullValue());
        MatcherAssert.assertThat(
            found.username(),
            Matchers.equalTo("john")
        );
        MatcherAssert.assertThat(
            found.provider(),
            Matchers.equalTo(Provider.Names.GITHUB)
        );
    }

    /**
     * SelfContributors returns null if the contributor is missing.
     */
    @Test
    public void returnsNullOnMissingContributor() {
        final Contributors contributors = new SelfJooq(
            new H2Database()
        ).contributors();
        final Contributor missing = contributors.getById(
            "missing", Provider.Names.GITHUB
        );
        MatcherAssert.assertThat(missing, Matchers.nullValue());
    }

    /**
     * SelfContributors can be iterated.
     */
    @Test
    public void canBeIterated() {
        final Contributors contributors = new SelfJooq(
            new H2Database()
        ).contributors();
        MatcherAssert.assertThat(
            contributors,
            Matchers.iterableWithSize(
                Matchers.greaterThan(0)
            )
        );
    }

    /**
     * SelfContributors should throw an exception when
     * trying to elect a Contributor.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void cannotElect() {
        final Contributors contributors = new SelfJooq(
            new H2Database()
        ).contributors();
        contributors.elect(Mockito.mock(Task.class));
    }

    /**
     * SelfContributors can return a Project's Contributors.
     */
    @Test
    public void returnsProjectContributors() {
        final Contributors contributors = new SelfJooq(
            new H2Database()
        ).contributors();
        final Contributors ofProject = contributors.ofProject(
            "amihaiemil/docker-java-api", Provider.Names.GITHUB
        );
        MatcherAssert.assertThat(
            ofProject,
            Matchers.iterableWithSize(
                Matchers.greaterThanOrEqualTo(3)
            )
        );
        for(final Contributor found : ofProject) {
            MatcherAssert.assertThat(
                found.provider(),
                Matchers.equalTo(Provider.Names.GITHUB)
            );
            for(final Contract contract : found.contracts()) {
                final Project project = contract.project();
                MatcherAssert.assertThat(
                    project.repoFullName(),
                    Matchers.equalTo("amihaiemil/docker-java-api")
                );
                MatcherAssert.assertThat(
                    project.provider(),
                    Matchers.equalTo(Provider.Names.GITHUB)
                );
            }
        }
    }

    /**
     * SelfContributors returns an empty Contributors if the Project
     * has no contributors.
     */
    @Test
    public void projectContributorsNotFound() {
        final Contributors contributors = new SelfJooq(
            new H2Database()
        ).contributors();
        final Contributors ofProject = contributors.ofProject(
            "john/missing", Provider.Names.GITLAB
        );
        MatcherAssert.assertThat(
            ofProject,
            Matchers.allOf(
                Matchers.iterableWithSize(0),
                Matchers.instanceOf(SelfContributors.EmptyContributors.class)
            )
        );
        MatcherAssert.assertThat(
            ofProject.totalPages(),
            Matchers.is(1)
        );
    }

    /**
     * Empty contributors throws ISE when page is larger than 1,
     * due bounds applied on total records relative to page number and size.
     */
    @Test(expected = IllegalStateException.class)
    public void emptyContributorsThrowsWhenPaging() {
        final Contributors contributors = new SelfJooq(
            new H2Database()
        ).contributors();
        contributors.ofProject(
            "john/missing", Provider.Names.GITLAB
        ).page(new Paged.Page(2, 10));
    }

    /**
     * SelfContributors can be iterated by page.
     */
    @Test
    public void canBeIteratedByPage(){
        final H2Database database = new H2Database();
        final SelfJooq jooq = new SelfJooq(database);
        final Contributors contributors = jooq.contributors();
        IntStream
            .rangeClosed(1, 50).mapToObj(i -> "user" + i)
            .forEach(user -> contributors.register(user, "github"));
        MatcherAssert.assertThat(contributors,
            Matchers.iterableWithSize(50 + 6));
        MatcherAssert.assertThat(contributors.totalPages(),
            Matchers.is(1));

        final Contributors page = contributors.page(new Paged.Page(1, 10));
        MatcherAssert.assertThat(page,
            Matchers.iterableWithSize(10));
        MatcherAssert.assertThat(page.totalPages(),
            Matchers.is(5 + 1));

        //cleanup
        database.connect().jooq()
            .delete(SLF_CONTRIBUTORS_XDSD)
            .where(SLF_CONTRIBUTORS_XDSD.USERNAME.like("user%"))
            .execute();
    }

    /**
     * SelfContributors can return an existing contributor in page.
     */
    @Test
    public void returnsFoundContributorInPage() {
        final Contributors contributors = new SelfJooq(
            new H2Database()
        ).contributors().page(new Paged.Page(1, 10));
        final Contributor found = contributors.getById(
            "john", Provider.Names.GITHUB
        );
        MatcherAssert.assertThat(found, Matchers.notNullValue());
        MatcherAssert.assertThat(
            found.username(),
            Matchers.equalTo("john")
        );
        MatcherAssert.assertThat(
            found.provider(),
            Matchers.equalTo(Provider.Names.GITHUB)
        );
    }

    /**
     * SelfContributors returns null if the contributor is missing in page,
     * even though the contributor might exist in table.
     */
    @Test
    public void returnsNullOnMissingContributorInPage() {
        final H2Database database = new H2Database();
        final SelfJooq jooq = new SelfJooq(database);
        final Contributors contributors = jooq.contributors();
        IntStream
            .rangeClosed(1, 50).mapToObj(i -> "user" + i)
            .forEach(user -> contributors.register(user, "github"));

        final Contributor found = contributors
            .getById("john", Provider.Names.GITHUB);
        MatcherAssert.assertThat("John should be found globally",
            found, Matchers.notNullValue());

        final Contributor missing = contributors
            .page(new Paged.Page(2, 10))
            .getById("john", Provider.Names.GITHUB);
        MatcherAssert.assertThat(missing, Matchers.nullValue());

        //cleanup
        database.connect().jooq()
            .delete(SLF_CONTRIBUTORS_XDSD)
            .where(SLF_CONTRIBUTORS_XDSD.USERNAME.like("user%"))
            .execute();
    }

    /**
     * SelfContributors can return a Project's Contributors in page.
     */
    @Test
    public void returnsProjectContributorsInPage() {
        final Contributors contributors = new SelfJooq(new H2Database())
            .contributors();

        Contributors ofProject = contributors
            .ofProject("amihaiemil/docker-java-api",
                Provider.Names.GITHUB);

        final Contributors ofProjectPageOne = ofProject
            .page(new Paged.Page(1, 2));
        MatcherAssert.assertThat(
            ofProjectPageOne,
            Matchers.iterableWithSize(2)
        );
        MatcherAssert.assertThat(
            ofProjectPageOne.totalPages(),
            Matchers.is(2)
        );
        for(final Contributor found : ofProjectPageOne) {
            MatcherAssert.assertThat(
                found.provider(),
                Matchers.equalTo(Provider.Names.GITHUB)
            );
            for(final Contract contract : found.contracts()) {
                final Project project = contract.project();
                MatcherAssert.assertThat(
                    project.repoFullName(),
                    Matchers.equalTo("amihaiemil/docker-java-api")
                );
                MatcherAssert.assertThat(
                    project.provider(),
                    Matchers.equalTo(Provider.Names.GITHUB)
                );
            }
        }

        final Contributors ofProjectPageTwo = ofProject
            .page(new Paged.Page(2, 2));
        MatcherAssert.assertThat(
            ofProjectPageTwo,
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            ofProjectPageTwo.totalPages(),
            Matchers.is(2)
        );
    }

    /**
     * SelfContributors can return a Project's Contributors of a page of
     * distinct contributors or empty if project is not found in that page.
     */
    @Test
    public void returnsProjectContributorsOfPage() {

        final Contributors contributors = new SelfJooq(new H2Database())
            .contributors();

        MatcherAssert.assertThat(contributors
                .page(new Paged.Page(1, 3))
                .ofProject("amihaiemil/docker-java-api",
                    Provider.Names.GITHUB),
            Matchers.iterableWithSize(3));

        MatcherAssert.assertThat(contributors
                .page(new Paged.Page(2, 3))
                .ofProject("amihaiemil/docker-java-api",
                    Provider.Names.GITHUB),
            Matchers.emptyIterable());

    }

    /**
     * SelfContributors can return a Provider's Contributors.
     */
    @Test
    public void returnsContributorsOfProvider(){
        final Contributors contributors = new SelfJooq(new H2Database())
            .contributors()
            .ofProvider(Provider.Names.GITHUB);

        MatcherAssert.assertThat(contributors, Matchers
            .iterableWithSize(Matchers.greaterThanOrEqualTo(5)));

        MatcherAssert.assertThat(contributors
            .page(new Paged.Page(1, 3)), Matchers
            .iterableWithSize(3));
    }
}
