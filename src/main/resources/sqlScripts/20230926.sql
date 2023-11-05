ALTER TABLE `market_list`
ADD COLUMN `analystName` VARCHAR(45) NULL AFTER `SkippingFactor`;

ALTER TABLE `market_list_adhoc`
ADD COLUMN `analystName` VARCHAR(45) NULL AFTER `SkippingFactor`;

ALTER TABLE `market_list_adhoc_AS`
ADD COLUMN `analystName` VARCHAR(45) NULL AFTER `SkippingFactor`;

ALTER TABLE `market_list_adhoc_DR`
ADD COLUMN `analystName` VARCHAR(45) NULL AFTER `SkippingFactor`;

ALTER TABLE `market_list_adhoc_JP`
ADD COLUMN `analystName` VARCHAR(45) NULL AFTER `SkippingFactor`;

ALTER TABLE `market_list_adhoc_KK`
ADD COLUMN `analystName` VARCHAR(45) NULL AFTER `SkippingFactor`;

ALTER TABLE `market_list_adhoc_MB`
ADD COLUMN `analystName` VARCHAR(45) NULL AFTER `SkippingFactor`;

ALTER TABLE `market_list_adhoc_MB`
ADD COLUMN `analystName` VARCHAR(45) NULL AFTER `SkippingFactor`;

ALTER TABLE `market_list_adhoc_mb`
ADD COLUMN `analystName` VARCHAR(45) NULL AFTER `SkippingFactor`;

ALTER TABLE `market_list_adhoc_MM`
ADD COLUMN `analystName` VARCHAR(45) NULL AFTER `SkippingFactor`;

ALTER TABLE `market_list_adhoc_NP`
ADD COLUMN `analystName` VARCHAR(45) NULL AFTER `SkippingFactor`;

ALTER TABLE `market_list_adhoc_SC`
ADD COLUMN `analystName` VARCHAR(45) NULL AFTER `SkippingFactor`;

ALTER TABLE `market_list_adhoc_SV`
ADD COLUMN `analystName` VARCHAR(45) NULL AFTER `SkippingFactor`;

ALTER TABLE `market_list_adhoc_Test`
ADD COLUMN `analystName` VARCHAR(45) NULL AFTER `SkippingFactor`;

CREATE TABLE `config_users` (
  `userName` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`userName`));

INSERT INTO `config_users` (`userName`) VALUES ('APARNA');
INSERT INTO `config_users` (`userName`) VALUES ('SRISHTI');
INSERT INTO `config_users` (`userName`) VALUES ('NEHA');
INSERT INTO `config_users` (`userName`) VALUES ('MAULSIRI');
INSERT INTO `config_users` (`userName`) VALUES ('MAYA');
INSERT INTO `config_users` (`userName`) VALUES ('KUSHAL');
INSERT INTO `config_users` (`userName`) VALUES ('SUKRUTI');

