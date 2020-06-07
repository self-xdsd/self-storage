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
import com.selfxdsd.api.Provider;
import com.selfxdsd.api.Task;
import com.selfxdsd.api.Tasks;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration tests for {@link SelfTasks}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class SelfTasksITCase {

    /**
     * SelfTasks can return an existing task.
     */
    @Test
    public void returnsFoundTask() {
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

}
