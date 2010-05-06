CREATE TABLE IF NOT EXISTS 'bluetoothreading' (
  'bluetoothReadingId' INTEGER PRIMARY KEY,
  'friendlyName' varchar(45) NOT NULL,
  'bluetoothAddress' varchar(45) NOT NULL,
  'majorDeviceClass' varchar(45) NOT NULL,
  'minorDeviceClass' varchar(45) NOT NULL
) ;
CREATE TABLE IF NOT EXISTS 'fingerprint' (
  'fingerprintId' INTEGER PRIMARY KEY, 
  'locationId' int(10) NOT NULL,
  'measurementId' int(10) NOT NULL
) ;
CREATE TABLE IF NOT EXISTS 'gsmreading' (
  'gsmReadingId' INTEGER PRIMARY KEY,
  'cellId' varchar(45) NOT NULL,
  'areaId' varchar(45) NOT NULL,
  'signalStrength' varchar(45) NOT NULL,
  'MCC' varchar(45) NOT NULL,
  'MNC' varchar(45) NOT NULL,
  'networkName' varchar(45) NOT NULL
) ;
CREATE TABLE IF NOT EXISTS 'location' (
  'locationId' INTEGER PRIMARY KEY,
  'symbolicId' varchar(90) NOT NULL,
  'mapId' int(10) NOT NULL,
  'mapXCord' int(10) NOT NULL,
  'mapYCord' int(10) NOT NULL,
  'accuracy' int(10) NOT NULL
) ;
CREATE TABLE IF NOT EXISTS 'map' (
  'mapId' INTEGER PRIMARY KEY,
  'mapName' varchar(45) NOT NULL,
  'mapURL' varchar(200) NOT NULL
) ;
CREATE TABLE IF NOT EXISTS 'measurement' (
  'measurementId' INTEGER PRIMARY KEY,
  'timestamp' bigint(20) NOT NULL
) ;
CREATE TABLE IF NOT EXISTS 'readinginmeasurement' (
  'id' INTEGER PRIMARY KEY,
  'measurementId' int(10) NOT NULL,
  'readingId' int(10) NOT NULL,
  'readingClassName' varchar(255)  NOT NULL default ''
) ;
CREATE TABLE IF NOT EXISTS 'wifireading' (
  'wifiReadingId' INTEGER PRIMARY KEY,
  'bssid' varchar(45) NOT NULL,
  'ssid' varchar(45) NOT NULL,
  'rssi' int(10) NOT NULL,
  'wepEnabled' tinyint(1) NOT NULL,
  'isInfrastructure' varchar(45) NOT NULL
) ;