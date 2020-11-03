-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 36

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
SELECT CustID, FName, LName, AccClosedDate
FROM Customer C
JOIN Account A ON (C.CustID = A.Customer)
WHERE LOWER(AccStatus) = 'closed' AND AccClosedDate >= '2017-03-01' AND LOWER(AccOpenLocation) = 'central';

-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
SELECT AccOpenLocation, AccStatus, COUNT(AccStatus)
FROM Account
WHERE LOWER(AccStatus) = 'closed' OR LOWER(AccStatus) = 'active' OR LOWER(AccStatus) = 'frozen'
GROUP BY AccOpenLocation, AccStatus
ORDER BY AccOpenLocation, AccStatus;

-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
SELECT AccNumber
FROM Account A1
WHERE (A1.AccBalance > 
    (SELECT AVG(AccBalance)
     FROM Account A2
     WHERE LOWER(A2.AccType) = 'checking' AND LOWER(A1.AccType) = 'checking'))
      OR (A1.AccBalance >
    (SELECT AVG(AccBalance)
     FROM Account A2
     WHERE LOWER(A2.AccType) = 'savings' AND LOWER(A1.AccType) = 'savings'))
ORDER BY AccNumber;

-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
SELECT TransLocation, AVG(TransAmount)
FROM Transaction T1
WHERE (T1.TransAmount < 
    (SELECT AVG(TransAmount)
     FROM Transaction T2
     WHERE T1.AccNumber = T2.AccNumber))
GROUP BY TransLocation
ORDER BY AVG(TransAmount) DESC;

-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
SELECT CustID, FName, LName, MAX(TransAmount)
FROM Customer C
JOIN Account A ON (C.CustID = A.Customer)
JOIN Transaction T ON (A.AccNumber = T.AccNumber)
WHERE LOWER(TransType) = 'w' AND LOWER(AccType) = 'savings'
GROUP BY CustID, FName, LName
ORDER BY CustID;

-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
SELECT CustID, FName, LName, AccStatus
FROM Customer C
LEFT OUTER JOIN Account A ON (C.CustID = A.Customer)
WHERE LOWER(AccStatus) IN ('closed', 'frozen') OR AccNumber IS NULL;

-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location
SELECT T.TransLocation, COUNT(A.AccOpenLocation)
FROM Transaction T
LEFT OUTER JOIN Account A ON (T.TransLocation = A.AccOpenLocation)
GROUP BY T.TransLocation;





