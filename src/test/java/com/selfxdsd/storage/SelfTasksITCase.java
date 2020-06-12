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
 * Integration tests for {@link SelfTasks}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class SelfTasksITCase {

    /**
     * SelfTasks can return an existing assigned task.
     */
    @Test
    public void returnsFoundAssignedTask() {
        final Tasks all = new SelfJooq(new H2Database()).tasks();
        final Task found = all.getById(
            "123",
            "amihaiemil/docker-java-api",
            Provider.Names.GITHUB
        );
        MatcherAssert.assertThat(
            found,
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            found.project().repoFullName(),
            Matchers.equalTo("amihaiemil/docker-java-api")
        );
        MatcherAssert.assertThat(
            found.role(), Matchers.equalTo(Contract.Roles.DEV)
        );
    }

    /**
     * SelfTasks can return an existing unassigned task.
     */
    @Test
    public void returnsFoundUnassignedTask() {
        final Tasks all = new SelfJooq(new H2Database()).tasks();
        final Task found = all.getById(
            "123",
            "vlad/test",
            Provider.Names.GITHUB
        );
        MatcherAssert.assertThat(
            found,
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            found.project().repoFullName(),
            Matchers.equalTo("vlad/test")
        );
        MatcherAssert.assertThat(
            found.role(), Matchers.equalTo(Contract.Roles.DEV)
        );
        MatcherAssert.assertThat(
            found.assignee(), Matchers.nullValue()
        );
    }

    /**
     * SelfTasks returns null if the task is not found.
     */
    @Test
    public void returnsNullForMissingTask() {
        final Tasks all = new SelfJooq(new H2Database()).tasks();
        final Task found = all.getById(
            "123",
            "amihaiemil/missing",
            Provider.Names.GITHUB
        );
        MatcherAssert.assertThat(found, Matchers.nullValue());
    }

    /**
     * SelfTasks can return the Tasks registered in a certain
     * Project.
     */
    @Test
    public void returnsProjectTasks() {
        final Tasks all = new SelfJooq(new H2Database()).tasks();
        final Tasks ofProject = all.ofProject(
            "amihaiemil/docker-java-api",
            Provider.Names.GITHUB
        );
        MatcherAssert.assertThat(
            ofProject,
            Matchers.iterableWithSize(3)
        );
        for(final Task task : ofProject) {
            final Project project = task.project();
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

    /**
     * SelfTasks can return the Tasks assigned to a Contributor.
     */
    @Test
    public void returnsContributorTasks() {
        final Tasks all = new SelfJooq(new H2Database()).tasks();
        final Tasks ofContributor = all.ofContributor(
            "john", Provider.Names.GITHUB
        );
        MatcherAssert.assertThat(
            ofContributor,
            Matchers.iterableWithSize(2)
        );
        for(final Task task : ofContributor) {
            final Contributor contributor = task.assignee();
            MatcherAssert.assertThat(
                contributor.username(),
                Matchers.equalTo("john")
            );
            MatcherAssert.assertThat(
                contributor.provider(),
                Matchers.equalTo(Provider.Names.GITHUB)
            );
        }
    }

    /**
     * SelfTasks.ofProject returns an empty iterable if the specified
     * Project has no registered tasks.
     */
    @Test
    public void returnsEmptyProjectTasks() {
        final Tasks all = new SelfJooq(new H2Database()).tasks();
        MatcherAssert.assertThat(
            all.ofProject("john/missing", Provider.Names.GITHUB),
            Matchers.emptyIterable()
        );
    }

    /**
     * SelfTasks.ofContributor returns an empty iterable if the specified
     * Contributor has no tasks assigned.
     */
    @Test
    public void returnsEmptyContributorTasks() {
        final Tasks all = new SelfJooq(new H2Database()).tasks();
        MatcherAssert.assertThat(
            all.ofContributor("dmarkov", Provider.Names.GITHUB),
            Matchers.emptyIterable()
        );
    }

    /**
     * SelfTasks should complain if we try to register an Issue
     * which doesn't correspond to any Project.
     */
    @Test (expected = IllegalStateException.class)
    public void doesNotRegisterIssueWithoutProject() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.repoFullName()).thenReturn("ana/missing");
        Mockito.when(issue.provider()).thenReturn(Provider.Names.GITHUB);
        final Tasks all = new SelfJooq(new H2Database()).tasks();
        all.register(issue);
    }

    /**
     * SelfTasks can register a new Issue.
     */
    @Test
    public void registersNewIssue() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.repoFullName()).thenReturn("mihai/test");
        Mockito.when(issue.provider()).thenReturn(Provider.Names.GITLAB);
        Mockito.when(issue.issueId()).thenReturn("234");
        Mockito.when(issue.role()).thenReturn(Contract.Roles.DEV);
        final Tasks all = new SelfJooq(new H2Database()).tasks();

        MatcherAssert.assertThat(
            all.getById("234", "mihai/test", Provider.Names.GITLAB),
            Matchers.nullValue()
        );

        final Task registered = all.register(issue);

        MatcherAssert.assertThat(
            registered.role(),
            Matchers.equalTo(Contract.Roles.DEV)
        );
        MatcherAssert.assertThat(
            all.getById("234", "mihai/test", Provider.Names.GITLAB),
            Matchers.notNullValue()
        );
    }

    /**
     * SelfTasks can be iterated.
     */
    @Test
    public void canBeIterated() {
        final Tasks all = new SelfJooq(new H2Database()).tasks();
        MatcherAssert.assertThat(
            all,
            Matchers.iterableWithSize(
                Matchers.greaterThanOrEqualTo(5)
            )
        );
        for(final Task task : all) {
            MatcherAssert.assertThat(task.project(), Matchers.notNullValue());
            MatcherAssert.assertThat(task.role(), Matchers.notNullValue());
        }
    }

}
