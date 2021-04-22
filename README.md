## Self's Storage Implementation

[![Build Status](https://travis-ci.org/self-xdsd/self-storage.svg?branch=master)](https://travis-ci.org/self-xdsd/self-storage)
[![Coverage Status](https://coveralls.io/repos/github/self-xdsd/self-storage/badge.svg?branch=master)](https://coveralls.io/github/self-xdsd/self-storage?branch=master)

[![Managed By Self XDSD](https://amihaiemil.com/images/mbself.svg)](#)
[![DevOps By Rultor.com](http://www.rultor.com/b/self-xdsd/self-storage)](http://www.rultor.com/p/self-xdsd/self-storage)
[![We recommend IntelliJ IDEA](http://amihaiemil.github.io/images/intellij-idea-recommend.svg)](https://www.jetbrains.com/idea/)

Self's Storage module, version `0.0.69`.

## Contributing 

If you would like to contribute, just open an issue or a PR.

You will need Java 11.
Make sure the maven build:

``$mvn clean install -Pcheckstyle,itcases``

passes before making a PR. [Checkstyle](http://checkstyle.sourceforge.net/) will make sure
you're following our code style and guidelines.

It's better to make changes on a separate branch (derived from ``master``), so you won't have to cherry pick commits in case your PR is rejected.

## Maven Settings

This project depends on jars from Self's Github Packages server. This server
requires authentication so, in order for Maven to be able to fetch dependencies,
you need to specify your credentials in Maven's ``settings.xml``:

```xml
<settings>
    ...
    <servers>
        <server>
            <id>github</id>
            <username>yourGithubUsername</username>
            <password>your_github_token</password>
        </server>
    </servers>
</settings>
```

Make sure you [generate](https://github.com/settings/tokens) a token with the appropriate
permissions. The ``settings.xml`` file usually resides on your computer at ``${user.home}/.m2/settings.xml``.
If the file is not there, you can create it.

## LICENSE

This product's code is open source. However, the [LICENSE](https://github.com/self-xdsd/self-core/blob/master/LICENSE) only allows you to read the code. Copying, downloading or forking the repo is strictly forbidden unless you are one of the project's contributors.
