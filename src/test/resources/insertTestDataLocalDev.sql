-- TEST DATA FOR DEVELOPMENT --

-- INSERT USERS
INSERT INTO `self_xdsd`.`slf_users_xdsd`
(`username`, `provider`, `role`, `email`)
VALUES ('amihaiemil', 'github', 'user', 'amihaiemil@gmail.com');

INSERT INTO `self_xdsd`.`slf_users_xdsd`
(`username`, `provider`, `role`, `email`)
VALUES ('Maiorusergiu', 'github', 'user', 'Maiorusergiu@test.com');

-- INSERT PROJECT MANAGERS

INSERT INTO `self_xdsd`.`slf_pms_xdsd`
(`userid`, `username`, `provider`, `access_token`, `commission`, `contributorCommission`)
VALUES ('33162107', 'zoeself', 'github', 'pm1ghtoken123', 6.5, 5.0);

-- INSERT PROJECTS

INSERT INTO `self_xdsd`.`slf_projects_xdsd`
(`repo_fullname`, `provider`, `username`, `pmid`, `webhook_token`)
VALUES ('amihaiemil/docker-java-api', 'github', 'amihaiemil', 1, 'whtoken123');

INSERT INTO `self_xdsd`.`slf_projects_xdsd`
(`repo_fullname`, `provider`, `username`, `pmid`, `webhook_token`)
VALUES ('amihaiemil/amihaiemil.github.io', 'github', 'amihaiemil', 1, 'whtoken124');

INSERT INTO `self_xdsd`.`slf_projects_xdsd`
(`repo_fullname`, `provider`, `username`, `pmid`, `webhook_token`)
VALUES ('amihaiemil/to_rename', 'github', 'amihaiemil', 1, 'whtoken1345345345');

INSERT INTO `self_xdsd`.`slf_projects_xdsd`
(`repo_fullname`, `provider`, `username`, `pmid`, `webhook_token`)
VALUES ('Maiorusergiu/repoTest1', 'github', 'Maiorusergiu', 1, 'whtoken1345115345');

INSERT INTO `self_xdsd`.`slf_projects_xdsd`
(`repo_fullname`, `provider`, `username`, `pmid`, `webhook_token`)
VALUES ('Maiorusergiu/repoTest2', 'github', 'Maiorusergiu', 1, 'whtok3n1345345346');

-- INSERT CONTRIBUTORS

INSERT INTO `self_xdsd`.`slf_contributors_xdsd` (`username`, `provider`) VALUES ('john', 'github');
INSERT INTO `self_xdsd`.`slf_contributors_xdsd` (`username`, `provider`) VALUES ('maria', 'github');
INSERT INTO `self_xdsd`.`slf_contributors_xdsd` (`username`, `provider`) VALUES ('alexandra', 'github');
INSERT INTO `self_xdsd`.`slf_contributors_xdsd` (`username`, `provider`) VALUES ('dmarkov', 'github');
INSERT INTO `self_xdsd`.`slf_contributors_xdsd` (`username`, `provider`) VALUES ('bob', 'github');
INSERT INTO `self_xdsd`.`slf_contributors_xdsd` (`username`, `provider`) VALUES ('john', 'gitlab');
INSERT INTO `self_xdsd`.`slf_contributors_xdsd` (`username`, `provider`) VALUES ('amihaiemil', 'github');
INSERT INTO `self_xdsd`.`slf_contributors_xdsd` (`username`, `provider`) VALUES ('Maiorusergiu', 'github');

-- INSERT CONTRACTS

INSERT INTO `self_xdsd`.`slf_contracts_xdsd`
(`repo_fullname`, `username`, `provider`, `role`, `hourly_rate`)
VALUES ('amihaiemil/docker-java-api', 'john', 'github', 'DEV', 10000);

