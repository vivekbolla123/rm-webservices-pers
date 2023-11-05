USE `QP_DW_RMALLOC`;

CREATE TABLE `criteria` (
  `criteria` VARCHAR(7) NOT NULL,
  PRIMARY KEY (`criteria`));

INSERT INTO `criteria` (`criteria`) VALUES ('MIN');
INSERT INTO `criteria` (`criteria`) VALUES ('MAX');
INSERT INTO `criteria` (`criteria`) VALUES ('MINO');
INSERT INTO `criteria` (`criteria`) VALUES ('MAXO');

UPDATE `role_permission` SET `PermissionID` = '1,2,3,4,5,6,7' WHERE (`Role` = 'MANAGER');
UPDATE `role_permission` SET `PermissionID` = '1,2,3,4,5,6,7' WHERE (`Role` = 'ADMIN');

INSERT INTO `QP_DW_RMALLOC`.`permissions` (`PermissionID`, `Permission`) VALUES ('7', 'view_aws_scheduler');
