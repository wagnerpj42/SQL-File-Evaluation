-- CS 260, Spring 2019
-- Lab Test Banking data script

-- 1. Cleanup (drop any existing tables)
DROP TABLE Transaction;
DROP TABLE Account;
DROP TABLE Customer;

-- 2. Create tables
-- create table for Customer rows
CREATE TABLE Customer
(CustID     CHAR(9), 
 FName      VARCHAR(15) NOT NULL,
 LName      VARCHAR(15) NOT NULL,
 PIN        CHAR(4)     NOT NULL,
 PRIMARY KEY (CustID)
); 

-- create table for Account rows
CREATE TABLE Account
(AccNumber   INTEGER,
 AccType     VARCHAR(8)  NOT NULL CHECK (AccType IN ('savings', 'checking')),
 AccBalance  NUMBER(*,2) NOT NULL,
 Customer    CHAR(9)     NOT NULL,
 AccOpenLocation  VARCHAR(15) NOT NULL,
 AccOpenDate DATE        NOT NULL,
 AccClosedDate DATE              ,
 AccStatus   VARCHAR(7)  NOT NULL CHECK (AccStatus IN ('Active','Closed','Frozen')),
 PRIMARY KEY (AccNumber),
 FOREIGN KEY (Customer) REFERENCES Customer(CustID)
);

-- create table for Transaction rows
CREATE TABLE Transaction
(TransID     INTEGER,
 AccNumber   INTEGER     NOT NULL,
 TransAmount NUMBER(*,2) NOT NULL,
 TransDateTime DATE      NOT NULL,
 TransType   CHAR        NOT NULL CHECK (TransType IN ('w', 'd')),
 TransLocation VARCHAR(15) NOT NULL,
 PRIMARY KEY (TransID),
 FOREIGN KEY (AccNumber) REFERENCES Account(AccNumber)
);


-- 3. Insert sample data rows
-- Inserting data into Customer table (CustID, FName, LName, PIN)
INSERT INTO Customer VALUES ('111564812','John','Williams','3574');
INSERT INTO Customer VALUES ('125487874','Gene','Edwards','5141');
INSERT INTO Customer VALUES ('151135593','Xiaoying','Wang','2234');
INSERT INTO Customer VALUES ('154879887','Dorthy','Lewis','3341');
INSERT INTO Customer VALUES ('160839453','Charles','Harris','2411');
INSERT INTO Customer VALUES ('190873519','Elizabeth','Taylor','1730');
INSERT INTO Customer VALUES ('198784544','Eric','Collins','4110');
INSERT INTO Customer VALUES ('112348546','Jianjun','Ju','3499');
INSERT INTO Customer VALUES ('115987938','Christopher','Garcia','6054');
INSERT INTO Customer VALUES ('128778823','William','Ward','3334');
INSERT INTO Customer VALUES ('132977562','Angela','Martinez','3134');
INSERT INTO Customer VALUES ('141582651','Mary','Johnson','9401');
INSERT INTO Customer VALUES ('141582657','Stanley','Browne','3409');
INSERT INTO Customer VALUES ('142519864','Susan','Martin','5699');
INSERT INTO Customer VALUES ('156465461','Eric','Cooper','2699');
INSERT INTO Customer VALUES ('156489494','Gil','Richardson','7010');
INSERT INTO Customer VALUES ('159542516','Matt','Nelson','4899');
INSERT INTO Customer VALUES ('174454898','Noor','Chua','5283');
INSERT INTO Customer VALUES ('178949844','Chad','Stewart','9980');
INSERT INTO Customer VALUES ('179887494','Dorothy','Howard','4031');
INSERT INTO Customer VALUES ('242518965','James','Smith','7709');
INSERT INTO Customer VALUES ('248965255','Barbara','Wilson','4895');
INSERT INTO Customer VALUES ('254099823','Patricia','Jones','4278');
INSERT INTO Customer VALUES ('254898318','Gim','Rogers','5175');
INSERT INTO Customer VALUES ('267894232','Paul','Hall','4998');
INSERT INTO Customer VALUES ('269734834','Rick','Carter','5555');
INSERT INTO Customer VALUES ('274878974','Harry','Watson','3670');
INSERT INTO Customer VALUES ('280158572','Margaret','Clark','4449');
INSERT INTO Customer VALUES ('287321212','Michael','Miller','3107');
INSERT INTO Customer VALUES ('289562686','Thomas','Robinson','4175');
INSERT INTO Customer VALUES ('291795563','Haywood','Kelly','6327');
INSERT INTO Customer VALUES ('298489484','Lisa','Gray','1799');
INSERT INTO Customer VALUES ('301221823','Juan','Rodriguez','2175');
INSERT INTO Customer VALUES ('310454876','Milo','Brooks','2399');
INSERT INTO Customer VALUES ('318548912','Ann','Mitchell','1750');
INSERT INTO Customer VALUES ('320874981','Daniel','Lee','3300');
INSERT INTO Customer VALUES ('322654189','Lisa','Walker','2175');
INSERT INTO Customer VALUES ('334568786','William','Moore','0050');
INSERT INTO Customer VALUES ('348121549','Trey','Phillips','1122');
INSERT INTO Customer VALUES ('351565322','Nancy','Allen','9910');
INSERT INTO Customer VALUES ('355548984','Tom','Murphy','4175');
INSERT INTO Customer VALUES ('356187925','Robert','Brown','8431');
INSERT INTO Customer VALUES ('390487451','Mark','Coleman','9910');
INSERT INTO Customer VALUES ('451519864','Mark','Young','4455');
INSERT INTO Customer VALUES ('454565232','Louis','Jenkins','7878');
INSERT INTO Customer VALUES ('455798411','Luis','Hernandez','4994');
INSERT INTO Customer VALUES ('486512566','David','Anderson','5199');
INSERT INTO Customer VALUES ('489221823','Richard','Jackson','2996');
INSERT INTO Customer VALUES ('489456522','Linda','Davis','5971');
INSERT INTO Customer VALUES ('548977562','Donald','King','4392');
INSERT INTO Customer VALUES ('550156548','George','Wright','4108');
INSERT INTO Customer VALUES ('552455318','Ana','Lopez','8822');
INSERT INTO Customer VALUES ('556784565','Kenneth','Hill','8410');
INSERT INTO Customer VALUES ('567354612','Karen','Scott','7991');
INSERT INTO Customer VALUES ('573284890','Dimitri','Kaftos','2939');
INSERT INTO Customer VALUES ('574489456','Betty','Adams','3991');
INSERT INTO Customer VALUES ('578875478','Edward','Baker','1010');
INSERT INTO Customer VALUES ('619023588','Jennifer','Thomas','3465');
INSERT INTO Customer VALUES ('135645489','Edward','Chi','2540');

