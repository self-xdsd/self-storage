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
(provider, access_token)
VALUES ('github', 'pm1ghtoken123');

-- INSERT PROJECTS

INSERT INTO self_xdsd.slf_projects_xdsd
(repo_fullname, provider, username, pmid)
VALUES ('amihaiemil/docker-java-api', 'github', 'amihaiemil', 1);
