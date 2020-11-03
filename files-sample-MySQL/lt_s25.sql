-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 25

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
--Can join based on a foreign key.
--THIS ONE IS WRONG
SELECT DISTINCT c.custID, c.fName, c.lName, a.AccClosedDate
FROM Customer c
JOIN Account a
ON (c.custID = a.Customer)
WHERE a.accStatus LIKE 'Closed';


-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
SELECT DISTINCT a.AccOpenLocation, a.AccStatus, COUNT(a.accStatus) AS AccStatusNum
FROM Account a
GROUP BY a.AccOpenLocation, a.AccStatus
ORDER BY a.AccOpenLocation, a.AccStatus ASC;


-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
--Self join
SELECT a1.accNumber
FROM Account a1
WHERE a1.accBalance > 
    (SELECT AVG(a2.accBalance)
     FROM Account a2
     WHERE a1.accType = a2.accType
    )
ORDER BY a1.accNumber ASC;

-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
SELECT DISTINCT t1.transLocation, AVG(t1.transAmount) AS TransAmount
FROM Transaction t1
WHERE t1.transAmount < 
    (SELECT AVG(t2.transAmount)
     FROM Transaction t2
     WHERE t1.transLocation LIKE t2.transLocation
    )
GROUP BY t1.transLocation
ORDER BY AVG(t1.transAmount) DESC;

-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
SELECT DISTINCT c.custID, c.fName, c.lName, t.transAmount
FROM Customer c
JOIN Account a
ON (Customer = a.customer)
JOIN Transaction t
ON (a.accNumber = t.accNumber)
WHERE t.transType LIKE 'w' AND a.accType = 'savings' 
GROUP BY c.custID, c.fName, c.lName, t.transAmount
ORDER BY c.custID ASC;


-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
SELECT DISTINCT c.custID, c.fName, c.lName
FROM Customer c
LEFT OUTER JOIN Account a
ON (c.custID = a.Customer)
WHERE a.accStatus LIKE 'Frozen' OR a.accStatus LIKE 'Closed';

-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location


