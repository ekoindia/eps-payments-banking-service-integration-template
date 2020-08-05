create database IF NOT EXISTS service_db_template;

USE service_db_template;

CREATE TABLE IF NOT EXISTS `configurations` (
  `configId` int(11) NOT NULL AUTO_INCREMENT,
  `configKey` varchar(50) DEFAULT NULL,
  `configValue` varchar(200) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `source` int(11) NOT NULL,
  PRIMARY KEY (`configId`),
  UNIQUE KEY `key_source_unique` (`configKey`,`source`),
  KEY `source` (`source`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `transactions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ekotrxnid` int(11) DEFAULT NULL,
  `txtime` datetime DEFAULT NULL,
  `requestype` varchar(2) DEFAULT NULL,
  `amount` decimal(10,2) DEFAULT NULL,
  `remarks` varchar(50) DEFAULT NULL,
  `trackingNumber` varchar(50) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `reconattempt` int(11) DEFAULT NULL,
  `responsecode` varchar(10) DEFAULT NULL,
  `bankTrxnId` varchar(50) DEFAULT NULL,
  `callbackStatus` int(11) DEFAULT NULL,
  `source` int(11) DEFAULT NULL,
  `utr_number` varchar(200) DEFAULT NULL,
  `last_recon_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ekotrxnid_UNIQUE` (`ekotrxnid`),
  KEY `ekotrxnid` (`ekotrxnid`),
  KEY `txtime` (`txtime`),
  KEY `amount` (`amount`),
  KEY `reconattempt` (`reconattempt`),
  KEY `responsecode` (`responsecode`),
  KEY `callbackStatus` (`callbackStatus`),
  KEY `status` (`status`),
  KEY `source` (`source`),
  KEY `bankTrxnId` (`bankTrxnId`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `callbackrequest` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ekotrxnid` int(11) DEFAULT NULL,
  `requestAt` datetime DEFAULT NULL,
  `source` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ekotrxnid` (`ekotrxnid`),
  KEY `requestAt` (`requestAt`),
  KEY `status` (`status`),
  KEY `source` (`source`)
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS `messagelog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ekotrxnid` int(11) DEFAULT NULL,
  `typeId` int(11) NOT NULL,
  `request` varchar(2000) DEFAULT NULL,
  `requestedAt` datetime DEFAULT NULL,
  `response` varchar(2000) DEFAULT NULL,
  `actCode` varchar(20) DEFAULT NULL,
  `responseReceivedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `Ekotrxnid` (`Ekotrxnid`),
  KEY `typeId` (`typeId`),
  KEY `actCode` (`actCode`),
  KEY `requestedAt` (`requestedAt`),
  KEY `responseReceivedAt` (`responseReceivedAt`)
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS `mock_config` (
  `configId` int(11) NOT NULL AUTO_INCREMENT,
  `configKey` varchar(50) DEFAULT NULL,
  `configValue` varchar(200) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `source` int(11) DEFAULT NULL,
  PRIMARY KEY (`configId`),
  UNIQUE KEY `configKey_UNIQUE` (`configKey`),
  KEY `source` (`source`),
  KEY `source_2` (`source`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `response_code` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `response_code` varchar(10) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `detailed_description` varchar(200) DEFAULT NULL,
  `decline_type` varchar(30) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `source` int(11) DEFAULT NULL,
  `enquiryStatus` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_response_unique` (`response_code`,`source`),
  KEY `status` (`status`),
  KEY `enquiryStatus` (`enquiryStatus`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `task_configuration` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` int(11) DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `task_interval` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `source` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `task_id_UNIQUE` (`task_id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `source` (`source`),
  KEY `source_2` (`source`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `task_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `taskId` int(11) DEFAULT NULL,
  `taskName` varchar(50) DEFAULT NULL,
  `startTime` datetime DEFAULT NULL,
  `endTime` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `taskId` (`taskId`),
  KEY `startTIme` (`startTime`),
  KEY `endTIme` (`endTime`),
  KEY `status` (`status`)
) ENGINE=InnoDB;


-- sample inserts

-- configurations table inserts
INSERT IGNORE INTO configurations (configId,configKey, configValue, description) values(1,"BRIDGE_URL","https://thirdparty.com/switch/imps","BRIDGE URL");
INSERT IGNORE INTO configurations (configId,configKey, configValue, description) values(2,"AUTHORIZED_IP_LIST","127.0.0.1","AUTHENTICATED IP ADDRESS LIST");
INSERT IGNORE INTO configurations (configId,configKey, configValue, description) values(3,"CONNECTION_TIME_OUT","40000","RBL BC TRANSACTCON CONNECTION TIME OUT");
INSERT IGNORE INTO configurations (configId,configKey, configValue, description) values(4,"VALIDATE_IP_ON_OFF","1","ENABLE IP AUTHENTICATION ON:1 OFF:0");
INSERT IGNORE INTO configurations (configId,configKey, configValue, description) values(5,"SERVER_LOG_FILE_PATH","./server.log","SERVER LOG FILE PATH");
INSERT IGNORE INTO configurations (configId,configKey, configValue, description) values(6,"MOCKED_RESPONSE_TRXN_AND_REQUERY","0","mock");
INSERT IGNORE INTO configurations (configId,configKey, configValue, description) values(7,"EOD_RECON_HOUR","21","Transaction Enquiry All RNR transactions");
INSERT IGNORE INTO configurations (configId,configKey, configValue, description) values(8,"CALL_BACK_URL","http://127.0.01:8080/eko/v1/transactions","Callback url of EPS");
INSERT IGNORE INTO configurations (configId,configKey, configValue, description) values(9,"MAX_DAYS_FOR_RECON","1","");
INSERT IGNORE INTO configurations (configId,configKey, configValue, description) values(10,"TRANSACTION_POSTING_HOURS","","");


-- response_code
insert ignore into response_code (response_code, description, detailed_description, decline_type, status, enquiryStatus) values ("00","TRANSACTION APPROVED","Successful","Successful",1,1);
insert ignore into response_code (response_code, description, detailed_description, decline_type, status, enquiryStatus) values ("08","HOST (CBS) OFFLINE","Beneficiary bank switch respond to NPCI that their CBS is down hence reverse the customer a/c of remitting bank","Technical",0,3);
insert ignore into response_code (response_code, description, detailed_description, decline_type, status, enquiryStatus) values ("91","Response time out","Bank as beneficiary does not send any response within the NPCI TAT of 30 seconds","Status Unknown",3,3);
insert ignore into response_code (response_code, description, detailed_description, decline_type, status, enquiryStatus) values ("12","INVALID TRANSACTION","An invalid transaction initiated by remitting bank ex: Deposit","Technical",0,3);
insert ignore into response_code (response_code, description, detailed_description, decline_type, status, enquiryStatus) values ("20","INVALID RESPONSE CODE","Beneficiary bank has sent an invalid response code to NPCI which is not as per specification","Technical",0,3);
insert ignore into response_code (response_code, description, detailed_description, decline_type, status, enquiryStatus) values ("96","UNABLE TO PROCESS","Beneficiary bank is not able to process the transaction due to various technical reason","Technical",0,3);
insert ignore into response_code (response_code, description, detailed_description, decline_type, status, enquiryStatus) values ("30","Invalid message format","Remitting bank / Beneficiary bank has sent the message in wrong format which is not as per NPCI specification","Technical",0,3);

-- task_configuration
insert ignore into task_configuration values (1,1001,'TransactionEnquiryTask',5,1);
insert ignore into task_configuration values (2,1002,'PendingCallbackRequestTask',10,1);
insert ignore into task_configuration values (3,1002,'TransactionRepostingTask',10,1);

