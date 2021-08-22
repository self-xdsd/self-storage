-- INSERT USERS
INSERT INTO self_xdsd.slf_users_xdsd
(username, provider, role, email)
VALUES ('vlad', 'github', 'user', 'vlad@example.com');
INSERT INTO self_xdsd.slf_users_xdsd
(username, provider, role, email)
VALUES ('mihai', 'gitlab', 'user' ,'mihai@example.com');
INSERT INTO self_xdsd.slf_users_xdsd
(username, provider, role, email)
VALUES ('amihaiemil', 'github', 'user', 'amihaiemil@gmail.com.com');
INSERT INTO self_xdsd.slf_users_xdsd
(username, provider, role, email)
VALUES ('johndoe', 'github', 'user', 'johndoe@gmail.com');
INSERT INTO self_xdsd.slf_users_xdsd
(username, provider, role, email)
VALUES ('maria', 'github', 'user', 'maria@gmail.com');

-- INSERT API TOKENS

INSERT INTO `self_xdsd`.`slf_apitokens_xdsd`
(`token`, `expiresAt`, `name`, `username`, `provider`)
VALUES ('apiToken123', '2022-01-01', 'Mihai Token 1', 'amihaiemil', 'github');
INSERT INTO `self_xdsd`.`slf_apitokens_xdsd`
(`token`, `expiresAt`, `name`, `username`, `provider`)
VALUES ('apiToken456', '2022-01-01', 'Mihai Token 2', 'amihaiemil', 'github');
INSERT INTO `self_xdsd`.`slf_apitokens_xdsd`
(`token`, `expiresAt`, `name`, `username`, `provider`)
VALUES ('apiToken789', '2022-01-01', 'John Token 1', 'johndoe', 'github');
INSERT INTO `self_xdsd`.`slf_apitokens_xdsd` -- token to be deleted in a test
(`token`, `expiresAt`, `name`, `username`, `provider`)
VALUES ('apiToken001', '2022-01-01', 'Vlad Token 1', 'vlad', 'github');

-----------------
-- INSERT PROJECT MANAGERS

INSERT INTO self_xdsd.slf_pms_xdsd
(userid, username, provider, access_token, commission, contributorCommission)
VALUES ('33162107', 'zoeself', 'github', 'pm1ghtoken123', 6.5, 5.0);

INSERT INTO self_xdsd.slf_pms_xdsd
(userid, username, provider, access_token, commission, contributorCommission)
VALUES ('33162108', 'otherpm', 'github', 'pm1ghtoken124', 6.5, 5.0);

INSERT INTO self_xdsd.slf_pms_xdsd
(userid, username, provider, access_token, commission, contributorCommission)
VALUES ('33162999', 'thirdpm', 'github', 'pm1ghtoken125', 8.0, 5.0);


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

INSERT INTO self_xdsd.slf_projects_xdsd
(repo_fullname, provider, username, pmid, webhook_token)
VALUES ('johndoe/stripe_repo', 'github', 'johndoe', 2, 'whtoken124');

INSERT INTO self_xdsd.slf_projects_xdsd
(repo_fullname, provider, username, pmid, webhook_token)
VALUES ('maria/to_remove', 'github', 'maria', 3, 'whtoken12888');

INSERT INTO self_xdsd.slf_projects_xdsd
(repo_fullname, provider, username, pmid, webhook_token)
VALUES ('amihaiemil/to_rename', 'github', 'amihaiemil', 1, 'whtoken1345345345');

-- INSERT CONTRIBUTORS

INSERT INTO self_xdsd.slf_contributors_xdsd (username, provider) VALUES ('john', 'github');
INSERT INTO self_xdsd.slf_contributors_xdsd (username, provider) VALUES ('maria', 'github');
INSERT INTO self_xdsd.slf_contributors_xdsd (username, provider) VALUES ('alexandra', 'github');
INSERT INTO self_xdsd.slf_contributors_xdsd (username, provider) VALUES ('dmarkov', 'github');
INSERT INTO self_xdsd.slf_contributors_xdsd (username, provider) VALUES ('bob', 'github');
INSERT INTO self_xdsd.slf_contributors_xdsd (username, provider) VALUES ('john', 'gitlab');

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
VALUES ('vlad/test', 'john', 'github', 'DEV', 16000);