INSERT INTO `self_xdsd`.`slf_contracts_xdsd`
(`repo_fullname`, `username`, `provider`, `role`, `hourly_rate`)
VALUES ('amihaiemil/docker-java-api', 'john', 'github', 'REV', 6000);

INSERT INTO `self_xdsd`.`slf_contracts_xdsd`
(`repo_fullname`, `username`, `provider`, `role`, `hourly_rate`)
VALUES ('amihaiemil/docker-java-api', 'alexandra', 'github', 'DEV', 15000);

INSERT INTO `self_xdsd`.`slf_contracts_xdsd`
(`repo_fullname`, `username`, `provider`, `role`, `hourly_rate`)
VALUES ('amihaiemil/docker-java-api', 'maria', 'github', 'REV', 8000);

INSERT INTO `self_xdsd`.`slf_contracts_xdsd`
(`repo_fullname`, `username`, `provider`, `role`, `hourly_rate`)
VALUES ('amihaiemil/to_rename', 'john', 'github', 'DEV', 8000);

INSERT INTO `self_xdsd`.`slf_contracts_xdsd`
(`repo_fullname`, `username`, `provider`, `role`, `hourly_rate`)
VALUES ('Maiorusergiu/repoTest1', 'amihaiemil', 'github', 'DEV', 8000);

INSERT INTO `self_xdsd`.`slf_contracts_xdsd`
(`repo_fullname`, `username`, `provider`, `role`, `hourly_rate`)
VALUES ('Maiorusergiu/repoTest1', 'Maiorusergiu', 'github', 'PO', 8000);

INSERT INTO `self_xdsd`.`slf_contracts_xdsd`
(`repo_fullname`, `username`, `provider`, `role`, `hourly_rate`)
VALUES ('Maiorusergiu/repoTest2', 'amihaiemil', 'github', 'REV', 9000);

INSERT INTO `self_xdsd`.`slf_contracts_xdsd`
(`repo_fullname`, `username`, `provider`, `role`, `hourly_rate`)
VALUES ('Maiorusergiu/repoTest2', 'Maiorusergiu', 'github', 'DEV', 10000);

-- INSERT INVOICES

INSERT INTO `self_xdsd`.`slf_invoices_xdsd`
(`invoiceId`, `repo_fullname`, `username`, `provider`, `role`, `createdAt`)
VALUES
    (1, 'amihaiemil/docker-java-api', 'john', 'github', 'DEV', NOW());

INSERT INTO `self_xdsd`.`slf_invoices_xdsd`
(`invoiceId`, `repo_fullname`, `username`, `provider`, `role`, `createdAt`)
VALUES
    (2, 'amihaiemil/docker-java-api', 'maria', 'github', 'REV', NOW());

INSERT INTO `self_xdsd`.`slf_invoices_xdsd`
(`invoiceId`, `repo_fullname`, `username`, `provider`, `role`, `createdAt`)
VALUES
    (6, 'amihaiemil/to_rename', 'john', 'github', 'DEV', NOW());

INSERT INTO `self_xdsd`.`slf_invoices_xdsd`
(`invoiceId`, `repo_fullname`, `username`, `provider`, `role`, `createdAt`)
VALUES
    (7, 'Maiorusergiu/repoTest1', 'amihaiemil', 'github', 'DEV', NOW());

-- INSERT WALLETS

INSERT INTO `self_xdsd`.`slf_wallets_xdsd`
(`repo_fullname`, `provider`, `type`, `cash`, `active`, `identifier`)
VALUES
    ('amihaiemil/docker-java-api', 'github', 'FAKE', 1000000000, 0, 'fakew-1232');

INSERT INTO `self_xdsd`.`slf_wallets_xdsd`
(`repo_fullname`, `provider`, `type`, `cash`, `active`, `identifier`)
VALUES
    ('amihaiemil/docker-java-api', 'github', 'STRIPE', 10000, 1, 'stripewallet-1232');

