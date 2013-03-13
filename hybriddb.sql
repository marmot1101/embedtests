/*
SQLyog Community v10.4 Beta1
MySQL - 5.5.28 : Database - vlmetrics2
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


/*Table structure for table `activeaction` */

DROP TABLE IF EXISTS `activeaction`;

CREATE TABLE `activeaction` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'active action count incremental ID',
  `vltraderID` int(10) unsigned NOT NULL COMMENT 'vltrader alias id',
  `timestamp` timestamp NOT NULL DEFAULT NOW()  COMMENT 'timestamp',
  `actautoruncount` int(11) DEFAULT NULL COMMENT 'autorun actions current active count',
  `actinteractivecount` int(11) DEFAULT NULL COMMENT 'interactive actions current count',
  `actscheduledcount` int(11) DEFAULT NULL COMMENT 'scheduled actions count',
  PRIMARY KEY (`ID`),
  KEY `vltraderid` (`vltraderID`),
  CONSTRAINT `activeaction_ibfk_1` FOREIGN KEY (`vltraderid`) REFERENCES `vltraders` (`ID`)
) ;

/*Table structure for table `cpurec` */

DROP TABLE IF EXISTS `cpurec`;

CREATE TABLE `cpurec` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'cpu metric record incremental ID',
  `vltraderID` int(11) unsigned NOT NULL COMMENT 'vltrader alias id',
  `timestamp` timestamp NOT NULL DEFAULT NOW()  COMMENT 'timestamp',
  `vltcpupercentage` int(11) DEFAULT NULL COMMENT 'vltrader cpu usage percentage',
  `vltcputime` int(11) DEFAULT NULL COMMENT 'vltrader cpu time',
  PRIMARY KEY (`ID`),
  
  CONSTRAINT `vltraderAlias` FOREIGN KEY (`vltraderID`) REFERENCES `vltraders` (`ID`)
)  ;

/*Table structure for table `memoryrec` */

DROP TABLE IF EXISTS `memoryrec`;

CREATE TABLE `memoryrec` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'memory metric record ID autoimcrement',
  `vltraderID` int(11) unsigned NOT NULL COMMENT 'vltrader alias id',
  `timestamp` timestamp NOT NULL DEFAULT NOW()  COMMENT 'timestamp',
  `heapmemoryconsumed` bigint(20) DEFAULT NULL COMMENT 'heap memory consumed in bytes',
  `permgenmemoryconsumed` bigint(20) DEFAULT NULL COMMENT 'perm gen memory consumed',
  PRIMARY KEY (`ID`),
  
  CONSTRAINT `memoryrec_ibfk_2` FOREIGN KEY (`vltraderID`) REFERENCES `vltraders` (`ID`)
)  ;

/*Table structure for table `protocol` */

DROP TABLE IF EXISTS `protocol`;

CREATE TABLE `protocol` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'protocol reference table incremental ID',
  `vltraderID` int(10) unsigned NOT NULL COMMENT 'vltrader alias id',
  `name` varchar(15) NOT NULL COMMENT 'protocol name',
  `portnumber` int(11) DEFAULT NULL COMMENT 'protocol port number',
  PRIMARY KEY (`ID`),
  
  CONSTRAINT `protocol_ibfk_1` FOREIGN KEY (`vltraderID`) REFERENCES `vltraders` (`ID`)
)  ;

/*Table structure for table `protocolconnections` */

DROP TABLE IF EXISTS `protocolconnections`;

CREATE TABLE `protocolconnections` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'protocol connection count incremental ID',
  `vltraderID` int(10) unsigned DEFAULT NULL COMMENT 'vltrader alias id',
  `protocolID` int(10) unsigned DEFAULT NULL COMMENT 'protocol table reference ID',
  `activeconnectioncount` int(11) DEFAULT NULL COMMENT 'count of active connections to this port',
  PRIMARY KEY (`ID`),
  
  CONSTRAINT `protocolconnections_ibfk_2` FOREIGN KEY (`vltraderID`) REFERENCES `vltraders` (`ID`),
  CONSTRAINT `protocolconnections_ibfk_1` FOREIGN KEY (`protocolID`) REFERENCES `protocol` (`ID`)
)  ;

/*Table structure for table `routedaction` */

DROP TABLE IF EXISTS `routedaction`;

