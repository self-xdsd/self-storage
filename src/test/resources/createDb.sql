-- -----------------------------------------------------
-- Schema self_xdsd
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS self_xdsd;

-- -----------------------------------------------------
-- Schema self_xdsd
-- -----------------------------------------------------
CREATE SCHEMA self_xdsd;

-- -----------------------------------------------------
-- Table self_xdsd.slf_contributors_xdsd
-- -----------------------------------------------------

CREATE TABLE self_xdsd.slf_contributors_xdsd (
  username VARCHAR(100) NOT NULL,
  provider VARCHAR(50) NOT NULL,
  PRIMARY KEY (username, provider)
);

-- -----------------------------------------------------
-- Table self_xdsd.slf_users_xdsd
-- -----------------------------------------------------

CREATE TABLE self_xdsd.slf_users_xdsd (
  username VARCHAR(100) NOT NULL,
  role VARCHAR (100) NOT NULL,
  provider VARCHAR(50) NOT NULL,
  email VARCHAR(150) NULL DEFAULT NULL,
  access_token VARCHAR(512) NULL DEFAULT NULL,
  PRIMARY KEY (username, provider)
);

-- -----------------------------------------------------
-- Table self_xdsd.slf_pms_xdsd
-- -----------------------------------------------------
CREATE TABLE self_xdsd.slf_pms_xdsd (
  id INT NOT NULL AUTO_INCREMENT,
  userid VARCHAR(100) NOT NULL,
  username VARCHAR(100) NOT NULL,
  provider VARCHAR(50) NOT NULL,
  access_token VARCHAR(256) NOT NULL,
  commission DECIMAL(20,0) NOT NULL,
  PRIMARY KEY (id)
);

-- -----------------------------------------------------
-- Table self_xdsd.slf_projects_xdsd
-- -----------------------------------------------------
CREATE TABLE self_xdsd.slf_projects_xdsd (
  repo_fullname VARCHAR(256) NOT NULL,
  webhook_token VARCHAR(256) NOT NULL,
  provider VARCHAR(50) NOT NULL,
  username VARCHAR(100) NOT NULL,
  pmid INT NOT NULL,
  PRIMARY KEY (repo_fullname, provider),
  CONSTRAINT owner
    FOREIGN KEY (username, provider)
    REFERENCES self_xdsd.slf_users_xdsd (username, provider),
  CONSTRAINT pm
    FOREIGN KEY (pmid)
    REFERENCES self_xdsd.slf_pms_xdsd (id)
);

-- -----------------------------------------------------
-- Table self_xdsd.slf_contracts_xdsd
-- -----------------------------------------------------
CREATE TABLE self_xdsd.slf_contracts_xdsd (
  repo_fullname VARCHAR(256) NOT NULL,
  username VARCHAR(100) NOT NULL,
  provider VARCHAR(50) NOT NULL,
  role VARCHAR(32) NOT NULL,
  hourly_rate BIGINT NOT NULL,
  PRIMARY KEY (repo_fullname, username, provider, role),
  CONSTRAINT contributor
    FOREIGN KEY (username, provider)
    REFERENCES self_xdsd.slf_contributors_xdsd (username, provider),
  CONSTRAINT project
    FOREIGN KEY (repo_fullname, provider)
    REFERENCES self_xdsd.slf_projects_xdsd (repo_fullname, provider)
);

-- -----------------------------------------------------
-- Table self_xdsd.slf_tasks_xdsd
-- -----------------------------------------------------
CREATE TABLE self_xdsd.slf_tasks_xdsd (
  repo_fullname VARCHAR(256) NOT NULL,
  issueId VARCHAR(50) NOT NULL,
  provider VARCHAR(50) NOT NULL,
  role VARCHAR(32) NOT NULL,
  username VARCHAR(100) NULL DEFAULT NULL,
  assigned DATETIME NULL DEFAULT NULL,
  deadline DATETIME NULL DEFAULT NULL,
  estimation_minutes INT NOT NULL,
  PRIMARY KEY (issueId, provider, repo_fullname),
  CONSTRAINT assignee
    FOREIGN KEY (repo_fullname, username, provider, role)
    REFERENCES self_xdsd.slf_contracts_xdsd (repo_fullname, username, provider, role),
  CONSTRAINT parent_project
    FOREIGN KEY (repo_fullname, provider)
    REFERENCES self_xdsd.slf_projects_xdsd (repo_fullname, provider)
);

