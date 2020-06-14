-- INSERT USERS
INSERT INTO self_xdsd.slf_users_xdsd
(username, provider, email, access_token)
VALUES ('vlad', 'github', 'vlad@example.com', 'vladgh123token');
INSERT INTO self_xdsd.slf_users_xdsd
(username, provider, email, access_token)
VALUES ('mihai', 'gitlab', 'mihai@example.com', 'mihaigl123token');
INSERT INTO self_xdsd.slf_users_xdsd
(username, provider, email, access_token)
VALUES ('amihaiemil', 'github', 'amihaiemil@gmail.com.com', 'amihaigh123token');

-----------------
-- INSERT PROJECT MANAGERS

INSERT INTO self_xdsd.slf_pms_xdsd
(username, provider, access_token)
VALUES ('zoeself', 'github', 'pm1ghtoken123');

-- INSERT PROJECTS

INSERT INTO self_xdsd.slf_projects_xdsd
(repo_fullname, provider, username, pmid, webhook_token)
VALUES ('amihaiemil/docker-java-api', 'github', 'amihaiemil', 1, 'whtoken123');

INSERT INTO self_xdsd.slf_projects_xdsd
(repo_fullname, provider, username, pmid, webhook_token)
VALUES ('amihaiemil/amihaiemil.github.io', 'github', 'amihaiemil', 1, 'whtoken124');

INSERT INTO self_xdsd.slf_projects_xdsd
(repo_fullname, provider, username, pmid, webhook_token)
VALUES ('vlad/test', 'github', 'vlad', 1, 'whtoken125');

INSERT INTO self_xdsd.slf_projects_xdsd
(repo_fullname, provider, username, pmid, webhook_token)
VALUES ('mihai/test', 'gitlab', 'mihai', 1, 'whtoken126');

-- INSERT CONTRIBUTORS

INSERT INTO self_xdsd.slf_contributors_xdsd (username, provider) VALUES ('john', 'github');
INSERT INTO self_xdsd.slf_contributors_xdsd (username, provider) VALUES ('maria', 'github');
INSERT INTO self_xdsd.slf_contributors_xdsd (username, provider) VALUES ('alexandra', 'github');
INSERT INTO self_xdsd.slf_contributors_xdsd (username, provider) VALUES ('dmarkov', 'github');
INSERT INTO self_xdsd.slf_contributors_xdsd (username, provider) VALUES ('bob', 'github');

-- INSERT CONTRACTS

INSERT INTO self_xdsd.slf_contracts_xdsd
(repo_fullname, username, provider, role, hourly_rate)
VALUES ('amihaiemil/docker-java-api', 'john', 'github', 'DEV', 10000);

INSERT INTO self_xdsd.slf_contracts_xdsd
(repo_fullname, username, provider, role, hourly_rate)
VALUES ('amihaiemil/docker-java-api', 'john', 'github', 'REV', 6000);

INSERT INTO self_xdsd.slf_contracts_xdsd
(repo_fullname, username, provider, role, hourly_rate)
VALUES ('amihaiemil/docker-java-api', 'alexandra', 'github', 'DEV', 15000);

INSERT INTO self_xdsd.slf_contracts_xdsd
(repo_fullname, username, provider, role, hourly_rate)
VALUES ('amihaiemil/docker-java-api', 'maria', 'github', 'REV', 8000);

INSERT INTO self_xdsd.slf_contracts_xdsd
(repo_fullname, username, provider, role, hourly_rate)
VALUES ('vlad/test', 'maria', 'github', 'DEV', 16000);

INSERT INTO self_xdsd.slf_contracts_xdsd
(repo_fullname, username, provider, role, hourly_rate)
VALUES ('vlad/test', 'maria', 'github', 'REV', 8000);

-- INSERT TASKS
INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role)
VALUES
('vlad/test', '123', 'github', 'DEV');

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role)
VALUES
('vlad/test', '124', 'github', 'DEV');

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, username, assigned, deadline)
VALUES
('amihaiemil/docker-java-api', '123', 'github', 'DEV', 'alexandra', '2020-06-07', '2020-06-17');

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, username, assigned, deadline)
VALUES
('amihaiemil/docker-java-api', '124', 'github', 'DEV', 'alexandra', '2020-06-14', '2020-06-24');

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, username, assigned, deadline)
VALUES
('amihaiemil/docker-java-api', '125', 'github', 'DEV', 'alexandra', '2020-06-14', '2020-06-24');


INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, username, assigned, deadline)
VALUES
('amihaiemil/docker-java-api', '343', 'github', 'DEV', 'john', '2020-06-07', '2020-06-17');

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, username, assigned, deadline)
VALUES
('amihaiemil/docker-java-api', '223', 'github', 'REV', 'john', '2020-06-07', '2020-06-17');

