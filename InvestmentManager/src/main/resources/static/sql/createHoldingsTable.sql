CREATE TABLE holdings (
  holding_id bigint(20) NOT NULL AUTO_INCREMENT,
  purchase_price decimal(10,4) DEFAULT NULL,
  quantity decimal(10,4) DEFAULT NULL,
  account_id bigint(20) DEFAULT NULL,
  investment_id bigint(20) DEFAULT NULL,
  PRIMARY KEY (holding_id),
  FOREIGN KEY (account_id) REFERENCES accounts(account_id),
  FOREIGN KEY (investment_id) REFERENCES investments(investment_id)
);