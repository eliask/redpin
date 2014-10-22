begin;
-- CREATE DATABASE redpin;

CREATE TABLE "map" (
  "mapId" serial primary key,
  "mapName" varchar(45) not null,
  "mapURL" text not null
);

CREATE TABLE "location" (
  "locationId" serial primary key,
  "symbolicId" varchar(90) not null,
  "mapId" integer not null,
  "mapXCord" integer not null,
  "mapYCord" integer not null,
  "accuracy" integer not null,
  -- KEY "symbolicId" ("symbolicId"),
  -- KEY "map" ("mapId"),
  CONSTRAINT "map" FOREIGN KEY ("mapId") REFERENCES "map" ("mapId") ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE "bluetoothreading" (
  "bluetoothReadingId" serial primary key,
  "friendlyName" varchar(45) not null,
  "bluetoothAddress" varchar(45) not null,
  "majorDeviceClass" varchar(45) not null,
  "minorDeviceClass" varchar(45) not null
);

CREATE TABLE "measurement" (
  "measurementId" serial primary key,
  "timestamp" bigint not null
);

CREATE TABLE "fingerprint" (
  "fingerprintId" serial primary key,
  "locationId" integer not null,
  "measurementId" integer not null,
  CONSTRAINT "location" FOREIGN KEY ("locationId") REFERENCES "location" ("locationId") ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT "measurement" FOREIGN KEY ("measurementId") REFERENCES "measurement" ("measurementId") ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE "gsmreading" (
  "gsmReadingId" serial primary key,
  "cellId" varchar(45) not null,
  "areaId" varchar(45) not null,
  "signalStrength" varchar(45) not null,
  "MCC" varchar(45) not null,
  "MNC" varchar(45) not null,
  "networkName" varchar(45) not null
);

CREATE TABLE "readinginmeasurement" (
  "id" serial primary key,
  "measurementId" integer NOT NULL,
  "readingId" integer NOT NULL,
  "readingClassName" varchar(255) NOT NULL,
  -- KEY "measurement" ("measurementId"),
  -- KEY "reading" ("readingId"),
  CONSTRAINT "measurementFk" FOREIGN KEY ("measurementId") REFERENCES "measurement" ("measurementId") ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE "wifireading" (
  "wifiReadingId" serial primary key,
  "bssid" varchar(45) not null,
  "ssid" varchar(45) not null,
  "rssi" integer not null,
  "wepEnabled" boolean not null,
  "isInfrastructure" boolean not null
);

commit;