-- Inserting data into Account table (AccNumber,AccType,AccBalance,Customer,AccOpenLocation,AccOpenDate,AccClosedDate,AccStatus)
INSERT INTO Account VALUES(867543,'savings', 256.93,'111564812' ,'SB1','11-DEC-2016', null, 'Active');
INSERT INTO Account VALUES(263521,'checking',1055.27,'141582651', 'Central','13-OCT-2016', '30-APR-2018', 'Closed');
INSERT INTO Account VALUES(818726,'checking',572.89,'141582657', 'Central','11-MAR-2017', null, 'Active');
INSERT INTO Account VALUES(587253,'savings',1041.99, 135645489,'SB4','4-MAR-2017', null, 'Active');
INSERT INTO Account VALUES(264357,'savings', 5.05, 151135593,'SB2','15-AUG-2016', null, 'Active');
INSERT INTO Account VALUES(362743,'savings',25401.02,190873519,'Central','7-MAR-2017', null, 'Active');
INSERT INTO Account VALUES(928745,'savings',500.00, 154879887,'Central','19-JAN-2017', null, 'Active');
INSERT INTO Account VALUES(426573,'savings',35781.95, 112348546,'SB4','11-MAR-2017', null, 'Active');
INSERT INTO Account VALUES(265843,'savings',1.01, 115987938,'Central','11-MAR-2017', null, 'Active');
INSERT INTO Account VALUES(282828,'savings',783.27, 190873519,'SB3','15-MAY-2017', null, 'Active');
INSERT INTO Account VALUES(119928,'savings',23.43, 125487874,'Central','15-MAY-2017', null, 'Active');
INSERT INTO Account VALUES(282736,'checking',0.01, 111564812,'SB1','11-DEC-2017', null, 'Active');
INSERT INTO Account VALUES(294358,'checking',987.64, 135645489,'SB4','22-FEB-2017', null, 'Active');
INSERT INTO Account VALUES(337567,'checking', 235.77, 132977562,'SB3','23-MAR-2017', null, 'Active');
INSERT INTO Account VALUES(111223,'savings',100.00, 141582651,'SB4','13-OCT-2016', null, 'Active');
INSERT INTO Account VALUES(354345,'checking', 891.77,111564812, 'SB4','19-MAR-2017', null,'Active');
INSERT INTO Account VALUES(777723,'checking',1317.08, 190873519,'SB3','11-MAR-2017', null, 'Active');
INSERT INTO Account VALUES(298475,'checking',10.20, 190873519,'Central','19-JAN-2017', null, 'Active');
INSERT INTO Account VALUES(999924,'checking',556.77, 160839453,'SB3','11-MAR-2017', null, 'Active');
INSERT INTO Account VALUES(772645,'checking', 982.66, 174454898,'SB1','11-DEC-2016', null, 'Active');
INSERT INTO Account VALUES(232323,'checking', 131.85, 198784544,'SB2','29-JAN-2017',null, 'Active');
INSERT INTO Account VALUES(525342,'savings', 400.20, 198784544,'SB2','11-MAR-2017',null, 'Active');
INSERT INTO Account VALUES(173642,'checking',365.21,115987938, 'Central','8-MAR-2017',null, 'Active');
INSERT INTO Account VALUES(273646,'checking',123.45, 115987938,'SB2','11-MAR-2006',null, 'Active');
INSERT INTO Account VALUES(182743,'checking', 99.00, 128778823,'Central','7-MAR-2017',null, 'Active');
INSERT INTO Account VALUES(273654,'checking',28.14, 242518965,'SB3','29-MAR-2017',null, 'Active');
INSERT INTO Account VALUES(173264,'savings', 0.00, 132977562,'Central','13-NOV-2016','22-MAY-2018', 'Closed');
INSERT INTO Account VALUES(283747,'savings',1815.28, 141582651,'Central','5-JAN-2017',null, 'Active');
INSERT INTO Account VALUES(827436,'savings', 118.18, 142519864,'SB2','15-MAY-2017',null, 'Active');
INSERT INTO Account VALUES(293847,'checking',2040.27,142519864,'SB2','15-MAY-2017',null, 'Active');
INSERT INTO Account VALUES(127272,'savings',12638.00,156465461,'SB1','19-JAN-2017',null, 'Active');
INSERT INTO Account VALUES(152434,'checking',3541.92,156489494,'SB1','11-MAR-2017',null,'Active');
INSERT INTO Account VALUES(183746,'savings',28943.01,179887494,'SB1','15-JAN-2017',null,'Active');
INSERT INTO Account VALUES(172643,'checking',893.41,179887494,'SB1','11-MAR-2017',null,'Active');
INSERT INTO Account VALUES(183274,'checking',1000.14,159542516,'Central','17-APR-2017',null,'Active');
INSERT INTO Account VALUES(283274,'savings',798.10,159542516,'Central','3-MAY-2017',null,'Active');
INSERT INTO Account VALUES(183747,'checking',25801.33,248965255,'Central','9-MAR-2017',null,'Active');
INSERT INTO Account VALUES(888776,'savings',109.82,254099823,'SB3','22-MAR-2017',null,'Active');
INSERT INTO Account VALUES(273645,'savings',897.78,178949844,'SB3','11-DEC-2016',null,'Active');
INSERT INTO Account VALUES(283746,'checking',1213.15,254898318,'SB3','11-MAR-2017',null,'Active');
INSERT INTO Account VALUES(274364,'checking',367.85,267894232,'SB3','25-MAR-2017',null,'Active');
INSERT INTO Account VALUES(269734,'checking',80.90,269734834,'SB4','16-MAR-2017',null,'Active');
INSERT INTO Account VALUES(369734,'savings',619.73,269734834,'SB4','5-JAN-2017',null,'Active');
INSERT INTO Account VALUES(776665,'savings',581.11,274878974,'SB5','23-MAY-2017',null,'Active');
INSERT INTO Account VALUES(889988,'savings',731.92,280158572,'SB5','11-MAY-2017',null,'Active');
INSERT INTO Account VALUES(363636,'checking',1515.23,280158572,'SB5','17-MAR-2017',null,'Active');
INSERT INTO Account VALUES(353534,'checking',7898.66,287321212,'Central','11-FEB-2017',null,'Active');
INSERT INTO Account VALUES(444455,'savings',515.82,289562686,'Central','11-JAN-2017',null,'Active');
INSERT INTO Account VALUES(663355,'savings',454.34,291795563,'SB1','12-MAY-2017',null,'Active');
INSERT INTO Account VALUES(776655,'savings',188.93,298489484,'SB2','12-MAY-2017',null,'Active');
INSERT INTO Account VALUES(998877,'savings',97.05,301221823,'SB2','1-May-2017',null,'Active');
INSERT INTO Account VALUES(383838,'savings',789.03,310454876,'SB3','3-May-2017',null,'Active');
INSERT INTO Account VALUES(121212,'savings',12015.12,318548912,'SB4','3-OCT-2016',null,'Active');
INSERT INTO Account VALUES(524342,'savings',897.79,320874981,'Central','15-SEP-2016',null, 'Active');
INSERT INTO Account VALUES(535353,'checking',1293.44,322654189,'Central','11-DEC-2016',null, 'Active');
INSERT INTO Account VALUES(654655,'checking',1010.10,334568786,'Central','23-MAR-2017',null,'Active');
INSERT INTO Account VALUES(654654,'savings',334.56,348121549,'Central','10-MAY-2017',null,'Active');
INSERT INTO Account VALUES(777777,'savings',101.01,351565322,'Central','11-MAR-2017',null,'Active');
INSERT INTO Account VALUES(666666,'checking',387.29,355548984,'SB2','23-JAN-2017',null,'Active');
INSERT INTO Account VALUES(555555,'savings',997.73,356187925,'SB1','22-JAN-2017',null,'Active');
INSERT INTO Account VALUES(444444,'savings',448.81,390487451,'SB5','31-JAN-2017',null,'Active');
INSERT INTO Account VALUES(333333,'savings',111.12,451519864,'Central','12-APR-2017',null,'Active');
INSERT INTO Account VALUES(222222,'savings',878.87,454565232,'SB1','11-OCT-2016',null,'Active');
INSERT INTO Account VALUES(222221,'checking',772.18,454565232,'SB1','10-DEC-2016',null,'Active');
INSERT INTO Account VALUES(222220,'savings',31.22,454565232,'SB2','10-JAN-2017',null,'Active');
INSERT INTO Account VALUES(333111,'savings',1414.14,455798411,'SB5','9-DEC-2016',null,'Active');
INSERT INTO Account VALUES(111333,'checking',1237.97,489221823,'SB3','9-FEB-2017',null,'Active');
INSERT INTO Account VALUES(321321,'checking',814.85,489456522,'SB1','8-MAR-2017',null,'Active');
INSERT INTO Account VALUES(123123,'checking',919.91,548977562,'SB1','5-JAN-2017',null,'Active');
INSERT INTO Account VALUES(321456,'checking',984.82,550156548,'SB2','5-JAN-2017',null,'Active');
INSERT INTO Account VALUES(456545,'savings',131.49,552455318,'SB2','5-JAN-2017',null,'Active');
INSERT INTO Account VALUES(666555,'savings',23.45,556784565,'SB5','5-MAR-2017',null,'Active');
INSERT INTO Account VALUES(646464,'checking',87.71,567354612,'Central','28-JAN-2017',null,'Active');
INSERT INTO Account VALUES(575757,'savings',989.71,573284890,'Central','27-JAN-2017',null,'Active');
INSERT INTO Account VALUES(737373,'checking',841.19,574489456,'Central','13-JAN-2017',null,'Active');
INSERT INTO Account VALUES(828282,'checking',148.82,578875478,'SB2','18-JAN-2017',null,'Active');
INSERT INTO Account VALUES(919191,'checking',0.00,619023588,'SB3','22-APR-2016','20-DEC-2018','Closed');
INSERT INTO Account VALUES(263777,'checking',-25.00,141582651,'Central','13-OCT-2018',null, 'Frozen');

