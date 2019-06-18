--- CS 260, Spring 2019, Lab Test
--- Name: Alyssa Phillips

--- Table List:
--- Customer ( _CustID_ , FName, LName, PIN)
--- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
---                         AccOpenDate, AccClosedDate, AccStatus)
--- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
---                         TransType, TransLocation)


--- 1. (15 points) List the customer id, both name fields, and the account closed 
---    date for customers whose accounts opened in the Central location but have 
---    been closed on or after January 1st, 2018.
SELECT C.CustID as CustomerID, C.FName AS FirstName, C.LName AS LastName
FROM Customer C
JOIN Account A
ON C.CustId = A.Customer
WHERE LOWER(A.ACCOPENLOCATION) LIKE '%central%' AND A.ACCCLOSEDDATE >= '01-Jan-2018';


--- 2. (16 points) For each account opening location, show how many active, closed,
---    and frozen, and accounts there are.  Display the results with the locations
---    and status values in alphabetical order.
---        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
---        Central        Active        10
---        Central        Closed        20
---        Central        Frozen         1
---        <similar for other branches>
SELECT A.AccStatus AS Status, A.AccOpenLocation AS Location, Count(Distinct A.AccNumber) AS NumberOfAccounts
FROM Account A
GROUP BY A.AccStatus, A.AccOpenLocation
ORDER BY A.AccOpenLocation, A.AccStatus;



--- 3. (18 points) List the accounts (by account number, in numeric order) 
---    with an account balance greater than the average account balance 
---    for all accounts of the same account type (checking or savings).
SELECT A.AccNumber AS AccountNumber
FROM Account A
WHERE A.AccBalance>
  (SELECT AVG(A1.AccBalance)
  FROM Account A1
  WHERE A.AccType = A1.AccType)
  ORDER BY A.AccNumber;

--- 4. (18 points) Display the transaction location and the average transaction 
---    amount for each transaction location where that average transaction amount
---    is less than the average transaction amount for all transactions.  Order the
---    results by average transaction amount, largest to smallest.
--- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
---    number to display with a limited number of decimal places; 
---    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
SELECT T.TransLocation, CAST (AVG(T.TransAmount) AS NUMBER(*,2)) AS AverageAmount
FROM Transaction T
GROUP BY T.TransLocation
HAVING AVG(T.TransAmount)<
  (SELECT AVG(T1.TransAmount)
  FROM Transaction T1);


--- 5. (16 points) Find each customer id, first name, and last name, plus the 
---    amount for the largest 'w' (withdrawal) transaction related to a 
---    savings account which that customer has opened.  Display the results 
---    in order by customer id.
SELECT C.CustID, C.FName, C.LName, MAX(T.TransAmount)
FROM Customer C
JOIN Account A
ON C.CustID = A.Customer
JOIN Transaction T
ON A.AccNumber = T.AccNumber
WHERE A.AccType LIKE '%savings%' AND T.TransType LIKE '%w%'
GROUP BY C.CustID, C.FName, C.LName;


--- 6. (17 points) List the customer id, first name and last name of any 
---    customers who have no active accounts.  This includes A) customers who 
---    have accounts, but they only have accounts that are closed and/or frozen, 
---    and also B) customers who do not have any accounts at all.
SELECT C.CustID, C.FName, C.LName
FROM Customer C
MINUS 
SELECT C.CustID, C.FName, C.LName
FROM Customer C
JOIN Account A
ON C.CustID = A.Customer
WHERE A.AccStatus LIKE '%Active%';


--- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
---    display the number of accounts opened at that location, even if no 
---    accounts were opened at that location.
--- NOTE: at least one transaction location has no accounts opened at that location
SELECT T.TransLocation AS Location, COUNT(Distinct A.AccNumber) AS NumberOfAccounts
FROM Transaction T
FULL OUTER JOIN Account A
ON T.AccNumber = A.AccNumber
GROUP BY T.TransLocation
ORDER BY T.TransLocation;

