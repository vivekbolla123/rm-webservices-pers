ALTER TABLE `role_permission`
CHANGE COLUMN `PermissionID` `PermissionID` VARCHAR(55) NULL DEFAULT NULL ;

INSERT INTO `permissions` (`PermissionID`, `Permission`) VALUES ('8', 'update_input_screen_version1');
INSERT INTO `permissions` (`PermissionID`, `Permission`) VALUES ('9', 'update_input_screen_version2');
INSERT INTO `permissions` (`PermissionID`, `Permission`) VALUES ('10', 'run_adhoc_scheduler');

DELETE FROM `permissions` WHERE (`PermissionID` = '6');

UPDATE `role_permission` SET `PermissionID` = '1,2,3,5,8,10' WHERE (`Role` = 'USER');
UPDATE `role_permission` SET `PermissionID` = '1,2,3,4,5,7,8,9,10' WHERE (`Role` = 'MANAGER');
UPDATE `role_permission` SET `PermissionID` = '1,2,3,4,5,7,8,9,10' WHERE (`Role` = 'ADMIN');

ALTER TABLE `role_permission`
ADD COLUMN `priority` INT NULL AFTER `PermissionID`;

UPDATE `role_permission` SET `priority` = '1' WHERE (`Role` = 'ADMIN');
UPDATE `role_permission` SET `priority` = '2' WHERE (`Role` = 'MANAGER');
UPDATE `role_permission` SET `priority` = '3' WHERE (`Role` = 'USER');
