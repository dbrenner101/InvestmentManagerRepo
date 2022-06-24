CREATE TABLE investments (
  investment_id bigint(20) NOT NULL AUTO_INCREMENT,
  company_name varchar(255) DEFAULT NULL,
  exchange varchar(255) DEFAULT NULL,
  sector varchar(255) DEFAULT NULL,
  symbol varchar(255) DEFAULT NULL,
  investment_type int(11) DEFAULT NULL,
  PRIMARY KEY (investment_id)
);