INSERT INTO `self_xdsd`.`slf_wallets_xdsd`
(`repo_fullname`, `provider`, `type`, `cash`, `active`, `identifier`)
VALUES
    ('amihaiemil/to_rename', 'github', 'FAKE', 10000000, 0, 'fakew-435345345');

INSERT INTO `self_xdsd`.`slf_wallets_xdsd`
(`repo_fullname`, `provider`, `type`, `cash`, `active`, `identifier`)
VALUES
    ('amihaiemil/to_rename', 'github', 'STRIPE', 10000, 1, 'stripewallet-87787878');

INSERT INTO `self_xdsd`.`slf_wallets_xdsd`
(`repo_fullname`, `provider`, `type`, `cash`, `active`, `identifier`)
VALUES
    ('Maiorusergiu/repoTest1', 'github', 'FAKE', 10000000, 1, 'fakew-435345345');

INSERT INTO `self_xdsd`.`slf_wallets_xdsd`
(`repo_fullname`, `provider`, `type`, `cash`, `active`, `identifier`)
VALUES
    ('Maiorusergiu/repoTest2', 'github', 'STRIPE', 10000, 1, 'stripewallet-87787878');

-- INSERT TASKS

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, username, assigned, deadline, estimation_minutes, isPullRequest)
VALUES
    ('Maiorusergiu/repoTest1', '123', 'github', 'DEV', 'amihaiemil', '2020-06-14', '2020-06-24', 60, 0);

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, username, assigned, deadline, estimation_minutes, isPullRequest)
VALUES
    ('Maiorusergiu/repoTest1', '124', 'github', 'DEV', 'amihaiemil', '2020-06-14', '2020-06-24', 60, 0);

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, username, assigned, deadline, estimation_minutes, isPullRequest)
VALUES
    ('Maiorusergiu/repoTest1', '125', 'github', 'DEV', 'amihaiemil', '2020-06-14', '2020-06-24', 60, 0);

-- INSERT PLATFORM INVOICES

INSERT INTO `self_xdsd`.`slf_platforminvoices_xdsd`
(`id`, `createdAt`, `billedTo`, `commission`, `vat`, `transactionId`, `payment_timestamp`, `eurToRon`)
VALUES (1, '2021-01-09', 'mihai', 130, 19, 'transaction123', '2021-01-09', 487);

INSERT INTO `self_xdsd`.`slf_platforminvoices_xdsd`
(`id`, `createdAt`, `billedTo`, `commission`, `vat`, `transactionId`, `payment_timestamp`, `eurToRon`)
VALUES (2, '2021-01-10', 'mihai', 120, 19, 'transaction124', '2021-01-10', 490);

INSERT INTO `self_xdsd`.`slf_platforminvoices_xdsd`
(`id`, `createdAt`, `billedTo`, `commission`, `vat`, `transactionId`, `payment_timestamp`, `eurToRon`)
VALUES (3, '2021-01-11', 'mihai', 125, 19, 'transaction125', '2021-01-11', 510);

INSERT INTO `self_xdsd`.`slf_platforminvoices_xdsd`
(`id`, `createdAt`, `billedTo`, `commission`, `vat`, `transactionId`, `payment_timestamp`, `eurToRon`)
VALUES (4, '2021-01-12', 'mihai', 140, 19, 'transaction126', '2021-01-12', 467);

INSERT INTO `self_xdsd`.`slf_platforminvoices_xdsd`
(`id`, `createdAt`, `billedTo`, `commission`, `vat`, `transactionId`, `payment_timestamp`, `eurToRon`)
VALUES (5, '2021-01-13', 'mihai', 133, 19, 'transaction127', '2021-01-13', 523);

INSERT INTO `self_xdsd`.`slf_platforminvoices_xdsd`
(`id`, `createdAt`, `billedTo`, `commission`, `vat`, `transactionId`, `payment_timestamp`, `eurToRon`)
VALUES (6, '2021-01-14', 'mihai', 135, 19, 'transaction129', '2021-01-14', 410);