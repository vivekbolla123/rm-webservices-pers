INSERT INTO `permissions` (`PermissionID`, `Permission`) VALUES ('11', 'view_update_queue');
INSERT INTO `permissions` (`PermissionID`, `Permission`) VALUES ('12', 'view_allocation');
INSERT INTO `permissions` (`PermissionID`, `Permission`) VALUES ('13', 'view_performance');

UPDATE `role_permission` SET `PermissionID` = '1,2,3,4,5,6,7,8,9,10,11,12' WHERE (`Role` = 'ADMIN');
UPDATE `role_permission` SET `PermissionID` = '1,2,3,4,5,6,7,8,9,10,11,12' WHERE (`Role` = 'MANAGER');
UPDATE `role_permission` SET `PermissionID` = '1,2,3,5,6,8,10,11,12' WHERE (`Role` = 'USER');
