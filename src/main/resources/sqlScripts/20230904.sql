truncate table permissions;
truncate table role_permission;

ALTER TABLE `role_permission`
CHANGE COLUMN `PermissionID` `PermissionID` JSON NULL DEFAULT NULL ;
ALTER TABLE `permissions`
RENAME TO `config_permissions` ;
ALTER TABLE `role_permission`
RENAME TO `config_role_permission` ;

INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('1', 'view_input_screen');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('2', 'view_curves');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('3', 'update_curves');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('4', 'view_fares');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('5', 'update_fares');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('6', 'view_strategy');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('7', 'update_strategy');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('8', 'view_market_list');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('9', 'update_market_list');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('10', 'view_own_adhoc_market_list');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('11', 'update_own_adhoc_market_list');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('12', 'view_dplf_band');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('13', 'update_dplf_band');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('14', 'view_ndo_band');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('15', 'update_ndo_band');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('16', 'view_time_range');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('17', 'update_time_range');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('18', 'view_acceptable_range');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('19', 'update_acceptable_range');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('20', 'view_softblock_threshold');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('21', 'update_softblock_threshold');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('22', 'view_allocation_summary');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('23', 'view_run_screen');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('24', 'trigger_adhoc_market_list');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('25', 'view_output_summary');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('26', 'view_run_scheduler');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('27', 'view_performance_screens');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('28', 'view_input_dashboards');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('29', 'view_run_summary_states');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('30', 'view_all_adhoc_market_list');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('31', 'update_all_adhoc_market_list');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('32', 'trigger_all_adhoc_market_list');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('33', 'view_allocation');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('34', 'view_update_queue');
INSERT INTO `config_permissions` (`PermissionID`, `Permission`) VALUES ('35', 'view_performance');


INSERT INTO `config_role_permission` VALUES
('ADMIN','{\"role\": \"ADMIN\", \"permissions\": [1, 2, 3, 4, 5, 6, 7, 8, 9, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35]}',1),
('MANAGER','{\"role\": \"MANAGER\", \"permissions\": [1, 2, 3, 4, 5, 6, 7, 8, 9, 20, 21, 22, 23, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35]}',2),
('INVENTORYANALYST','{\"role\": \"INVENTORY ANALYST\", \"permissions\": [1, 2, 4, 6, 7, 8, 9, 10, 11, 20, 22, 23, 24, 25, 26, 27, 28, 29, 33, 34, 35]}',3),
('PRICINGANALYST','{\"role\": \"PRICING ANALYST\", \"permissions\": [1, 2, 4, 5, 6, 8, 10, 20, 22, 23, 25, 26, 27, 28, 29, 33, 34, 35]}',4);