CREATE DATABASE IF NOT EXISTS redpin;
USE redpin;
CREATE TABLE  `redpin`.`bluetoothreading` (
  `bluetoothReadingId` int(10) unsigned auto_increment,
  `friendlyName` varchar(45),
  `bluetoothAddress` varchar(45),
  `majorDeviceClass` varchar(45),
  `minorDeviceClass` varchar(45),
  PRIMARY KEY  (`bluetoothReadingId`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
CREATE TABLE  `redpin`.`fingerprint` (
  `fingerprintId` int(10) unsigned auto_increment,
  `locationId` int(10) unsigned,
  `measurementId` int(10) unsigned,
  PRIMARY KEY  (`fingerprintId`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
CREATE TABLE  `redpin`.`gsmreading` (
  `gsmReadingId` int(10) unsigned auto_increment,
  `cellId` varchar(45),
  `areaId` varchar(45),
  `signalStrength` varchar(45),
  `MCC` varchar(45),
  `MNC` varchar(45),
  `networkName` varchar(45),
  PRIMARY KEY  (`gsmReadingId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `redpin`.`location` (
  `locationId` int(10) unsigned auto_increment,
  `symbolicId` varchar(90),
  `mapId` int(10) unsigned,
  `mapXCord` int(10) unsigned,
  `mapYCord` int(10) unsigned,
  `accuracy` int(10) unsigned,
  PRIMARY KEY  (`locationId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `redpin`.`map` (
  `mapId` int(10) unsigned auto_increment,
  `mapName` varchar(45),
  `mapURL` varchar(200),
  PRIMARY KEY  (`mapId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `redpin`.`measurement` (
  `measurementId` int(10) unsigned auto_increment,
  `timestamp` bigint(20) unsigned,
  `wifiReadingsvectorId` int(10) unsigned,
  `gsmReadingsvectorId` int(10) unsigned,
  `bluetoothReadingsvectorId` int(10) unsigned,
  PRIMARY KEY  (`measurementId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `redpin`.`serializablevector` (
  `vectorId` int(9) unsigned auto_increment,
  `size` int(9) default '0',
  `containedObjectsClassName` varchar(255) character set latin1 collate latin1_general_ci default '',
  `containedObjectIds` varchar(255) character set latin1 collate latin1_general_ci default '',
  PRIMARY KEY  (`vectorId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `redpin`.`wifireading` (
  `wifiReadingId` int(10) unsigned auto_increment,
  `bssid` varchar(45),
  `ssid` varchar(45),
  `rssi` int(10) unsigned,
  `wepEnabled` tinyint(1),
  `isInfrastructure` varchar(45),
  PRIMARY KEY  (`wifiReadingId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