INSERT INTO self_xdsd.slf_contracts_xdsd
(repo_fullname, username, provider, role, hourly_rate)
VALUES ('vlad/test', 'bob', 'github', 'DEV', 16000);

INSERT INTO self_xdsd.slf_contracts_xdsd
(repo_fullname, username, provider, role, hourly_rate)
VALUES ('vlad/test', 'maria', 'github', 'REV', 8000);

INSERT INTO self_xdsd.slf_contracts_xdsd
(repo_fullname, username, provider, role, hourly_rate)
VALUES ('vlad/test', 'maria', 'github', 'QA', 8000);

INSERT INTO self_xdsd.slf_contracts_xdsd
(repo_fullname, username, provider, role, hourly_rate)
VALUES ('vlad/test', 'alexandra', 'github', 'DEV', 8000);

INSERT INTO self_xdsd.slf_contracts_xdsd -- this Contract is for testing SelfContracts.remove(...). Don't use it in other tests.
(repo_fullname, username, provider, role, hourly_rate, markedForRemoval)
VALUES ('vlad/test', 'maria', 'github', 'PO', 10000, '2020-10-10');

INSERT INTO self_xdsd.slf_contracts_xdsd
(repo_fullname, username, provider, role, hourly_rate)
VALUES ('amihaiemil/to_rename', 'john', 'github', 'DEV', 8000);

-- INSERT TASKS
INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, estimation_minutes, isPullRequest)
VALUES
('maria/to_remove', '123', 'github', 'DEV', 60, 0);
INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, estimation_minutes, isPullRequest)
VALUES
('vlad/test', '123', 'github', 'DEV', 60, 0);

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, estimation_minutes, isPullRequest)
VALUES
('vlad/test', '124', 'github', 'DEV', 60, 0);

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, username, assigned, deadline, estimation_minutes, isPullRequest)
VALUES
('amihaiemil/docker-java-api', '123', 'github', 'DEV', 'alexandra', '2020-06-07', '2020-06-17', 60, 0);

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, username, assigned, deadline, estimation_minutes, isPullRequest)
VALUES
('amihaiemil/docker-java-api', '124', 'github', 'DEV', 'alexandra', '2020-06-14', '2020-06-24', 60, 0);

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, username, assigned, deadline, estimation_minutes, isPullRequest)
VALUES
('amihaiemil/docker-java-api', '125', 'github', 'DEV', 'alexandra', '2020-06-14', '2020-06-24', 60, 0);

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, username, assigned, deadline, estimation_minutes, isPullRequest)
VALUES
('amihaiemil/docker-java-api', '126', 'github', 'DEV', 'alexandra', '2020-12-14', '2020-12-24', 60, 0);


INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, username, assigned, deadline, estimation_minutes, isPullRequest)
VALUES
('amihaiemil/docker-java-api', '343', 'github', 'DEV', 'john', '2020-06-07', '2020-06-17', 60, 0);

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, username, assigned, deadline, estimation_minutes, isPullRequest)
VALUES
('amihaiemil/docker-java-api', '223', 'github', 'REV', 'john', '2020-06-07', '2020-06-17', 60, 0);

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, username, assigned, deadline, estimation_minutes, isPullRequest)
VALUES
('vlad/test', '887', 'github', 'QA', 'maria', '2020-08-25', '2020-09-04', 60, 0);

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, username, assigned, deadline, estimation_minutes, isPullRequest)
VALUES
('vlad/test', '900', 'github', 'QA', 'maria', '2020-08-25', '2020-09-04', 60, 0);

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, estimation_minutes, isPullRequest)
VALUES
('vlad/test', '901', 'github', 'DEV', 60, 0);

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, estimation_minutes, isPullRequest)
VALUES
('vlad/test', '999', 'github', 'DEV', 60, 0);

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, estimation_minutes, isPullRequest)
VALUES
('vlad/test', '1000', 'github', 'DEV', 60, 1);

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, estimation_minutes, isPullRequest)
VALUES
('amihaiemil/to_rename', '1000', 'github', 'DEV', 60, 0);

