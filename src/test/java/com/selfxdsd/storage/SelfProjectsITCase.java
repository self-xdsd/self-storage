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
import com.selfxdsd.api.storage.Paged;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jooq.exception.DataAccessException;
import org.junit.Test;
import org.mockito.Mockito;

import static com.selfxdsd.storage.generated.jooq.tables.SlfProjectsXdsd.SLF_PROJECTS_XDSD;

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
            found.projectManager().provider().name(),
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
     * Returns a project by its WebHook Token.
     */
    @Test
    public void getsProjectByWebHookToken() {
        final Projects projects = new SelfJooq(new H2Database()).projects();
        final Project found = projects.getByWebHookToken("whtoken123");
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
            found.projectManager().provider().name(),
            Matchers.equalTo(Provider.Names.GITHUB)
        );
    }

    /**
     * Returns null the project is not found by webhook token.
     */
    @Test
    public void returnsNullIfProjectNotFountByWebHookToken() {
        final Projects projects = new SelfJooq(new H2Database()).projects();
        final Project found = projects.getByWebHookToken("missing-token-123");
        MatcherAssert.assertThat(found, Matchers.nullValue());
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
        all.register(Mockito.mock(Repo.class), missing, "wh123token");
    }

    /**
     * SelfProjects can register a new project.
     */
    @Test
    public void registersNewProject() {
        final H2Database database = new H2Database();
        final Projects all = new SelfJooq(database).projects();
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

        final Project registered = all.register(repo, manager, "wh123token");

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

        //cleanup
        database.connect().jooq().delete(SLF_PROJECTS_XDSD)
            .where(SLF_PROJECTS_XDSD.REPO_FULLNAME
                .eq("amihaiemil/eo-jsonp-impl"))
            .execute();
    }

    /**
     * SelfProjects throws an IAE if we try to register a Repo
     * with a null PM.
     */
    @Test(expected = IllegalArgumentException.class)
    public void registerFailsWithNullPm() {
        final Projects all = new SelfJooq(new H2Database()).projects();
        all.register(Mockito.mock(Repo.class), null, "wh123token");
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
     * SelfProjects can be iterated by page.
     */
    @Test
    public void canBeIteratedByPage() {
        final Projects projects = new SelfJooq(new H2Database())
            .projects()
            .page(new Paged.Page(2, 2));
        MatcherAssert.assertThat(
            projects,
            Matchers.iterableWithSize(2)
        );
        for(final Project project : projects) {
            MatcherAssert.assertThat(
                project,
                Matchers.notNullValue()
            );
        }
        MatcherAssert.assertThat("When setting MAX_VALUE for page size "
                + " should iterate all records",
            projects.page(new Paged.Page(1, Integer.MAX_VALUE)),
            Matchers.iterableWithSize(
                Matchers.greaterThanOrEqualTo(4)
            )
        );
    }

    /**
     * SelfProjects page has correct info about total pages.
     */
    @Test
    public void hasCorrectTotalPages() {
        final H2Database database = new H2Database();
        final Projects projects = new SelfJooq(database).projects();
        MatcherAssert.assertThat(projects
            .page(new Paged.Page(1, 5))
            .totalPages(), Matchers.greaterThanOrEqualTo(1));
        for (int i = 0; i < 16; i++) {
            final Repo repo = this.mockRepo("amihaiemil/repo" + i,
                "amihaiemil", "github");
            final ProjectManager manager = Mockito.mock(ProjectManager.class);
            Mockito.when(manager.id()).thenReturn(1);
            projects.register(repo, manager, "wbtoken" + i);
        }
        MatcherAssert.assertThat(projects
            .page(new Paged.Page(1, 5))
            .totalPages(), Matchers.is(5));
        MatcherAssert.assertThat(projects
            .totalPages(), Matchers.is(1));

        //cleanup
        database.connect().jooq().delete(SLF_PROJECTS_XDSD).where(
            SLF_PROJECTS_XDSD.REPO_FULLNAME.like("amihaiemil/repo%")
        ).execute();
    }

    /**
     * SelfProjects can return the Projects owned by a certain User in pages
     * of Projects.
     */
    @Test
    public void returnsOwnedByUserInPage() {
        final Projects all = new SelfJooq(new H2Database()).projects();

        final Projects pageOne = all.page(new Paged.Page(1, 2));
        MatcherAssert.assertThat(pageOne, Matchers.iterableWithSize(2));
        MatcherAssert.assertThat(
            "`amihaiemil` should own 2 projects on page 1",
            pageOne.ownedBy(mockUser("amihaiemil", "github")),
            Matchers.iterableWithSize(2)
        );
        MatcherAssert.assertThat(
            "`vlad` should own no projects project on page 1",
            pageOne.ownedBy(mockUser("vlad", "github")),
            Matchers.emptyIterable()
        );

        final Projects pageTwo = all.page(new Paged.Page(2, 2));
        MatcherAssert.assertThat(pageTwo, Matchers.iterableWithSize(2));
        MatcherAssert.assertThat(
            "`amihaiemil` should own no projects on page 2",
            pageTwo.ownedBy(mockUser("amihaiemil", "github")),
            Matchers.emptyIterable()
        );
        MatcherAssert.assertThat(
            "`vlad` should own 1 project on page 2",
            pageTwo.ownedBy(mockUser("vlad", "github")),
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            "`mihai` should own 1 project on page 2",
            pageTwo.ownedBy(mockUser("mihai", "gitlab")),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * Owned by User Projects can be be paged.
     */
    @Test
    public void returnsPageOfOwnedByUser() {
        final Projects all = new SelfJooq(new H2Database()).projects();

        final Projects ofAmihaiemil = all
            .ownedBy(mockUser("amihaiemil", "github"));
        MatcherAssert.assertThat(ofAmihaiemil, Matchers.iterableWithSize(3));
        MatcherAssert.assertThat(ofAmihaiemil
            .page(new Paged.Page(1, 1)),
            Matchers.iterableWithSize(1));
        MatcherAssert.assertThat(all.page(new Paged.Page(2, 1)),
            Matchers.iterableWithSize(1));

        final Projects ofAmihaiemilSubpage = all
            .page(new Paged.Page(1, 3))
            .ownedBy(mockUser("amihaiemil", "github"));
        MatcherAssert.assertThat(ofAmihaiemilSubpage,
            Matchers.iterableWithSize(2));
        MatcherAssert.assertThat(ofAmihaiemilSubpage
                .page(new Paged.Page(1, 1)),
            Matchers.iterableWithSize(1));
        MatcherAssert.assertThat(ofAmihaiemilSubpage
                .page(new Paged.Page(2, 1)),
            Matchers.iterableWithSize(1));
    }

    /**
     * SelfProjects can return the Projects assigned to PM in pages
     * of Projects.
     */
    @Test
    public void returnsAssignedToPmInPage() {
        final Projects all = new SelfJooq(new H2Database()).projects();

        MatcherAssert.assertThat(all
                .page(new Paged.Page(1, 3))
                .assignedTo(1),
            Matchers.iterableWithSize(3));
        MatcherAssert.assertThat(all
                .page(new Paged.Page(1, 3))
                .assignedTo(4),
            Matchers.emptyIterable());

        MatcherAssert.assertThat(all
                .page(new Paged.Page(2, 3))
                .assignedTo(1),
            Matchers.iterableWithSize(2));
    }

    /**
     * SelfProjects assigned can be paged.
     */
    @Test
    public void returnsPageOfAssignedToPm() {
        final Projects all = new SelfJooq(new H2Database()).projects();
        final Projects ofZoeself = all.assignedTo(1);
        MatcherAssert.assertThat(ofZoeself,
            Matchers.iterableWithSize(5));
        MatcherAssert.assertThat(ofZoeself
                .page(new Paged.Page(1, 3)),
            Matchers.iterableWithSize(3));
        MatcherAssert.assertThat(ofZoeself
                .page(new Paged.Page(2, 3)),
            Matchers.iterableWithSize(2));

        final Projects ofZoeselfSubpage = all
            .page(new Paged.Page(1, 2))
            .assignedTo(1);
        MatcherAssert.assertThat(ofZoeselfSubpage,
            Matchers.iterableWithSize(2));
    }

    /**
     * SelfProjects can remove a Project, together with its Wallets and
     * PaymentMethods.
     */
    @Test
    public void removesProject() {
        final Storage storage = new SelfJooq(new H2Database());
        final Projects all = storage.projects();
        final Project toRemove = all.getProjectById(
            "maria/to_remove", Provider.Names.GITHUB
        );
        MatcherAssert.assertThat(toRemove, Matchers.notNullValue());
        MatcherAssert.assertThat(
            toRemove.contracts(),
            Matchers.emptyIterable()
        );
        MatcherAssert.assertThat(
            toRemove.wallets(),
            Matchers.iterableWithSize(2)
        );
        final Wallet active = toRemove.wallets().active();
        MatcherAssert.assertThat(
            active.type(),
            Matchers.equalTo("STRIPE")
        );
        MatcherAssert.assertThat(
            active.paymentMethods(),
            Matchers.iterableWithSize(2)
        );
        all.remove(toRemove);
        MatcherAssert.assertThat(
            storage.wallets().ofProject(toRemove),
            Matchers.emptyIterable()
        );
        MatcherAssert.assertThat(
            storage.paymentMethods().ofWallet(active),
            Matchers.emptyIterable()
        );
        MatcherAssert.assertThat(
            all.getProjectById(
                "maria/to_remove", Provider.Names.GITHUB
            ),
            Matchers.nullValue()
        );
        MatcherAssert.assertThat(
            storage.projectManagers().getById(3),
            Matchers.notNullValue()
        );
    }

    /**
     * We should get an exception if the Project we're trying to remove
     * still has some Contracts.
     */
    @Test (expected = DataAccessException.class)
    public void doesNotRemoveProjectWithContracts() {
        final Storage storage = new SelfJooq(new H2Database());
        final Projects all = storage.projects();
        final Project toRemove = all.getProjectById(
            "amihaiemil/docker-java-api", Provider.Names.GITHUB
        );
        MatcherAssert.assertThat(toRemove, Matchers.notNullValue());
        MatcherAssert.assertThat(
            toRemove.contracts(),
            Matchers.iterableWithSize(
                Matchers.greaterThan(0)
            )
        );
        all.remove(toRemove);
    }

    /**
     * SelfProjects.rename(...) returns the renamed Project.
     */
    @Test
    public void canRenameProject() {
        final Storage storage = new SelfJooq(new H2Database());
        final Projects all = storage.projects();
        final Project toRename = all.getProjectById(
            "amihaiemil/to_rename",
            "github"
        );
        MatcherAssert.assertThat(
            toRename.repoFullName(),
            Matchers.equalTo("amihaiemil/to_rename")
        );
        MatcherAssert.assertThat(
            toRename.contracts(),
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            toRename.tasks(),
            Matchers.iterableWithSize(2)
        );
        MatcherAssert.assertThat(
            toRename.wallets(),
            Matchers.iterableWithSize(2)
        );

        final Project renamed = all.rename(toRename, "newName");

        MatcherAssert.assertThat(
            renamed.repoFullName(),
            Matchers.equalTo("amihaiemil/newName")
        );
        MatcherAssert.assertThat(
            renamed.contracts(),
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            renamed.tasks(),
            Matchers.iterableWithSize(2)
        );
        MatcherAssert.assertThat(
            renamed.wallets(),
            Matchers.iterableWithSize(2)
        );

        final Project renamedSelected = all.getProjectById(
            "amihaiemil/newName",
            "github"
        );

        MatcherAssert.assertThat(
            renamedSelected.repoFullName(),
            Matchers.equalTo("amihaiemil/newName")
        );
        MatcherAssert.assertThat(
            renamedSelected.contracts(),
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            renamedSelected.tasks(),
            Matchers.iterableWithSize(2)
        );
        MatcherAssert.assertThat(
            renamedSelected.wallets(),
            Matchers.iterableWithSize(2)
        );
    }

    /**
     * SelfProjects.rename(...) returns null if the Project does not exist.
     */
    @Test
    public void renameReturnsNullOnMissingProject() {
        final Storage storage = new SelfJooq(new H2Database());
        final Projects all = storage.projects();
        final Project missing = Mockito.mock(Project.class);
        Mockito.when(missing.repoFullName())
            .thenReturn("mihai/missing-repo-rename");
        Mockito.when(missing.provider()).thenReturn("github");
        final Project renamed = all.rename(missing, "newName");
        MatcherAssert.assertThat(renamed, Matchers.nullValue());
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
