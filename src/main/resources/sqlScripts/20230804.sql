USE `QP_DW_RMALLOC`;

-- Alter Run Summary
ALTER TABLE `run_summary`
ADD COLUMN `hiFare` VARCHAR(10) NULL AFTER `RunId`,
ADD COLUMN `fareAnchor` VARCHAR(5) NULL AFTER `hiFare`,
ADD COLUMN `fareOffset` VARCHAR(5) NULL AFTER `fareAnchor`;

-- Alter Run Summary
ALTER TABLE `navitaire_allocation_audit`
ADD COLUMN `b2cRunId` VARCHAR(45) NULL AFTER `run_id`,
ADD COLUMN `b2bRunId` VARCHAR(45) NULL AFTER `b2cRunId`,
ADD COLUMN `failures` INT NULL AFTER `b2bRunId`,
ADD COLUMN `total` INT NULL AFTER `failures`;