INSERT INTO self_xdsd.slf_tasks_xdsd
(repo_fullname, issueId, provider, role, estimation_minutes, isPullRequest)
VALUES
('amihaiemil/to_rename', '1001', 'github', 'DEV', 60, 0);

INSERT INTO `self_xdsd`.`slf_invoices_xdsd`
(invoiceId, repo_fullname, username, provider, role, createdAt)
VALUES
(1, 'amihaiemil/docker-java-api', 'john', 'github', 'DEV', NOW());

INSERT INTO `self_xdsd`.`slf_invoices_xdsd`
(invoiceId, repo_fullname, username, provider, role, createdAt)
VALUES
(2, 'amihaiemil/docker-java-api', 'maria', 'github', 'REV', NOW());

INSERT INTO `self_xdsd`.`slf_invoices_xdsd`
(invoiceId, repo_fullname, username, provider, role, createdAt)
VALUES
(3, 'vlad/test', 'maria', 'github', 'PO', NOW());

INSERT INTO `self_xdsd`.`slf_invoices_xdsd`
(invoiceId, repo_fullname, username, provider, role, createdAt)
VALUES
(4, 'vlad/test', 'alexandra', 'github', 'DEV', NOW());

INSERT INTO `self_xdsd`.`slf_invoices_xdsd`
(invoiceId, repo_fullname, username, provider, role, createdAt)
VALUES
(5, 'vlad/test', 'alexandra', 'github', 'DEV', NOW());

INSERT INTO `self_xdsd`.`slf_invoices_xdsd`
(invoiceId, repo_fullname, username, provider, role, createdAt)
VALUES
(6, 'amihaiemil/to_rename', 'john', 'github', 'DEV', NOW());

INSERT INTO `self_xdsd`.`slf_payments_xdsd`
(`invoiceId`, `transactionId`, `payment_timestamp`, `value`, `status`, `failReason`)
VALUES(1, 'transaction123', '2021-03-01', 1000, 'FAILED', 'Failed Payment 1');

INSERT INTO `self_xdsd`.`slf_payments_xdsd`
(`invoiceId`, `transactionId`, `payment_timestamp`, `value`, `status`, `failReason`)
VALUES(1, 'transaction456', '2021-03-02', 1000, 'FAILED', 'Failed Payment 2');

INSERT INTO `self_xdsd`.`slf_payments_xdsd`
(`invoiceId`, `transactionId`, `payment_timestamp`, `value`, `status`, `failReason`)
VALUES(2, 'transaction123', '2021-03-01', 1000, 'FAILED', 'Failed Payment 1');

INSERT INTO `self_xdsd`.`slf_payments_xdsd`
(`invoiceId`, `transactionId`, `payment_timestamp`, `value`, `status`, `failReason`)
VALUES(2, 'transaction456', '2021-03-02', 1000, 'FAILED', 'Failed Payment 2');

INSERT INTO `self_xdsd`.`slf_invoicedtasks_xdsd`
(invoiceId,
 repo_fullname,
 username,
 provider,
 role,
 value,
 commission,
 contributorCommission,
 issueId,
 assigned,
 deadline,
 invoiced,
 estimation_minutes,
 isPullRequest)
VALUES
(   1,
    'amihaiemil/docker-java-api',
    'john',
    'github',
    'DEV',
    15000,
    50,
    30,
    '200',
    '2020-06-01',
    '2020-06-11',
    NOW(),
    90,
    0
);
INSERT INTO `self_xdsd`.`slf_invoicedtasks_xdsd`
(invoiceId,
 repo_fullname,
 username,
 provider,
 role,
 value,
 commission,
 contributorCommission,
 issueId,
 assigned,
 deadline,
 invoiced,
 estimation_minutes,
 isPullRequest)
