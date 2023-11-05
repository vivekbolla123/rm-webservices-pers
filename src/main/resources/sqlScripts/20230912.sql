ALTER TABLE `rm_parameter_values`
CHANGE COLUMN `parameterValue` `parameterValue` TEXT NULL DEFAULT NULL ;

INSERT INTO `rm_parameter_values` (`parameterKey`, `parameterValue`) VALUES ('RBD_VALUES', 'Y,B,C,D,E,F,H,I,J,K,M,N,O,Q,R0,R1,R2,R3,R4,R5,R6,R7,R8,R9,T0,T1,T2,T3,T4,T5,T6,T7,T8,T9,U0,U1,U2,U3,U4,U5,U6,U7,U8,U9,V0,V1,V2,V3,V4,V5,V6,V7,V8,V9,Z0,Z1,Z2,Z3,Z4,Z5,Z6,Z7,Z8,Z9');
INSERT INTO `rm_parameter_values` (`parameterKey`, `parameterValue`) VALUES ('RDB_SIZE', '66');