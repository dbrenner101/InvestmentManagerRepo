CREATE TABLE quotes (
  quote_id bigint(20) NOT NULL AUTO_INCREMENT,
  price_at_close decimal(10,4) DEFAULT NULL,
  quote_date datetime DEFAULT NULL,
  high decimal(10,4) DEFAULT NULL,
  low decimal(10,4) DEFAULT NULL,
  price_at_open decimal(10,4) DEFAULT NULL,
  price_change decimal(10,4) DEFAULT NULL,
  volume int(11) DEFAULT NULL,
  investment_id bigint(20) DEFAULT NULL,
  PRIMARY KEY (quote_id),
  FOREIGN KEY investment_id REFERENCES investments(investment_id)
)