VALUES
(   2,
    'amihaiemil/docker-java-api',
    'maria',
    'github',
    'REV',
    8000,
    50,
    30,
    '201',
    '2020-06-01',
    '2020-06-11',
    NOW(),
    60,
    0
);

INSERT INTO `self_xdsd`.`slf_invoicedtasks_xdsd`
(invoiceId, repo_fullname, username, provider, role, value, commission, contributorCommission, issueId, assigned, deadline, invoiced, estimation_minutes, isPullRequest)
VALUES
(3, 'vlad/test', 'maria', 'github', 'PO', 10000, 50, 30, '100', '2020-06-01', '2020-06-11', NOW(), 60, 0);

INSERT INTO `self_xdsd`.`slf_invoicedtasks_xdsd`
(invoiceId, repo_fullname, username, provider, role, value, commission, contributorCommission, issueId, assigned, deadline, invoiced, estimation_minutes, isPullRequest)
VALUES
(4, 'vlad/test', 'alexandra', 'github', 'DEV', 10000, 100, 30, '899', '2020-06-01', '2020-06-11', NOW(), 60, 0);

INSERT INTO `self_xdsd`.`slf_invoicedtasks_xdsd`
(invoiceId, repo_fullname, username, provider, role, value, commission, contributorCommission, issueId, assigned, deadline, invoiced, estimation_minutes, isPullRequest)
VALUES
(6, 'amihaiemil/to_rename', 'john', 'github', 'DEV', 10000, 100, 30, '800', '2020-06-01', '2020-06-11', NOW(), 60, 0);

INSERT INTO `self_xdsd`.`slf_invoicedtasks_xdsd`
(invoiceId, repo_fullname, username, provider, role, value, commission, contributorCommission, issueId, assigned, deadline, invoiced, estimation_minutes, isPullRequest)
VALUES
(6, 'amihaiemil/to_rename', 'john', 'github', 'DEV', 10000, 100, 30, '801', '2020-06-01', '2020-06-11', NOW(), 60, 0);

INSERT INTO `self_xdsd`.`slf_invoicedtasks_xdsd`
(invoiceId, repo_fullname, username, provider, role, value, commission, contributorCommission, issueId, assigned, deadline, invoiced, estimation_minutes, isPullRequest)
VALUES
(6, 'amihaiemil/to_rename', 'john', 'github', 'DEV', 10000, 100, 30, '802', '2020-06-01', '2020-06-11', NOW(), 60, 0);

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
('johndoe/stripe_repo', 'github', 'STRIPE', 5000, 1, 'stripewallet-4444');

INSERT INTO `self_xdsd`.`slf_wallets_xdsd`
(`repo_fullname`, `provider`, `type`, `cash`, `active`, `identifier`)
VALUES
('johndoe/stripe_repo', 'github', 'FAKE', 10000000, 0, 'fakeWallet-5555');

INSERT INTO `self_xdsd`.`slf_wallets_xdsd`
(`repo_fullname`, `provider`, `type`, `cash`, `active`, `identifier`)
VALUES
('maria/to_remove', 'github', 'FAKE', 10000000, 0, 'fakew-5555555');

INSERT INTO `self_xdsd`.`slf_wallets_xdsd`
(`repo_fullname`, `provider`, `type`, `cash`, `active`, `identifier`)
VALUES
('maria/to_remove', 'github', 'STRIPE', 10000, 1, 'stripewallet-5555555');

INSERT INTO `self_xdsd`.`slf_wallets_xdsd`
(`repo_fullname`, `provider`, `type`, `cash`, `active`, `identifier`)
VALUES
('amihaiemil/to_rename', 'github', 'FAKE', 10000000, 0, 'fakew-435345345');

INSERT INTO `self_xdsd`.`slf_wallets_xdsd`
(`repo_fullname`, `provider`, `type`, `cash`, `active`, `identifier`)
VALUES
('amihaiemil/to_rename', 'github', 'STRIPE', 10000, 1, 'stripewallet-87787878');

INSERT INTO `self_xdsd`.`slf_paymentmethods_xdsd` (`repo_fullname`,`provider`,`type`,`identifier`,`active`)
VALUES ('johndoe/stripe_repo', 'github', 'STRIPE', 'stripe_pm_to_delete', 0);