CREATE TABLE `routedaction` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'scheduled action count incremental ID',
  `vltraderID` int(10) unsigned NOT NULL COMMENT 'vltrader alias id',
  `timestamp` timestamp NOT NULL DEFAULT NOW()  COMMENT 'timestamp',
  `rtrtotal` int(11) DEFAULT NULL COMMENT 'count of total scheduled actions',
  `rtractive` int(11) DEFAULT NULL COMMENT 'count of running scheduled actions',
  `rtrdisabled` int(11) DEFAULT NULL COMMENT 'count of disable scheduled actions',
  `rtrlastrestart` datetime DEFAULT NULL COMMENT 'datetime of last restart',
  PRIMARY KEY (`ID`),
  
  CONSTRAINT `routedaction_ibfk_1` FOREIGN KEY (`vltraderID`) REFERENCES `vltraders` (`ID`)
)  ;

/*Table structure for table `scheduledaction` */

DROP TABLE IF EXISTS `scheduledaction`;

CREATE TABLE `scheduledaction` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'scheduled action count incremental ID',
  `vltraderID` int(10) unsigned NOT NULL COMMENT 'vltrader alias id',
  `timestamp` timestamp NOT NULL DEFAULT NOW()  COMMENT 'timestamp',
  `schtotal` int(11) DEFAULT NULL COMMENT 'count of total scheduled actions',
  `schactive` int(11) DEFAULT NULL COMMENT 'count of running scheduled actions',
  `schdisabled` int(11) DEFAULT NULL COMMENT 'count of disable scheduled actions',
  `schcycletime` int(11) DEFAULT NULL COMMENT 'schedule cycle time in seconds',
  PRIMARY KEY (`ID`),
  
  CONSTRAINT `scheduledaction_ibfk_1` FOREIGN KEY (`vltraderID`) REFERENCES `vltraders` (`ID`)
)  ;

/*Table structure for table `statuscodes` */

DROP TABLE IF EXISTS `statuscodes`;

CREATE TABLE `statuscodes` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'status code incremental id',
  `status` varchar(50) NOT NULL COMMENT 'status verbage',
  PRIMARY KEY (`ID`)
)  ;

/*Table structure for table `storagerec` */

DROP TABLE IF EXISTS `storagerec`;

CREATE TABLE `storagerec` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'storage metric record auto increment ID',
  `vltraderID` int(11) unsigned NOT NULL COMMENT 'vltrader alias id',
  `timestamp` timestamp NOT NULL DEFAULT NOW()  COMMENT 'timestamp',
  `diskspaceused` bigint(20) DEFAULT NULL COMMENT 'storage space used in bytes',
  `totaldiskspace` bigint(20) DEFAULT NULL COMMENT 'total storagespace in bytes',
  PRIMARY KEY (`ID`),
  
  CONSTRAINT `storagerec_ibfk_1` FOREIGN KEY (`vltraderID`) REFERENCES `vltraders` (`ID`)
)  ;

/*Table structure for table `sychitems` */

DROP TABLE IF EXISTS `sychitems`;

CREATE TABLE `sychitems` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'synchronization items auto increment ID',
  `poolID` int(10) unsigned DEFAULT NULL COMMENT 'reference to vltpools id',
  `vltraderID` int(10) unsigned DEFAULT NULL COMMENT 'reference to vltrader alias',
  `timestamp` timestamp NOT NULL DEFAULT NOW()  COMMENT 'timestamp',
  `synchstatus` int(11) unsigned DEFAULT NULL COMMENT 'reference to statuscodes',
  `synchpending` int(11) DEFAULT NULL COMMENT 'count of synch items pending',
  `synchfailed` int(11) DEFAULT NULL COMMENT 'count of failed synch items',
  PRIMARY KEY (`ID`),
  
  CONSTRAINT `sychitems_ibfk_3` FOREIGN KEY (`synchstatus`) REFERENCES `statuscodes` (`ID`),
  CONSTRAINT `sychitems_ibfk_1` FOREIGN KEY (`vltraderID`) REFERENCES `vltraders` (`ID`),
  CONSTRAINT `sychitems_ibfk_2` FOREIGN KEY (`poolID`) REFERENCES `vltpools` (`ID`)
)  ;

/*Table structure for table `threadrec` */

DROP TABLE IF EXISTS `threadrec`;

