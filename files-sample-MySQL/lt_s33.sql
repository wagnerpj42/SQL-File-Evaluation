-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 33

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
SELECT C.CustID, C.FName, C.LName, A.AccClosedDate
FROM Customer C
JOIN Account A ON (A.Customer = C.CustID)
WHERE a.accopenlocation = 'Central' AND a.acccloseddate >= '2017-03-01';

-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
SELECT AccopenLocation, AccStatus, COUNT(AccStatus)
FROM Account
GROUP BY accopenlocation, accstatus
Order By accopenlocation, accstatus;

-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
SELECT AccNumber, AccBalance
FROM Account A
WHERE AccBalance > (
    SELECT AVG(A1.AccBalance)
    FROM Account A1
    WHERE A.ACCTYPE = A1.AccType)
ORDER BY AccNumber;

-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
SELECT T.TRANSLOCATION, AVG(T.TRANSAMOUNT)
FROM TRANSACTION T
GROUP BY T.TRANSLOCATION, T.TRANSAMOUNT
HAVING AVG(T.TRANSAMOUNT) < (
    SELECT AVG(T1.TRANSAMOUNT)
    FROM TRANSACTION T1)
ORDER BY T.TRANSAMOUNT DESC;

-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.

SELECT C.CUSTID, C.FNAME, C.LNAME, MAX(T.TransAmount)
FROM Customer C
JOIN Account A ON (C.CustID = A.Customer)
JOIN Transaction T ON (A.AccNumber = T.AccNumber)
WHERE T.TransType = 'w' AND A.AccType = 'savings'
GROUP BY C.CUSTID, C.FNAME, C.LNAME
ORDER BY C.CUSTID;

-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
    
SELECT DISTINCT C.CUSTID, C.FNAME, C.LNAME
FROM Account A
RIGHT OUTER JOIN Customer C
ON A.customer = C.CustID
WHERE A.ACCSTATUS = 'Closed' OR A.ACCSTATUS = 'Frozen' OR A.ACCSTATUS IS NULL;

-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location

SELECT TRANSLOCATION, COUNT(A.ACCOPENLOCATION)
FROM ACCOUNT A
LEFT OUTER JOIN CUSTOMER C ON (C.CUSTID = A.CUSTOMER)
LEFT OUTER JOIN TRANSACTION T ON (A.ACCNUMBER = T.ACCNUMBER)
GROUP BY TRANSLOCATION;