INSERT INTO `self_xdsd`.`slf_paymentmethods_xdsd` (`repo_fullname`,`provider`,`type`,`identifier`,`active`)
VALUES ('johndoe/stripe_repo', 'github', 'STRIPE', 'stripe_pm_active', 1);

INSERT INTO `self_xdsd`.`slf_paymentmethods_xdsd` (`repo_fullname`,`provider`,`type`,`identifier`,`active`)
VALUES ('johndoe/stripe_repo', 'github', 'STRIPE', 'stripe_pm_inactive', 0);

INSERT INTO `self_xdsd`.`slf_paymentmethods_xdsd` (`repo_fullname`,`provider`,`type`,`identifier`,`active`)
VALUES ('amihaiemil/docker-java-api', 'github', 'STRIPE', 'stripe_pm_1', 1);

INSERT INTO `self_xdsd`.`slf_paymentmethods_xdsd` (`repo_fullname`,`provider`,`type`,`identifier`,`active`)
VALUES ('amihaiemil/docker-java-api', 'github', 'STRIPE', 'stripe_pm_2', 0);

INSERT INTO `self_xdsd`.`slf_paymentmethods_xdsd` (`repo_fullname`,`provider`,`type`,`identifier`,`active`)
VALUES ('maria/to_remove', 'github', 'STRIPE', 'stripe_mary_card_1', 0);

INSERT INTO `self_xdsd`.`slf_paymentmethods_xdsd` (`repo_fullname`,`provider`,`type`,`identifier`,`active`)
VALUES ('maria/to_remove', 'github', 'STRIPE', 'stripe_mary_card_2', 1);

INSERT INTO `self_xdsd`.`slf_paymentmethods_xdsd` (`repo_fullname`,`provider`,`type`,`identifier`,`active`)
VALUES ('amihaiemil/to_rename', 'github', 'STRIPE', 'stripe_mihairn_card_1', 1);

INSERT INTO `self_xdsd`.`slf_resignations_xdsd`
(`repo_fullname`, `username`, `provider`, `issueId`, `timestamp`,`reason`, `isPullRequest`)
VALUES
('vlad/test', 'maria', 'github', '999', '2020-09-03', 'DEADLINE', 0);

INSERT INTO `self_xdsd`.`slf_resignations_xdsd`
(`repo_fullname`, `username`, `provider`, `issueId`, `timestamp`,`reason`, `isPullRequest`)
VALUES
('vlad/test', 'john', 'github', '999', '2020-09-01', 'ASKED', 0);

INSERT INTO `self_xdsd`.`slf_resignations_xdsd`
(`repo_fullname`, `username`, `provider`, `issueId`, `timestamp`,`reason`, `isPullRequest`)
VALUES
('amihaiemil/to_rename', 'john', 'github', '1001', '2020-09-01', 'ASKED', 0);

INSERT INTO `self_xdsd`.`slf_payoutmethods_xdsd`
(`username`, `provider`, `type`, `identifier`)
VALUES ('maria', 'github', 'stripe', 'acct_001');

INSERT INTO `self_xdsd`.`slf_platforminvoices_xdsd`
(`id`, `createdAt`, `billedTo`, `commission`, `vat`, `transactionId`, `payment_timestamp`, `eurToRon`)
VALUES (1, '2021-01-09', 'mihai', 130, 19, 'transaction123', '2021-01-09', 487);

INSERT INTO `self_xdsd`.`slf_platforminvoices_xdsd`
(`id`, `createdAt`, `billedTo`, `commission`, `vat`, `transactionId`, `payment_timestamp`, `invoiceId`, `eurToRon`)
VALUES (2, '2021-01-09', 'vlad', 130, 19, 'transactionIdHere', '2021-01-09', 4, 487);

INSERT INTO `self_xdsd`.`slf_jsonstorage_xdsd` (`url`, `etag`, `jsonBody`)
VALUES ('https://github.com/self-xdsd/self-storage/issues/123', 'etag123321', '{"issueId":"123"}');