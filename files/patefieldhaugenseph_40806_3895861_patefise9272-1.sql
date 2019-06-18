--- CS 260, Spring 2019, Lab Test
--- Name: Steven Patefield Haugen (Seph)

--- Table List:
--- Customer ( _CustID_ , FName, LName, PIN)
--- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
---                         AccOpenDate, AccClosedDate, AccStatus)
--- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
---                         TransType, TransLocation)


--- 1. (15 points) List the customer id, both name fields, and the account closed 
---    date for customers whose accounts opened in the Central location but have 
---    been closed on or after January 1st, 2018.
SELECT c.custId, c.fName, c.lName, a.accClosedDate
FROM Customer c
JOIN Account a
ON a.customer = c.custId
WHERE a.accOpenLocation = 'Central' AND a.accClosedDate >= '01-JAN-18';

--- 2. (16 points) For each account opening location, show how many active, closed,
---    and frozen, and accounts there are.  Display the results with the locations
---    and status values in alphabetical order.
---        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
---        Central        Active        10
---        Central        Closed        20
---        Central        Frozen         1
---        <similar for other branches>
SELECT a.accOpenLocation, a.accStatus, COUNT(a.accStatus)
FROM Account a
GROUP BY a.accOpenLocation, a.accStatus
ORDER BY a.accOpenLocation, a.accStatus;

--- 3. (18 points) List the accounts (by account number, in numeric order) 
---    with an account balance greater than the average account balance 
---    for all accounts of the same account type (checking or savings).
SELECT a1.accNumber
FROM Account a1
WHERE a1.accBalance >
  (SELECT AVG(a2.accBalance)
  FROM Account a2
  WHERE a1.accType = a2.accType
  )
ORDER BY a1.accNumber;

--- 4. (18 points) Display the transaction location and the average transaction 
---    amount for each transaction location where that average transaction amount
---    is less than the average transaction amount for all transactions.  Order the
---    results by average transaction amount, largest to smallest.
--- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
---    number to display with a limited number of decimal places; 
---    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
SELECT t1.transLocation, CAST(AVG(t1.transAmount) AS NUMBER(*,2))
FROM Transaction t1
HAVING AVG(t1.transAmount) <
  (SELECT AVG(t2.transAmount)
  FROM Transaction t2
  )
GROUP BY t1.transLocation 
ORDER BY AVG(t1.transAmount);

--- 5. (16 points) Find each customer id, first name, and last name, plus the 
---    amount for the largest 'w' (withdrawal) transaction related to a 
---    savings account which that customer has opened.  Display the results 
---    in order by customer id.
SELECT DISTINCT c.custId, c.fName, c.lName, t1.transAmount
FROM Customer c
JOIN Account a
ON a.customer = c.custId
JOIN Transaction t1
ON t1.accNumber = a.accNumber
WHERE a.accType = 'savings' AND t1.transType = 'w' AND t1.transAmount =
  (SELECT MAX(t2.transAmount)
  FROM Transaction t2
  WHERE t2.transType = 'w' AND t2.transId = t1.transId
  )
ORDER BY c.custId;

--- 6. (17 points) List the customer id, first name and last name of any 
---    customers who have no active accounts.  This includes A) customers who 
---    have accounts, but they only have accounts that are closed and/or frozen, 
---    and also B) customers who do not have any accounts at all.
SELECT c.custId, c.fName, c.lName
FROM Customer c
WHERE NOT EXISTS
  (SELECT DISTINCT c2.custId
  FROM Customer c2
  JOIN Account a
  ON a.customer = c2.custId
  WHERE a.accStatus = 'Active'
  );

--- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
---    display the number of accounts opened at that location, even if no 
---    accounts were opened at that location.
--- NOTE: at least one transaction location has no accounts opened at that location


