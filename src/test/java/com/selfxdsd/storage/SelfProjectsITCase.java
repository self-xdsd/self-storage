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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Integration tests for {@link SelfProjects}.
 * Read the package-info.java if you want to run these tests manually.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class SelfProjectsITCase {

    /**
     * Returns a project by its ID.
     */
    @Test
    public void getsProjectById() {
        final Projects projects = new SelfJooq(new H2Database()).projects();
        final Project found = projects.getProjectById(
            "amihaiemil/docker-java-api", Provider.Names.GITHUB
        );
        MatcherAssert.assertThat(
            found.repoFullName(),
            Matchers.equalTo("amihaiemil/docker-java-api")
        );
        MatcherAssert.assertThat(
            found.owner().username(),
            Matchers.equalTo("amihaiemil")
        );
        MatcherAssert.assertThat(
            found.projectManager().id(),
            Matchers.equalTo(1)
        );
        MatcherAssert.assertThat(
            found.projectManager().provider(),
            Matchers.equalTo(Provider.Names.GITHUB)
        );
    }

    /**
     * Returns null the project is not found.
     */
    @Test
    public void returnsNullIfProjectMissing() {
        final Projects projects = new SelfJooq(new H2Database()).projects();
        final Project found = projects.getProjectById(
            "amihaiemil/missing", Provider.Names.GITHUB
        );
        MatcherAssert.assertThat(
            found,
            Matchers.nullValue()
        );
    }

    /**
     * SelfProjects can return the Projects owned by a certain User.
     */
    @Test
    public void returnsOwnedByUser() {
        final Projects all = new SelfJooq(new H2Database()).projects();
        final Projects amihaiemil = all.ownedBy(
            this.mockUser("amihaiemil", Provider.Names.GITHUB)
        );
        MatcherAssert.assertThat(
            amihaiemil,
            Matchers.iterableWithSize(
                Matchers.greaterThanOrEqualTo(2)
            )
        );
        for(final Project project : amihaiemil) {
            final User owner = project.owner();
            MatcherAssert.assertThat(
                owner.username(),
                Matchers.equalTo("amihaiemil")
            );
            MatcherAssert.assertThat(
                owner.provider().name(),
                Matchers.equalTo(Provider.Names.GITHUB)
            );
        }
    }

    /**
     * SelfProjects can return empty Projects if the user doesn't own any
     * projects.
     */
    @Test
    public void returnsEmptyOwnedByUser() {
        final Projects all = new SelfJooq(new H2Database()).projects();
        final Projects ofThomas = all.ownedBy(
            this.mockUser("thomas", Provider.Names.GITLAB)
        );
        MatcherAssert.assertThat(ofThomas, Matchers.emptyIterable());
    }

    /**
     * SelProjects can return the Projects assigned to a certain PM.
     */
    @Test
    public void returnsAssignedProjects() {
        final Projects all = new SelfJooq(new H2Database()).projects();
        final Projects assigned = all.assignedTo(1);
        MatcherAssert.assertThat(
            assigned,
            Matchers.iterableWithSize(
                Matchers.greaterThanOrEqualTo(4)
            )
        );
        for(final Project project : assigned) {
            final ProjectManager manager = project.projectManager();
            MatcherAssert.assertThat(
                manager.id(),
                Matchers.equalTo(1)
            );
        }
    }

    /**
     * SelfProjects can return empty Projects if the specified PM
     * has no assigned projects.
     */
    @Test
    public void returnsEmptyAssignedProjects() {
        final Projects all = new SelfJooq(new H2Database()).projects();
        final Projects assigned = all.assignedTo(153);
        MatcherAssert.assertThat(assigned, Matchers.emptyIterable());
    }

    /**
     * SelfProjects throws an IAE if we try to register a Repo
     * with a missing PM.
     */
    @Test(expected = IllegalArgumentException.class)
    public void registerFailsWithNonExistingPm() {
        final Projects all = new SelfJooq(new H2Database()).projects();
        final ProjectManager missing = Mockito.mock(ProjectManager.class);
        Mockito.when(missing.id()).thenReturn(567);
        all.register(Mockito.mock(Repo.class), missing);
    }

    /**
     * SelfProjects can register a new project.
     */
    @Test
    public void registersNewProject() {
        final Projects all = new SelfJooq(new H2Database()).projects();
        MatcherAssert.assertThat(
            all.getProjectById(
                "amihaiemil/eo-jsonp-impl",
                Provider.Names.GITHUB
            ),
            Matchers.nullValue()
        );
        final Repo repo = this.mockRepo(
            "amihaiemil/eo-jsonp-impl",
            "amihaiemil",
            Provider.Names.GITHUB
        );
        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        Mockito.when(manager.id()).thenReturn(1);

        final Project registered = all.register(repo, manager);

        MatcherAssert.assertThat(
            registered.projectManager(),
            Matchers.is(manager)
        );

        MatcherAssert.assertThat(
            all.getProjectById(
                "amihaiemil/eo-jsonp-impl",
                Provider.Names.GITHUB
            ),
            Matchers.notNullValue()
        );
    }

    /**
     * SelfProjects throws an IAE if we try to register a Repo
     * with a null PM.
     */
    @Test(expected = IllegalArgumentException.class)
    public void registerFailsWithNullPm() {
        final Projects all = new SelfJooq(new H2Database()).projects();
        all.register(Mockito.mock(Repo.class), null);
    }

    /**
     * SelfProjects can be iterated.
     */
    @Test
    public void canBeIterated() {
        final Projects all = new SelfJooq(new H2Database()).projects();
        MatcherAssert.assertThat(
            all,
            Matchers.iterableWithSize(
                Matchers.greaterThanOrEqualTo(4)
            )
        );
        for(final Project project : all) {
            MatcherAssert.assertThat(
                project,
                Matchers.notNullValue()
            );
        }
    }

    /**
     * Mock a User for test.
     * @param username Username.
     * @param provider Provider.
     * @return User.
     */
    private User mockUser(final String username, final String provider) {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn(username);

        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn(provider);

        Mockito.when(user.provider()).thenReturn(prov);

        return user;
    }

    /**
     * Mock a Repo for test.
     * @param fullName Repo full name.
     * @param username Owner's username.
     * @param provider Provider.
     * @return Repo.
     */
    private Repo mockRepo(
        final String fullName,
        final String username,
        final String provider
    ) {
        final User owner = this.mockUser(username, provider);
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.owner()).thenReturn(owner);
        Mockito.when(repo.provider()).thenReturn(provider);
        Mockito.when(repo.fullName()).thenReturn(fullName);
        return repo;
    }
}
