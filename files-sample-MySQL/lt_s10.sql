-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 10

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.

SELECT CustID, FName, LName, AccClosedDate as "AccClosedDate"
FROM Customer
JOIN Account
ON(CustID = Customer)
WHERE (AccOpenLocation LIKE 'Central' AND AccClosedDate >= '2017-03-01')
ORDER BY AccClosedDate DESC;


-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>

SELECT DISTINCT AccOpenLocation, AccStatus
FROM Account
ORDER BY AccOpenLocation ASC, AccStatus ASC;


-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).

SELECT A1.AccNumber, A1.AccBalance, A1.AccType
FROM Account A1
WHERE (AccBalance >
    (SELECT AVG(A2.AccBalance) FROM Account A2
    WHERE (A1.AccType = A2.AccType)
    GROUP BY A1.AccNumber))
    ORDER BY A1.AccNumber ASC;


-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.

SELECT T1.TransLocation, T1.TransAmount
FROM Transaction T1
WHERE (T1.TransAmount > 
    (SELECT AVG(TransAmount) FROM Transaction T2
    WHERE (T1.TransLocation = T2.TransLocation)
    GROUP BY T2.TransLocation))
    ORDER BY TransAmount DESC;


-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.

SELECT DISTINCT C.CustID, C.FName, C.LName, T.TransAmount
FROM Transaction T
JOIN Account A
ON (T.AccNumber = A.AccNumber AND T.TransType LIKE 'w')
JOIN Customer C
ON (A.Customer = C.CustID AND A.AccType LIKE 'savings');


-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.

SELECT DISTINCT C.CustID, C.FName, C.LName
FROM Customer C
JOIN Account A
ON (CustID = Customer AND (AccStatus LIKE 'Frozen' OR AccStatus LIKE 'Closed'));


-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location


