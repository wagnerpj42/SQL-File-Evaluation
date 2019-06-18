--- CS 260, Spring 2019, Lab Test
--- Name: Bryant Callaghan

--- Table List:
--- Customer ( _CustID_ , FName, LName, PIN)
--- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
---                         AccOpenDate, AccClosedDate, AccStatus)
--- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
---                         TransType, TransLocation)


--- 1. (15 points) List the customer id, both name fields, and the account closed 
---    date for customers whose accounts opened in the Central location but have 
---    been closed on or after January 1st, 2018.

SELECT c.CustID, c.FName, c.LName, a.AccClosedDate
FROM Customer c
JOIN Account a ON (c.CustID=a.Customer)
WHERE a.AccOpenLocation='Central' AND a.AccClosedDate>='01-JAN-2018';

--- 2. (16 points) For each account opening location, show how many active, closed,
---    and frozen, and accounts there are.  Display the results with the locations
---    and status values in alphabetical order.
---        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
---        Central        Active        10
---        Central        Closed        20
---        Central        Frozen         1
---        <similar for other branches>

SELECT acc.AccOpenLocation, acc.AccStatus, COUNT(acc.AccNumber)
FROM Account acc
GROUP BY acc.AccOpenLocation, acc.AccStatus
ORDER BY acc.AccOpenLocation, acc.AccStatus;

--- 3. (18 points) List the accounts (by account number, in numeric order) 
---    with an account balance greater than the average account balance 
---    for all accounts of the same account type (checking or savings).

SELECT ac.AccNumber
FROM Account ac
WHERE ac.AccBalance > 
  (SELECT AVG(acc.ACCBALANCE)
  FROM Account acc
  WHERE ac.AccType = acc.AccType
  GROUP BY acc.AccType)
ORDER BY ac.AccNumber;

--- 4. (18 points) Display the transaction location and the average transaction 
---    amount for each transaction location where that average transaction amount
---    is less than the average transaction amount for all transactions.  Order the
---    results by average transaction amount, largest to smallest.
--- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
---    number to display with a limited number of decimal places; 
---    e.g. CAST (AVG(value) AS NUMBER(*,2)) 

SELECT t.TransLocation, AVG(t.TransAmount)
FROM Transaction t
WHERE t.TransAmount < 
  (SELECT AVG(tr.TransAmount)
  FROM Transaction tr)
GROUP BY t.TransLocation
ORDER BY AVG(t.TransAmount);

--- 5. (16 points) Find each customer id, first name, and last name, plus the 
---    amount for the largest 'w' (withdrawal) transaction related to a 
---    savings account which that customer has opened.  Display the results 
---    in order by customer id.

SELECT c.CustID, c.FName, c.LName, MAX(t.TransAmount)
FROM Customer c
JOIN Account ac ON(c.CustID=ac.Customer)
JOIN Transaction t ON (ac.AccNumber=t.AccNumber)
WHERE t.TransType='w' AND ac.AccType='savings'
GROUP BY c.CustID, c.FName, c.LName;

--- 6. (17 points) List the customer id, first name and last name of any 
---    customers who have no active accounts.  This includes A) customers who 
---    have accounts, but they only have accounts that are closed and/or frozen, 
---    and also B) customers who do not have any accounts at all.

SELECT c.CustID, c.FName, c.LName
FROM Customer c
WHERE c.CustID NOT IN
  (SELECT DISTINCT cc.CustID
  FROM Customer cc
  JOIN Account ac ON(cc.CustID=ac.Customer)
  WHERE ac.AccStatus='Active');

--- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
---    display the number of accounts opened at that location, even if no 
---    accounts were opened at that location.
--- NOTE: at least one transaction location has no accounts opened at that location

SELECT t.TransLocation, COUNT(DISTINCT ac.AccNumber)
FROM Account ac
FULL OUTER JOIN Transaction t ON (t.AccNumber=ac.AccNumber)
GROUP BY t.TransLocation;