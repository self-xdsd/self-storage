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

/**
 * Integration tests for {@link SelfResignations}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.8
 */
public final class SelfResignationsITCase {

    /**
     * SelfResignations can return a Task's resignations.
     */
    @Test
    public void returnsTaskResignations() {
        final Storage storage = new SelfJooq(new H2Database());
        final Project project = storage
            .projects()
            .getProjectById("vlad/test", Provider.Names.GITHUB);
        MatcherAssert.assertThat(
            project,
            Matchers.notNullValue()
        );
        final Task task = project
            .tasks().getById("999", "vlad/test", Provider.Names.GITHUB);
        MatcherAssert.assertThat(task, Matchers.notNullValue());
        final Resignations resignations = task.resignations();
        MatcherAssert.assertThat(resignations, Matchers.iterableWithSize(2));
        for(final Resignation res : resignations) {
            MatcherAssert.assertThat(
                res.task(),
                Matchers.is(task)
            );
            MatcherAssert.assertThat(
                res.contributor().provider(),
                Matchers.is(Provider.Names.GITHUB)
            );
            MatcherAssert.assertThat(
                res.timestamp(),
                Matchers.notNullValue()
            );
            MatcherAssert.assertThat(
                res.reason(),
                Matchers.not(
                    Matchers.isEmptyOrNullString()
                )
            );
        }
    }

    /**
     * SelfResignations returns empty if the Task has no registered
     * resignations.
     */
    @Test
    public void returnsEmptyTaskResignations() {
        final Storage storage = new SelfJooq(new H2Database());
        final Project project = storage
            .projects()
            .getProjectById(
                "amihaiemil/docker-java-api",
                Provider.Names.GITHUB
            );
        MatcherAssert.assertThat(
            project,
            Matchers.notNullValue()
        );
        final Task task = project
            .tasks().getById(
                "123",
                "amihaiemil/docker-java-api",
                Provider.Names.GITHUB
            );
        MatcherAssert.assertThat(task, Matchers.notNullValue());
        final Resignations resignations = task.resignations();
        MatcherAssert.assertThat(resignations, Matchers.emptyIterable());
    }

    /**
     * SelfResignations.register(...) throws ISE if the Task has no assignee.
     */
    @Test (expected = IllegalStateException.class)
    public void registerComplainsOnNoAssignee() {
        final Storage storage = new SelfJooq(new H2Database());
        final Project project = storage
            .projects()
            .getProjectById(
                "vlad/test",
                Provider.Names.GITHUB
            );
        MatcherAssert.assertThat(
            project,
            Matchers.notNullValue()
        );
        final Task task = project
            .tasks().getById(
                "124",
                "vlad/test",
                Provider.Names.GITHUB
            );
        MatcherAssert.assertThat(task, Matchers.notNullValue());
        storage.resignations().register(task, Resignations.Reason.ASKED);
    }

    /**
     * SelfResignations.register(...) works.
     */
    @Test
    public void registerWorks() {
        final Storage storage = new SelfJooq(new H2Database());
        final Project project = storage
            .projects()
            .getProjectById(
                "amihaiemil/docker-java-api",
                Provider.Names.GITHUB
            );
        MatcherAssert.assertThat(
            project,
            Matchers.notNullValue()
        );
        final Task task = project
            .tasks().getById(
                "124",
                "amihaiemil/docker-java-api",
                Provider.Names.GITHUB
            );
        MatcherAssert.assertThat(task, Matchers.notNullValue());
        MatcherAssert.assertThat(
            task.resignations(),
            Matchers.emptyIterable()
        );
        final Resignation res = storage
            .resignations()
            .register(task, Resignations.Reason.DEADLINE);
        final Resignations resignations = task.resignations();
        MatcherAssert.assertThat(
            resignations,
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            res.task(),
            Matchers.is(task)
        );
        MatcherAssert.assertThat(
            res.contributor().username(),
            Matchers.equalTo("alexandra")
        );
        MatcherAssert.assertThat(
            res.reason(),
            Matchers.equalTo(Resignations.Reason.DEADLINE)
        );
        MatcherAssert.assertThat(
            res.timestamp(),
            Matchers.notNullValue()
        );
    }
}