CREATE TABLE `threadrec` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'thread metric record incremental ID',
  `vltraderID` int(11) unsigned NOT NULL COMMENT 'vltrader alias id',
  `timestamp` timestamp NOT NULL DEFAULT NOW()  COMMENT 'timestamp',
  `threadcount` int(11) DEFAULT NULL COMMENT 'thread count',
  `peakthreadcount` int(11) DEFAULT NULL COMMENT 'peak thread count',
  `deadlockedthreadcount` int(11) DEFAULT NULL COMMENT 'deadlocked thread count',
  PRIMARY KEY (`ID`),
  CONSTRAINT `threadrec_ibfk_1` FOREIGN KEY (`ID`) REFERENCES `vltraders` (`ID`)
)  ;

/*Table structure for table `uiconnections` */

DROP TABLE IF EXISTS `uiconnections`;

CREATE TABLE `uiconnections` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ui connection count autoincrement ID',
  `vltraderID` int(10) unsigned NOT NULL COMMENT 'vltrader alias ID',
  `uitypeID` int(10) unsigned NOT NULL COMMENT 'uitype ID',
  `timestamp` timestamp NOT NULL DEFAULT NOW()  COMMENT 'timestamp',
  `connectioncount` int(11) NOT NULL COMMENT 'count of connections to ui',
  PRIMARY KEY (`ID`),
  
  CONSTRAINT `uiconnections_ibfk_1` FOREIGN KEY (`vltraderID`) REFERENCES `vltraders` (`ID`),
  CONSTRAINT `uiconnections_ibfk_2` FOREIGN KEY (`uitypeID`) REFERENCES `uitypes` (`ID`)
)  ;

/*Table structure for table `uitypes` */

DROP TABLE IF EXISTS `uitypes`;

CREATE TABLE `uitypes` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ui type incremental ID',
  `interfaceType` varchar(25) DEFAULT NULL COMMENT 'interface type verbage',
  PRIMARY KEY (`ID`)
)  ;

/*Table structure for table `vltpools` */

DROP TABLE IF EXISTS `vltpools`;

CREATE TABLE `vltpools` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'vltrader sync pool incremental ID',
  `poolname` varchar(50) DEFAULT NULL COMMENT 'vltrader sync pool name',
  PRIMARY KEY (`ID`)
)  ;

/*Table structure for table `vltraders` */

DROP TABLE IF EXISTS `vltraders`;

CREATE TABLE `vltraders` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'vltrader autoincrement ID',
  `vltraderalias` char(25) DEFAULT NULL COMMENT 'vltrader alias',
  `vltraderserial` varchar(25) DEFAULT NULL COMMENT 'vltrader serial number',
  `poolID` int(10) unsigned DEFAULT NULL COMMENT 'synch pool ID',
  `schedulerstatus` int(11) unsigned DEFAULT NULL COMMENT 'scheduler status(tied to statuscodes)',
  `schlastrestart` datetime DEFAULT NULL COMMENT 'time of last scheduler restart',
  `routerstatus` int(11) unsigned DEFAULT NULL COMMENT 'router status(tied to statuscodes)',
  `routerlastrestart` datetime DEFAULT NULL COMMENT 'time of last router restart',
  `dbpayloadstatus` int(11) unsigned DEFAULT NULL COMMENT 'ID reference to statuscodes table',
  `dbpayloadconnection` int(11) DEFAULT NULL COMMENT 'ID reference to statuscodes',
  `dbpayloadbacklog` int(11) DEFAULT NULL COMMENT 'backlog count of database payload',
  `pendingdeletions` int(11) DEFAULT NULL COMMENT 'count of pending file deletions',
  PRIMARY KEY (`ID`),
  CONSTRAINT `vltraders_ibfk_2` FOREIGN KEY (`schedulerstatus`) REFERENCES `statuscodes` (`ID`),
  CONSTRAINT `vltraders_ibfk_3` FOREIGN KEY (`routerstatus`) REFERENCES `statuscodes` (`ID`),
  CONSTRAINT `vltraders_ibfk_4` FOREIGN KEY (`dbpayloadstatus`) REFERENCES `statuscodes` (`ID`),
  CONSTRAINT `vltraders_ibfk_1` FOREIGN KEY (`poolID`) REFERENCES `vltpools` (`ID`)
)  ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
