-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 54

-- -- Parsing Issue: no blank line between question header (as instructor comment) and submitted query
-- -- Current Handling: parses appropriately
-- -- Desired Handling: should parse appropriately

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
WHERE A.AccOpenLocation = 'Central' AND A.AccClosedDate >= '2017-03-01';

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
Group By AccOpenLocation,AccStatus
Order By AccOpenLocation, AccStatus;

-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).

SELECT AccNumber
FROM Account A1
WHERE AccBalance > 
    (SELECT AVG(AccBalance) 
     FROM Account A2 
     WHERE A1.AccType = A2.AccType 
     GROUP BY AccType)
ORDER BY A1.AccNumber;

-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.

SELECT TransLocation, AVG(T1.TransAmount) AS AvgTrans
FROM Transaction T1
GROUP BY (T1.TransLocation)
HAVING AVG(T1.TransAmount) < (SELECT AVG(T2.TransAmount) 
                              FROM Transaction T2)
ORDER BY AvgTrans;

-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.

SELECT CustId, FName, LName, MAX(T1.TransAmount) AS LargestWithdrawal
FROM Customer C
Join Account A1 ON (C.CustID = A1.Customer)
Join TransAction T1 on (T1.AccNumber = A1.AccNumber)
WHERE T1.TransType = 'w' AND A1.AccType = 'savings'
GROUP BY CustId, FName, LName, T1.AccNumber
ORDER BY CustID;

-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.

SELECT CustID, FName, LName
FROM Customer
WHERE CustID NOT IN(
    SELECT CustID
    FROM Customer C2
    JOIN Account A2 ON (C2.CustID = A2.Customer)
    WHERE AccStatus = 'Active');

-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location


SELECT TransLocation, COUNT(AccOpenLocation)
FROM Account A
RIGHT OUTER JOIN Transaction T ON (A.AccOpenLocation = T.TransLocation)
GROUP BY T.TransLocation, A.AccOpenLocation
ORDER BY T.TransLocation;


