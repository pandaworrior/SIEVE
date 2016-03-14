@AOSETTABLE CREATE TABLE `users` (
  `Id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  @LWWSTRING `UniqueName` varchar(120) NOT NULL,
  @NUMDELTAINTEGER `NoTimesLoggedIn` int(10) unsigned NOT NULL DEFAULT '0',
  @LWWDATETIME `DateTimeLastLogin` timestamp NOT NULL DEFAULT '1971-01-01 00:00:00',
  @LWWSTRING `TimeZoneId` varchar(255) NOT NULL DEFAULT 'UTC',
  @LWWINTEGER `CultureInfoId` int(10) unsigned NOT NULL DEFAULT '0',
  @LWWDATETIME `DateLastUpdated` datetime DEFAULT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `UniqueName_UNIQUE` (`UniqueName`),
  KEY `Index 3` (`SiteName`)
);

@ARSETTABLE CREATE TABLE `person` (
  `Id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `UUId` int(20) unsigned NOT NULL AUTO_INCREMENT,
  @LWWSTRING `UniqueName` varchar(120) NOT NULL,
  @NUMDELTAINTEGER `NoTimesLoggedIn` int(10) unsigned NOT NULL DEFAULT '0',
  @LWWDATETIME `DateTimeLastLogin` timestamp NOT NULL DEFAULT '1971-01-01 00:00:00',
  @LWWSTRING `TimeZoneId` varchar(255) NOT NULL DEFAULT 'UTC',
  @LWWINTEGER `CultureInfoId` int(10) unsigned NOT NULL DEFAULT '0',
  @LWWDATETIME `DateLastUpdated` datetime DEFAULT NULL,
  PRIMARY KEY (`Id`, `UUId`),
  UNIQUE KEY `UniqueName_UNIQUE` (`UniqueName`),
  KEY `Index 3` (`SiteName`)
);

CREATE TABLE `dog` (
  `Id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `UniqueName` varchar(120) NOT NULL,
  `NoTimesLoggedIn` int(10) unsigned NOT NULL DEFAULT '0',
  `DateTimeLastLogin` timestamp NOT NULL DEFAULT '1971-01-01 00:00:00',
  `TimeZoneId` varchar(255) NOT NULL DEFAULT 'UTC',
  `CultureInfoId` int(10) unsigned NOT NULL DEFAULT '0',
  `DateLastUpdated` datetime DEFAULT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `UniqueName_UNIQUE` (`UniqueName`),
  KEY `Index 3` (`SiteName`)
)