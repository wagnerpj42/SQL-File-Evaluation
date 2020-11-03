-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 17

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
SELECT CustID, FName, LName, A.AccClosedDate
FROM Customer C
JOIN Account A
ON (C.CustID = A.Customer)
WHERE AccOpenLocation = 'Central' AND A.AccClosedDate >= '2017-03-01';


-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
SELECT AccOpenLocation, AccStatus, COUNT(A.AccStatus)
FROM Account A
WHERE AccStatus = 'Closed' 
    OR AccStatus = 'Active' 
    OR AccStatus = 'Frozen'
GROUP BY AccOpenLocation, AccStatus
ORDER BY AccStatus;



-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
SELECT AccNumber
FROM Account A1
WHERE AccBalance >
    (SELECT AVG(A2.AccBalance)
     FROM Account A2
     WHERE A1.AccType = A2.AccType)
ORDER BY AccNumber;


-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
SELECT TransLocation, AVG(TransAmount)
FROM Transaction T1
WHERE (T1.TransAmount) <
    (SELECT AVG(T2.TransAmount)
     FROM Transaction T2)
GROUP BY TransLocation
ORDER BY AVG(TransAmount) DESC;


-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
SELECT CustID, FName, LName, MAX(T.TransAmount)
FROM Customer C
JOIN Account A
ON (a.Customer = C.CustID)
JOIN Transaction T
ON (T.AccNumber = A.AccNumber)
WHERE CustID IN
    (SELECT A.Customer
     FROM Account A
     WHERE A.AccType = 'savings'
        AND TransAmount IN
            (SELECT T.TransAmount
             FROM Transaction T
             WHERE TransType = 'w')
     )
GROUP BY CustID, FName, LName
ORDER BY CustID;



-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
SELECT CustID, FName, LName
FROM Customer C
WHERE C.CustID NOT IN (
SELECT CustID
FROM Customer C
JOIN Account A
ON (C.custID = A.Customer)
WHERE AccStatus = 'Active');



-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location
SELECT TransLocation, COUNT(AccNumber)
FROM Trasaction T
JOIN Account A
ON (T.AccNumber = A.AccNumber)
GROUP BY TransLocation
HAVING AccNumber IN
    (SELECT COUNT(AccNumber)
     FROM Account A
     WHERE A.AccNumber >=0);


