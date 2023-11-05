ALTER TABLE `config_column_names`
CHANGE COLUMN `tableName` `tableName` VARCHAR(50) NOT NULL ,
ADD PRIMARY KEY (`tableName`);

DELETE FROM `config_column_names` WHERE (`tableName` = 'market_list_adhoc');
UPDATE `config_column_names` SET `columns` = 'Origin,Destin,FlightNumber,PerType,PerStart,PerEnd,DOW,strategyFlag,TimeWindowStart,TimeWindowEnd,CurveID,CarrExlusion,fareAnchor,fareOffset,FirstRBDAlloc,OtherRBDAlloc,SkippingFactor,B2BBackstop,B2CBackstop,B2BFactor' WHERE (`tableName` = 'market_list');

INSERT INTO `config_column_names` (`tableName`, `columns`) VALUES ('d1_d2_strategies', 'strategy,Decision,Band,0,1,2,3,4,5,6');
INSERT INTO `config_column_names` (`tableName`, `columns`) VALUES ('allocation_acceptable_range_d1', 'airports,ar_start,ar_end');
INSERT INTO `config_column_names` (`tableName`, `columns`) VALUES ('config_pfl_threshold', 'NDO,plf_threshold,flightNumber');
INSERT INTO `config_column_names` (`tableName`, `columns`) VALUES ('strategy_input', 'strategy,ndo_band,dplf_band,criteria,time_range,offset');
INSERT INTO `config_column_names` (`tableName`, `columns`) VALUES ('dplf_bands', 'dplf_band,start,end');
INSERT INTO `config_column_names` (`tableName`, `columns`) VALUES ('ndo_bands', 'ndo_band,start,end');
INSERT INTO `config_column_names` (`tableName`, `columns`) VALUES ('time_ranges', 'time_range,start,end,type');
