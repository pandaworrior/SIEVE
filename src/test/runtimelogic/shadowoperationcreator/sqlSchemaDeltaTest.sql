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