-- insert data into Transaction table (TransID, AccNumber, TransAmount, TransDateTime, TransType, TransLocation)
INSERT INTO Transaction VALUES(1,282736,500,'14-MAR-2018','w','SB2');
INSERT INTO Transaction VALUES(2,919191,500,'22-APR-2018','d','Online');
INSERT INTO Transaction VALUES(3,264357,4000,'15-AUG-2018','d','SB2');
INSERT INTO Transaction VALUES(4,524342,4981,'15-SEP-2018','d','Central');
INSERT INTO Transaction VALUES(5,121212,3185,'3-OCT-2018','d','SB4');
INSERT INTO Transaction VALUES(6,222222,232,'11-OCT-2018','d','SB1');
INSERT INTO Transaction VALUES(7,111223,1415,'13-OCT-2018','d','Online');
INSERT INTO Transaction VALUES(8,173264,2000,'13-NOV-2018','d','Central');
INSERT INTO Transaction VALUES(9,264357,5000,'25-NOV-2018','d','SB2');
INSERT INTO Transaction VALUES(10,333111,400,'9-DEC-2018','d','SB5');
INSERT INTO Transaction VALUES(11,222221,454,'10-DEC-2018','d','SB1');
INSERT INTO Transaction VALUES(12,535353,189,'11-DEC-2018','d','Online');
INSERT INTO Transaction VALUES(13,273645,498.45,'11-DEC-2018','d','SB3');
INSERT INTO Transaction VALUES(14,282736,1000,'11-DEC-2018','d','SB1');
INSERT INTO Transaction VALUES(15,772645,600,'11-DEC-2018','d','SB1');
INSERT INTO Transaction VALUES(16,919191,500,'20-DEC-2018','w','Online');
INSERT INTO Transaction VALUES(17,283747,145,'5-JAN-2018','d','Online');
INSERT INTO Transaction VALUES(18,123123,548.95,'5-JAN-2018','d','SB1');
INSERT INTO Transaction VALUES(19,321456,3500,'5-JAN-2018','d','SB2');
INSERT INTO Transaction VALUES(20,456545,552.45,'5-JAN-2018','d','SB2');
INSERT INTO Transaction VALUES(21,369734,834,'5-JAN-2018','d','ATM03');
INSERT INTO Transaction VALUES(22,222220,40000,'10-JAN-2018','d','Online');
INSERT INTO Transaction VALUES(23,444455,686,'11-JAN-2018','d','Central');
INSERT INTO Transaction VALUES(24,737373,9456,'13-JAN-2018','d','Online');
INSERT INTO Transaction VALUES(25,183746,175,'15-JAN-2018','d','SB1');
INSERT INTO Transaction VALUES(26,828282,7500,'18-JAN-2018','d','ATM03');
INSERT INTO Transaction VALUES(27,928745,2300,'19-JAN-2018','d','Online');
INSERT INTO Transaction VALUES(28,298475,1900,'19-JAN-2018','d','Central');
INSERT INTO Transaction VALUES(29,127272,150,'19-JAN-2018','d','SB1');
INSERT INTO Transaction VALUES(30,928745,2300,'19-JAN-2018','d','SB1');
INSERT INTO Transaction VALUES(31,555555,925,'22-JAN-2018','d','ATM01');
INSERT INTO Transaction VALUES(32,666666,5489.85,'23-JAN-2018','d','ATM03');
INSERT INTO Transaction VALUES(33,575757,890,'27-JAN-2018','d','Central');
INSERT INTO Transaction VALUES(34,646464,612,'28-JAN-2018','d','Online');
INSERT INTO Transaction VALUES(35,232323,750,'29-JAN-2018','d','SB2');
INSERT INTO Transaction VALUES(36,444444,10000,'31-JAN-2018','d','ATM02');
INSERT INTO Transaction VALUES(37,111333,823,'9-FEB-2018','d','SB3');
INSERT INTO Transaction VALUES(38,353534,212,'11-FEB-2018','d','Online');
INSERT INTO Transaction VALUES(39,294358,5500,'22-FEB-2018','d','Online');
INSERT INTO Transaction VALUES(40,587253,5000,'4-MAR-2018','d','SB4');
INSERT INTO Transaction VALUES(41,666555,84565,'5-MAR-2018','d','SB5');
INSERT INTO Transaction VALUES(42,362743,3000,'7-MAR-2018','d','Online');
INSERT INTO Transaction VALUES(43,362743,300,'7-MAR-2018','w','ATM02');
INSERT INTO Transaction VALUES(44,182743,8823,'7-MAR-2018','d','Central');
INSERT INTO Transaction VALUES(45,321321,4895,'8-MAR-2018','d','SB1');
INSERT INTO Transaction VALUES(46,173642,550.95,'8-MAR-2018','d','ATM02');
INSERT INTO Transaction VALUES(47,173642,87938,'8-MAR-2018','d','Online');
INSERT INTO Transaction VALUES(48,183747,9652.55,'9-MAR-2018','d','Central');
INSERT INTO Transaction VALUES(49,426573,500,'11-MAR-2018','d','SB4');
INSERT INTO Transaction VALUES(50,777723,1990,'11-MAR-2018','d','Online');
INSERT INTO Transaction VALUES(51,265843,135,'11-MAR-2018','d','Online');
INSERT INTO Transaction VALUES(52,999924,1600,'11-MAR-2018','d','SB3');
INSERT INTO Transaction VALUES(53,525342,2500,'11-MAR-2018','d','SB2');
INSERT INTO Transaction VALUES(54,525342,5000,'11-MAR-2018','d','SB2');
INSERT INTO Transaction VALUES(55,273646,110,'11-MAR-2018','d','SB2');
INSERT INTO Transaction VALUES(56,777777,351,'11-MAR-2018','d','Central');
INSERT INTO Transaction VALUES(57,152434,156.48,'11-MAR-2018','d','SB1');
INSERT INTO Transaction VALUES(58,172643,1790,'11-MAR-2018','d','SB1');
INSERT INTO Transaction VALUES(59,283746,350,'11-MAR-2018','d','SB3');
INSERT INTO Transaction VALUES(60,269734,834,'16-MAR-2018','d','SB4');
INSERT INTO Transaction VALUES(61,282736,500,'16-MAR-2018','w','ATM01');
INSERT INTO Transaction VALUES(62,363636,572,'17-MAR-2018','d','SB5');
INSERT INTO Transaction VALUES(63,354345,1000,'19-MAR-2018','d','SB4');
INSERT INTO Transaction VALUES(64,888776,2540,'22-MAR-2018','d','ATM01');
INSERT INTO Transaction VALUES(65,337567,1350,'23-MAR-2018','d','SB3');
INSERT INTO Transaction VALUES(66,654655,3345,'23-MAR-2018','d','Central');
INSERT INTO Transaction VALUES(67,173264,1000,'25-MAR-2018','w','SB2');
INSERT INTO Transaction VALUES(68,274364,4232,'25-MAR-2018','d','SB3');
INSERT INTO Transaction VALUES(69,273654,8965,'29-MAR-2018','d','SB3');
INSERT INTO Transaction VALUES(70,362743,1000,'5-APR-2018','d','Central');
INSERT INTO Transaction VALUES(71,818726,2500,'10-APR-2018','d','ATM01');
INSERT INTO Transaction VALUES(72,333333,9864,'12-APR-2018','d','Central');
INSERT INTO Transaction VALUES(73,183274,159.50,'17-APR-2018','d','ATM02');
INSERT INTO Transaction VALUES(74,928745,1300,'18-APR-2018','w','Central');
INSERT INTO Transaction VALUES(75,818726,1300,'19-APR-2018','w','SB3');
INSERT INTO Transaction VALUES(76,587253,2000,'24-APR-2018','d','ATM01');
INSERT INTO Transaction VALUES(77,818726,1200,'1-MAY-2018','w','SB2');
INSERT INTO Transaction VALUES(78,283274,516,'3-AUG-2018','d','Central');
INSERT INTO Transaction VALUES(79,383838,2500,'3-JUL-2018','d','SB3');
INSERT INTO Transaction VALUES(80,654654,549,'10-SEP-2018','d','Central');
INSERT INTO Transaction VALUES(81,889988,28015,'11-JUN-2018','d','SB5');
INSERT INTO Transaction VALUES(82,663355,2917.95,'12-SEP-2018','d','SB1');
INSERT INTO Transaction VALUES(83,776655,250,'12-AUG-2018','d','SB2');
INSERT INTO Transaction VALUES(84,827436,986,'15-JUL-2018','d','ATM02');
INSERT INTO Transaction VALUES(85,173264,1000,'15-MAY-2018','w','ATM01');
INSERT INTO Transaction VALUES(86,282828,1000,'15-SEP-2018','d','SB3');
INSERT INTO Transaction VALUES(87,119928,500,'15-MAY-2018','d','Central');
INSERT INTO Transaction VALUES(88,293847,950,'15-AUG-2018','d','SB2');
INSERT INTO Transaction VALUES(89,776665,78974,'23-AUG-2018','d','Online');
INSERT INTO Transaction VALUES(90,867543,100,'25-JUL-2018','d','ATM01');
