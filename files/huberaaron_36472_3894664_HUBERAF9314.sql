--- CS 260, Spring 2019, Lab Test
--- Name: Aaron Huber

--- Table List:
--- Customer ( _CustID_ , FName, LName, PIN)
--- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
---                         AccOpenDate, AccClosedDate, AccStatus)
--- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
---                         TransType, TransLocation)


--- 1. (15 points) List the customer id, both name fields, and the account closed 
---    date for customers whose accounts opened in the Central location but have 
---    been closed on or after January 1st, 2018.

SELECT C.custId, C.fName, C.lName, A.AccClosedDate
FROM Customer C
JOIN Account A
ON C.custId = A.customer
WHERE A.AccClosedDate >= '01-JAN-18';


--- 2. (16 points) For each account opening location, show how many active, closed,
---    and frozen, and accounts there are.  Display the results with the locations
---    and status values in alphabetical order.
---        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
---        Central        Active        10
---        Central        Closed        20
---        Central        Frozen         1
---        <similar for other branches>

SELECT A.accOpenLocation, A.accStatus, COUNT(*)
FROM Account A
GROUP BY A.accOpenLocation, A.accStatus
ORDER BY A.accOpenLocation, A.accStatus;


--- 3. (18 points) List the accounts (by account number, in numeric order) 
---    with an account balance greater than the average account balance 
---    for all accounts of the same account type (checking or savings).

SELECT A.accNumber
FROM Account A
WHERE EXISTS
  (SELECT AVG(A2.accBalance)
  FROM Account A2
  GROUP BY A2.accType
  HAVING AVG(A2.accBalance) < A.accBalance)
ORDER BY A.accNumber;                   


--- 4. (18 points) Display the transaction location and the average transaction 
---    amount for each transaction location where that average transaction amount
---    is less than the average transaction amount for all transactions.  Order the
---    results by average transaction amount, largest to smallest.
--- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
---    number to display with a limited number of decimal places; 
---    e.g. CAST (AVG(value) AS NUMBER(*,2)) 

SELECT T.transLocation, AVG(T.transAmount)
FROM Transaction T
GROUP BY T.transLocation
HAVING AVG(T.transAmount) < 
  (SELECT AVG(T2.transAmount)
  FROM Transaction T2)
ORDER BY AVG(T.transAmount) DESC;


--- 5. (16 points) Find each customer id, first name, and last name, plus the 
---    amount for the largest 'w' (withdrawal) transaction related to a 
---    savings account which that customer has opened.  Display the results 
---    in order by customer id.

SELECT C.custId, C.fName, C.lName, MAX(T.transAmount)
FROM Customer C
JOIN Account A
ON C.custId = A.customer
JOIN Transaction T
ON A.accNumber = T.accNumber
WHERE EXISTS
  (SELECT A2.customer, MAX(T2.transAmount)
  FROM Account A2
  JOIN Transaction T2
  ON A2.accNumber = T2.accNumber
  WHERE T2.transType = 'w'
  GROUP BY A2.customer)
GROUP BY C.custId, C.fName, C.lName
ORDER BY C.custId;


--- 6. (17 points) List the customer id, first name and last name of any 
---    customers who have no active accounts.  This includes A) customers who 
---    have accounts, but they only have accounts that are closed and/or frozen, 
---    and also B) customers who do not have any accounts at all.

SELECT C.custId, C.fName, C.lName
FROM Customer C
WHERE NOT EXISTS
  (SELECT A.customer, A.accStatus, COUNT(*)
  FROM Account A
  WHERE A.customer = C.custId
  AND A.accStatus = 'Active'
  GROUP BY A.customer, A.accStatus
  HAVING COUNT(*) >= 1);


--- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
---    display the number of accounts opened at that location, even if no 
---    accounts were opened at that location.
--- NOTE: at least one transaction location has no accounts opened at that location

SELECT A.accOpenLocation, COUNT(A.accNumber)
FROM Account A
GROUP BY A.accOpenLocation
  UNION
SELECT T.transLocation, 0
FROM Transaction T
WHERE T.transLocation NOT IN
  (SELECT A2.accOpenLocation
  FROM Account A2);

