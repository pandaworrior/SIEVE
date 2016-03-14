CREATE TABLE `users` (
  `Id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Name` varchar(120) NOT NULL,
  `Age` int(10) unsigned NOT NULL DEFAULT '0',
  `LastLogin` timestamp NOT NULL DEFAULT '1971-01-01 00:00:00',
  `_SP_del` bool NOT NULL DEFAULT '0',
  `_SP_clock` varchar(100) NOT NULL DEFAULT '0-0',
  `_SP_ts` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`Id`),
  UNIQUE KEY `UniqueName_UNIQUE` (`Name`),
  KEY `Index 3` (`Age`)
);

CREATE TABLE `person` (
  `pId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `UniqueName` varchar(120) NOT NULL,
   `Salary` int(10) unsigned NOT NULL DEFAULT '0',
   `DateTimeLastLogin` timestamp NOT NULL DEFAULT '1971-01-01 00:00:00',
   `YourAge` int(10) NULL DEFAULT '0',
   `Nice` float(10) NULL DEFAULT '0.0',
     `_SP_del` bool NOT NULL DEFAULT '0',
  `_SP_clock` varchar(100) NOT NULL DEFAULT '0-0',
  `_SP_ts` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`pId`)
);

insert into users (Id, Name, Age, LastLogin,  _SP_del , _SP_clock, _SP_ts) values (1, 'ChengLi', 20, '2013-01-01 00:00:00', false, '0-0',0);
insert into person(pId, UniqueName, Salary, DateTimeLastLogin, YourAge, Nice,  _SP_del , _SP_clock, _SP_ts) values(10, 'JD', 150000, '2013-01-01 00:00:00', 26, 1.8, false, '0-0',0);