-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 21

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
SELECT c.CustID, c.FName, c.LName, a.AccClosedDate
FROM Account a 
JOIN Customer c ON c.CustID = a.Customer
WHERE a.AccOpenLocation = 'Central' AND a.AccClosedDate >= '01-MAR-2017';

-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
SELECT AccOpenLocation, AccStatus, COUNT(*)
FROM Account
GROUP BY AccStatus, AccOpenLocation
ORDER BY accopenlocation;

-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
SELECT AccNumber
FROM Account a
WHERE AccBalance > (SELECT AVG(AccBalance)
                    FROM Account b
                    WHERE b.AccType = a.AccType)
ORDER BY a.accnumber;

-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
-- -- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
-- --    number to display with a limited number of decimal places; 
-- --    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
SELECT t.TransLocation, AVG(t.TransAmount)
FROM transaction t
WHERE (SELECT AVG(t1.TransAmount)
        FROM transaction t1
        WHERE t1.TransLocation = t.TransLocation) < (SELECT AVG(t2.TransAmount)
                                FROM transaction t2) 
GROUP BY t.TransLocation;

-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
SELECT a.Customer, c.FName, c.LName, MAX(t.TransAmount)
FROM Account a
JOIN Customer c ON c.custid = a.customer
JOIN Transaction t ON a.accnumber = t.accnumber
WHERE t.transtype = 'w' 
group by a.Customer, c.FName, c.LName;


-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
SELECT CustID, FName, LName, a.accstatus
FROM Customer c
LEFT JOIN Account a ON c.custid = a.customer
WHERE a.Customer IS NULL;

-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location


