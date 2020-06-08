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
  username VARCHAR(100) NOT NULL,
  provider VARCHAR(50) NOT NULL,
  access_token VARCHAR(256) NOT NULL,
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
  PRIMARY KEY (issueId, provider, repo_fullname),
  CONSTRAINT assignee
    FOREIGN KEY (repo_fullname, provider, role, username)
    REFERENCES self_xdsd.slf_contracts_xdsd (repo_fullname, provider, role, username),
  CONSTRAINT parent_project
    FOREIGN KEY (repo_fullname, provider)
    REFERENCES self_xdsd.slf_projects_xdsd (repo_fullname, provider)
);
