@AOSETTABLE CREATE TABLE `users` (
  `Id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Name` varchar(120) NOT NULL,
  `Age` int(10) unsigned NOT NULL DEFAULT '0',
  `LastLogin` timestamp NOT NULL DEFAULT '1971-01-01 00:00:00',
  PRIMARY KEY (`Id`),
  UNIQUE KEY `UniqueName_UNIQUE` (`Name`),
  KEY `Index 3` (`Age`)
);

@ARSETTABLE CREATE TABLE `person` (
  `pId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  @LWWSTRING `UniqueName` varchar(120) NOT NULL,
  @LWWINTEGER `Salary` int(10) unsigned NOT NULL DEFAULT '0',
  @LWWDATETIME `DateTimeLastLogin` timestamp NOT NULL DEFAULT '1971-01-01 00:00:00',
  @NUMDELTAINTEGER `YourAge` int(10),
  @NUMDELTAFLOAT `Nice` float(10),
  PRIMARY KEY (`pId`)
);

@ARSETTABLE CREATE TABLE `best` (
  `bId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  @LWWSTRING `great` varchar(120) NOT NULL,
  @LWWINTEGER `hah` int(10) unsigned NOT NULL DEFAULT '0',
  @LWWDATETIME `maybe` timestamp NOT NULL DEFAULT '1971-01-01 00:00:00',
  @LWWFLOAT `go` float(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`bId`)
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