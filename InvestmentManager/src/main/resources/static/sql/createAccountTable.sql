CREATE TABLE accounts (
  account_id bigint(20) NOT NULL AUTO_INCREMENT,
  account_name varchar(255) DEFAULT NULL,
  account_number varchar(255) DEFAULT NULL,
  account_type varchar(255) DEFAULT NULL,
  company varchar(255) DEFAULT NULL,
  account_owner varchar(255) DEFAULT NULL,
  visible int(11) NOT NULL,
  PRIMARY KEY (account_id)
);
