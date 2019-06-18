--- CS 260, Spring 2019, Lab Test
--- Name: Ben Pardon

--- Table List:
--- Customer ( _CustID_ , FName, LName, PIN)
--- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
---                         AccOpenDate, AccClosedDate, AccStatus)
--- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
---                         TransType, TransLocation)


--- 1. (15 points) List the customer id, both name fields, and the account closed 
---    date for customers whose accounts opened in the Central location but have 
---    been closed on or after January 1st, 2018.
SELECT C.CUSTID, FNAME, LNAME, ACCCLOSEDDATE
FROM Customer C
JOIN Account A
ON C.CUSTID = A.CUSTOMER
WHERE (AccOpenLocation LIKE 'Central' AND AccClosedDate >= '01-JAN-2018');



--- 2. (16 points) For each account opening location, show how many active, closed,
---    and frozen, and accounts there are.  Display the results with the locations
---    and status values in alphabetical order.
---        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
---        Central        Active        10
---        Central        Closed        20
---        Central        Frozen         1
---        <similar for other branches>
SELECT AccOpenLocation, AccStatus, COUNT(*)
FROM Account A
GROUP BY (AccOpenLocation, AccStatus)
ORDER BY AccOpenLocation ASC, AccStatus ASC;


--- 3. (18 points) List the accounts (by account number, in numeric order) 
---    with an account balance greater than the average account balance 
---    for all accounts of the same account type (checking or savings).
SELECT AccNumber
FROM Account A
WHERE AccBalance >
  (SELECT AVG(AccBalance)
  FROM Account
  GROUP BY (AccType)
  Having (A.AccType = AccType))
ORDER BY AccNumber ASC;


--- 4. (18 points) Display the transaction location and the average transaction 
---    amount for each transaction location where that average transaction amount
---    is less than the average transaction amount for all transactions.  Order the
---    results by average transaction amount, largest to smallest.
--- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
---    number to display with a limited number of decimal places; 
---    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
SELECT TransLocation, CAST(AVG(TransAmount) AS NUMBER(*,2))
FROM Transaction
GROUP BY TransLocation
HAVING(AVG(TransAmount)< 
  (SELECT AVG(TransAmount)
  FROM Transaction))
ORDER BY AVG(TransAmount) DESC;


--- 5. (16 points) Find each customer id, first name, and last name, plus the 
---    amount for the largest 'w' (withdrawal) transaction related to a 
---    savings account which that customer has opened.  Display the results 
---    in order by customer id.
SELECT C.CustId, FName, LName, MAX(TransAmount)
FROM Transaction T
JOIN Account A
ON(T.AccNumber = A.AccNumber)
JOIN Customer C
ON(C.CustId = A.Customer)
WHERE(AccType LIKE 'savings')
GROUP BY(transType, C.custId, FName, LName)
HAVING(TransType LIKE 'w')
ORDER BY C.CUSTID;



--- 6. (17 points) List the customer id, first name and last name of any 
---    customers who have no active accounts.  This includes A) customers who 
---    have accounts, but they only have accounts that are closed and/or frozen, 
---    and also B) customers who do not have any accounts at all.
SELECT C.CustId, FName, LName
FROM Customer C
LEFT OUTER JOIN Account A
ON(C.CustId = A.Customer)
MINUS
SELECT C.CustId, FName, LName
FROM Customer C
JOIN Account A
ON(C.CustId = A.CUSTOMER)
WHERE (AccStatus LIKE 'Active');


--- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
---    display the number of accounts opened at that location, even if no 
---    accounts were opened at that location.
--- NOTE: at least one transaction location has no accounts opened at that location
SELECT TransLocation, COUNT(AccOpenLocation)
FROM Transaction T
LEFT OUTER JOIN Account A
ON(T.AccNumber = A.AccNumber)
GROUP BY(TransLocation, AccOpenLocation)
HAVING (T.TransLocation = A.AccOpenLocation);


