-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 41

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
SELECT CustID, FName, LName, AccClosedDate FROM Customer c
JOIN Account a
ON c.CustID = a.Customer
WHERE AccOpenLocation = 'Central' AND AccClosedDate > '2017-03-01';


-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
SELECT a.AccOpenLocation, a.AccStatus, COUNT(b.AccStatus) FROM Account a
LEFT JOIN Account b
ON a.AccNumber = b.AccNumber
GROUP BY a.AccOpenLocation, a.AccStatus
ORDER BY a.AccOpenLocation, a.AccStatus;


-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
SELECT AccNumber FROM Account a1
WHERE a1.AccBalance > 
    (SELECT AVG(AccBalance) FROM Account a2
    WHERE a2.AccType = a1.AccType
    )
ORDER BY a1.AccNumber ASC;


-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
SELECT TransLocation, AVG(TransAmount) AS avg_amount FROM Transaction
GROUP BY TransLocation
HAVING AVG(TransAmount) < 
    (SELECT AVG(TransAmount) FROM Transaction
    )
ORDER BY avg_amount DESC;


-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
SELECT CustID, FName, LName, MAX(TransAmount) FROM Customer
JOIN Account a 
ON CustID = Customer
JOIN Transaction t
ON a.AccNumber = t.AccNumber AND t.TransType = 'w'
GROUP BY CustID, FName, LName
ORDER BY CustID;

-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
SELECT DISTINCT CustID, FName, LName from Customer c
JOIN Account a
ON c.CustID = a.Customer
WHERE AccStatus NOT LIKE 'Active';


-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location
SELECT b.TransLocation, COUNT(a.AccOpenLocation) FROM Transaction b
Right JOIN Account a
ON a.AccNumber = b.AccNumber
GROUP BY b.TransLocation;