-- -----------------------------------------------------
-- Table `self_xdsd`.`slf_invoices_xdsd`
-- -----------------------------------------------------
CREATE TABLE `self_xdsd`.`slf_invoices_xdsd` (
  `invoiceId` INT NOT NULL AUTO_INCREMENT,
  `repo_fullname` VARCHAR(256) NOT NULL,
  `username` VARCHAR(100) NOT NULL,
  `provider` VARCHAR(50) NOT NULL,
  `role` VARCHAR(32) NOT NULL,
  `payment_timestamp` DATETIME NULL DEFAULT NULL,
  `createdAt` DATETIME NOT NULL,
  `transactionId` VARCHAR(256) NULL DEFAULT NULL,
  PRIMARY KEY (`invoiceId`, `repo_fullname`, `username`, `provider`, `role`),
  CONSTRAINT `fkContract`
    FOREIGN KEY (`repo_fullname` , `username` , `provider` , `role`)
    REFERENCES `self_xdsd`.`slf_contracts_xdsd` (`repo_fullname` , `username` , `provider` , `role`));

-- -----------------------------------------------------
-- Table `self_xdsd`.`slf_invoicedtasks_xdsd`
-- -----------------------------------------------------
CREATE TABLE `self_xdsd`.`slf_invoicedtasks_xdsd` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `repo_fullname` VARCHAR(256) NOT NULL,
  `username` VARCHAR(100) NOT NULL,
  `provider` VARCHAR(50) NOT NULL,
  `role` VARCHAR(32) NOT NULL,
  `value` DECIMAL(20,0) NOT NULL,
  `commission` DECIMAL(20,0) NOT NULL,
  `issueId` VARCHAR(50) NOT NULL,
  `assigned` DATETIME NOT NULL,
  `deadline` DATETIME NOT NULL,
  `invoiced` DATETIME NOT NULL,
  `invoiceId` INT NOT NULL,
  `estimation_minutes` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `invoiceContractFk`
    FOREIGN KEY (`repo_fullname` , `username` , `provider` , `role` , `invoiceId`)
    REFERENCES `self_xdsd`.`slf_invoices_xdsd` (`repo_fullname` , `username` , `provider` , `role` , `invoiceId`));

-- -----------------------------------------------------
-- Table `self_xdsd`.`slf_resignations_xdsd`
-- -----------------------------------------------------
CREATE TABLE `self_xdsd`.`slf_resignations_xdsd` (
  `repo_fullname` VARCHAR(256) NOT NULL,
  `username` VARCHAR(100) NOT NULL,
  `provider` VARCHAR(50) NOT NULL,
  `issueId` VARCHAR(50) NOT NULL,
  `timestamp` DATETIME NOT NULL,
  `reason` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`repo_fullname`, `username`, `provider`, `issueId`),
  CONSTRAINT `task`
    FOREIGN KEY (`repo_fullname` , `provider` , `issueId`)
    REFERENCES `self_xdsd`.`slf_tasks_xdsd` (`repo_fullname` , `provider` , `issueId`),
  CONSTRAINT `resignee`
    FOREIGN KEY (`username` , `provider`)
    REFERENCES `self_xdsd`.`slf_contributors_xdsd` (`username` , `provider`));

-- -----------------------------------------------------
-- Table `self_xdsd`.`slf_wallets_xdsd`
-- -----------------------------------------------------
CREATE TABLE `self_xdsd`.`slf_wallets_xdsd` (
  `repo_fullname` VARCHAR(256) NOT NULL,
  `provider` VARCHAR(50) NOT NULL,
  `type` VARCHAR(50) NOT NULL,
  `cash` DECIMAL(20,0) NOT NULL,
  `active` TINYINT(1) NOT NULL,
  `identifier` VARCHAR(256) NOT NULL,
  PRIMARY KEY (`repo_fullname`, `provider`, `type`),
  CONSTRAINT `ownerProject`
    FOREIGN KEY (`repo_fullname` , `provider`)
    REFERENCES `self_xdsd`.`slf_projects_xdsd` (`repo_fullname` , `provider`));

-- -----------------------------------------------------
-- Table `self_xdsd`.`slf_payoutmethods_xdsd`
-- -----------------------------------------------------
CREATE TABLE `self_xdsd`.`slf_payoutmethods_xdsd` (
  `username` VARCHAR(100) NOT NULL,
  `provider` VARCHAR(50) NOT NULL,
  `type` VARCHAR(50) NOT NULL,
  `active` TINYINT(1) NOT NULL,
  `identifier` VARCHAR(256) NOT NULL,
  PRIMARY KEY (`username`, `provider`, `type`),
  CONSTRAINT `ownerContributor`
    FOREIGN KEY (`username` , `provider`)
    REFERENCES `self_xdsd`.`slf_contributors_xdsd` (`username` , `provider`));

