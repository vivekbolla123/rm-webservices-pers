USE `QP_DW_RMALLOC`;

CREATE TABLE `permissions` (
  `PermissionID` int NOT NULL,
  `Permission` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`PermissionID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


INSERT INTO `permissions` VALUES (1,'view_output_screen'),(2,'view_allocation_summary'),(3,'view_input_screen_version1'),(4,'view_input_screen_version2'),(5,'view_run_screen'),(6,'view_run_schedule');

CREATE TABLE `role_permission` (
  `Role` varchar(25) NOT NULL,
  `PermissionID` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`Role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


INSERT INTO `role_permission` VALUES ('USER','1,2,3,5,6'),('ADMIN','1,2,3,4,5,6'),('MANAGER','1,2,3,4,5,6');