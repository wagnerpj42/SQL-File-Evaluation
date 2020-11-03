-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 23

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
JOIN Account A
ON C.CustID = A.Customer
WHERE A.AccOpenLocation = 'Central'
AND A.AccClosedDate >= '2017-03-01';

-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
SELECT DISTINCT A.AccOpenLocation, S.AccStatus, COUNT(A.AccStatus)
FROM Account A
JOIN Account S
ON A.AccNumber = S.AccNumber
GROUP BY A.AccOpenLocation, S.AccStatus
ORDER BY A.AccOpenLocation, S.AccStatus ASC;

-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
SELECT A.AccNumber
FROM Account A
WHERE A.AccBalance > (SELECT AVG(B.AccBalance)
                      FROM Account B
                      WHERE B.AccType = A.AccType)
ORDER BY A.AccNumber ASC;

-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
SELECT T.TransLocation, AVG(T.TransAmount)
FROM Transaction T
GROUP BY T.TransLocation
HAVING AVG(T.TransAmount) < (SELECT AVG(A.TransAmount)
                            FROM Transaction A)
ORDER BY AVG(T.TransAmount) DESC;


-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
SELECT C.CustID, C.FName, C.LName, MAX(T.TransAmount)
FROM Customer C
JOIN Account A
ON C.CustID = A.Customer
JOIN Transaction T
ON A.AccNumber = T.AccNumber
WHERE A.AccType = 'savings'
GROUP BY C.CustID, C.FName, C.LName
ORDER BY C.CustID ASC;


-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
SELECT DISTINCT C.CustID, C.FName, C.LName
FROM Customer C
LEFT JOIN Account A
ON C.CustID = A.Customer
WHERE A.AccNumber IS NULL
OR A.AccClosedDate IS NOT NULL
AND A.AccStatus != 'Active'
ORDER BY C.CustID;


-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location
SELECT T.TransLocation, COUNT(A.AccOpenLocation)
FROM Transaction T
LEFT OUTER JOIN Account A
ON T.AccNumber = A.AccNumber
GROUP BY T.TransLocation;

