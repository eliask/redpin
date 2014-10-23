CREATE DATABASE IF NOT EXISTS redpin;
USE redpin;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `bluetoothreading`;
CREATE TABLE `bluetoothreading` (
  `bluetoothReadingId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `friendlyName` varchar(45) DEFAULT NULL,
  `bluetoothAddress` varchar(45) DEFAULT NULL,
  `majorDeviceClass` varchar(45) DEFAULT NULL,
  `minorDeviceClass` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`bluetoothReadingId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `fingerprint`;
CREATE TABLE `fingerprint` (
  `fingerprintId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `locationId` int(10) unsigned DEFAULT NULL,
  `measurementId` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`fingerprintId`),
  CONSTRAINT `location` FOREIGN KEY (`locationId`) REFERENCES `location` (`locationId`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `measurement` FOREIGN KEY (`measurementId`) REFERENCES `measurement` (`measurementId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `gsmreading`;
CREATE TABLE `gsmreading` (
  `gsmReadingId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `cellId` varchar(45) DEFAULT NULL,
  `areaId` varchar(45) DEFAULT NULL,
  `signalStrength` varchar(45) DEFAULT NULL,
  `MCC` varchar(45) DEFAULT NULL,
  `MNC` varchar(45) DEFAULT NULL,
  `networkName` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`gsmReadingId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `location`;
CREATE TABLE `location` (
  `locationId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `symbolicId` varchar(90) DEFAULT NULL,
  `mapId` int(10) unsigned DEFAULT NULL,
  `mapXCord` int(10) unsigned DEFAULT NULL,
  `mapYCord` int(10) unsigned DEFAULT NULL,
  `accuracy` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`locationId`),
  KEY `symbolicId` (`symbolicId`),
  KEY `map` (`mapId`),
  CONSTRAINT `map` FOREIGN KEY (`mapId`) REFERENCES `map` (`mapId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `map`;
CREATE TABLE `map` (
  `mapId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `mapName` varchar(45) DEFAULT NULL,
  `mapURL` text,
  PRIMARY KEY (`mapId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `measurement`;
CREATE TABLE `measurement` (
  `measurementId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `timestamp` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`measurementId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `readinginmeasurement`;
CREATE TABLE `readinginmeasurement` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `measurementId` int(10) unsigned NOT NULL,
  `readingId` int(10) unsigned NOT NULL,
  `readingClassName` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `measurement` (`measurementId`),
  KEY `reading` (`readingId`),
  CONSTRAINT `measurementFk` FOREIGN KEY (`measurementId`) REFERENCES `measurement` (`measurementId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `wifireading`;
CREATE TABLE `wifireading` (
  `wifiReadingId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `bssid` varchar(45) DEFAULT NULL,
  `ssid` varchar(45) DEFAULT NULL,
  `rssi` int(10) signed DEFAULT NULL,
  `wepEnabled` tinyint(1) DEFAULT NULL,
  `isInfrastructure` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`wifiReadingId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

SET FOREIGN_KEY_CHECKS = 1;
