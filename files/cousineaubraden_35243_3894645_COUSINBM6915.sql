--- CS 260, Spring 2019, Lab Test
--- Name: Braden Cousineau

--- Table List:
--- Customer ( _CustID_ , FName, LName, PIN)
--- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
---                         AccOpenDate, AccClosedDate, AccStatus)
--- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
---                         TransType, TransLocation)


--- 1. (15 points) List the customer id, both name fields, and the account closed 
---    date for customers whose accounts opened in the Central location but have 
---    been closed on or after January 1st, 2018.

SELECT CustID, FName, LName, AccClosedDate
FROM CUSTOMER
JOIN ACCOUNT ON CUSTOMER.CustID = ACCOUNT.Customer
WHERE ACCOUNT.AccOpenLocation = 'Central'
AND TO_CHAR(ACCOUNT.AccClosedDate, 'YYYYMMDD') >= '20180101';

--- 2. (16 points) For each account opening location, show how many active, closed,
---    and frozen, and accounts there are.  Display the results with the locations
---    and status values in alphabetical order.
---        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
---        Central        Active        10
---        Central        Closed        20
---        Central        Frozen         1
---        <similar for other branches>

SELECT AccOpenLocation, AccStatus, COUNT(AccStatus)
FROM ACCOUNT
GROUP BY ACCOUNT.AccOpenLocation, ACCOUNT.AccStatus
ORDER BY ACCOUNT.AccOpenLocation, ACCOUNT.AccStatus;

--- 3. (18 points) List the accounts (by account number, in numeric order) 
---    with an account balance greater than the average account balance 
---    for all accounts of the same account type (checking or savings).

SELECT AccNumber, AccBalance, AccType
FROM ACCOUNT A1
WHERE AccBalance > 
(SELECT AVG(AccBalance)
FROM ACCOUNT A2
WHERE A1.AccType = A2.AccType);

--SELECT AccType, AVG(AccBalance)
--FROM ACCOUNT
--GROUP BY ACCOUNT.AccType;

--- 4. (18 points) Display the transaction location and the average transaction 
---    amount for each transaction location where that average transaction amount
---    is less than the average transaction amount for all transactions.  Order the
---    results by average transaction amount, largest to smallest.
--- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
---    number to display with a limited number of decimal places; 
---    e.g. CAST (AVG(value) AS NUMBER(*,2)) 

SELECT TransLocation, AVG(TransAmount)
FROM TRANSACTION T1
WHERE AVG(TransAmount) < 
(SELECT AVG(TransAmount) 
FROM TRANSACTION T2)
GROUP BY T1.TransLocation, TransLocation
ORDER BY AVG(T1.TransAmount);



--- 5. (16 points) Find each customer id, first name, and last name, plus the 
---    amount for the largest 'w' (withdrawal) transaction related to a 
---    savings account which that customer has opened.  Display the results 
---    in order by customer id.

SELECT CustID, FName, LName, MAX(TransAmount)
FROM CUSTOMER 
JOIN ACCOUNT ON CUSTOMER.CustID = ACCOUNT.Customer
JOIN TRANSACTION ON ACCOUNT.AccNumber = TRANSACTION.AccNumber
WHERE ACCOUNT.AccType = 'savings' 
AND TRANSACTION.TransType = 'w' 
GROUP BY CustID, FName, LName 
ORDER BY CUSTOMER.CustID;

--- 6. (17 points) List the customer id, first name and last name of any 
---    customers who have no active accounts.  This includes A) customers who 
---    have accounts, but they only have accounts that are closed and/or frozen, 
---    and also B) customers who do not have any accounts at all.

SELECT CustID, FName, LName
FROM CUSTOMER
JOIN ACCOUNT ON CUSTOMER.CustID = ACCOUNT.Customer
WHERE ACCOUNT.AccStatus != 'Active' 
OR ACCOUNT.AccNumber IS NULL;

--- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
---    display the number of accounts opened at that location, even if no 
---    accounts were opened at that location.
--- NOTE: at least one transaction location has no accounts opened at that location

SELECT TransLocation, COUNT(AccOpenDate)
FROM TRANSACTION
JOIN ACCOUNT ON ACCOUNT.AccNumber = TRANSACTION.AccNumber 
GROUP BY TRANSACTION.TransLocation
ORDER BY TRANSACTION.TransLocation;



