-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 50

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.

SELECT C.CustID, C.FName, C.LName, A.AccClosedDate FROM CUSTOMER C 
JOIN ACCOUNT A ON (C.CustID = A.Customer) 
WHERE A.AccClosedDate >= TO_DATE('01-Mar-2017', 'DD-Mon-YYYY');

-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>

-- ASSUMPTION: If the OpenLocation doesn't have Accounts of some type (Active, Closed, Frozen) it won't need to print them

SELECT DISTINCT AccOpenLocation, AccStatus, count(AccNumber) FROM Account 
group by AccOpenLocation, AccStatus 
order by AccOpenLocation;

-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).

SELECT A.AccNumber FROM ACCOUNT A WHERE A.AccBalance > (
    SELECT AVG(B.AccBalance) FROM ACCOUNT B 
    WHERE B.AccType = A.AccType) 
ORDER BY A.AccNumber;

-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
-- -- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
-- --    number to display with a limited number of decimal places; 
-- --    e.g. CAST (AVG(value) AS NUMBER(*,2)) 

SELECT DISTINCT T.TransLocation, CAST(averageForLoc.average AS NUMBER(*,2)) FROM Transaction T JOIN (
    SELECT Trans.TransLocation, AVG(Trans.TransAmount) average FROM Transaction Trans 
    group by Trans.TransLocation) averageForLoc 
ON(T.TransLocation = averageForLoc.TransLocation) 
WHERE averageForLoc.average < (
    SELECT AVG(allTrans.TransAmount) FROM Transaction allTrans) 
ORDER BY CAST(averageForLoc.average AS NUMBER(*,2)) DESC;

-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.

SELECT A.Customer, C.FName, C.LName, T.maximum FROM Account A JOIN Customer C ON(C.CustID = A.Customer) 
JOIN (
    SELECT MAX(Trans.TransAmount) maximum, Trans.AccNumber accnum FROM Transaction Trans 
    group by Trans.AccNumber) T 
    ON(T.accnum = A.AccNumber) WHERE A.AccType = 'savings' 
    ORDER BY A.Customer;

-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.

SELECT C.CustID, C.FName, C.LName FROM Customer C 
WHERE C.CustID NOT IN(
    SELECT A.Customer FROM Account A)
OR C.CustID NOT IN(
    SELECT B.Customer FROM Account B
    WHERE B.AccStatus = 'Active');

-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location

SELECT DISTINCT TransLocation, locationCount.counter FROM Transaction FULL OUTER JOIN (
    SELECT count(AccNumber) counter, AccOpenLocation accLocation FROM Account 
    group by AccOpenLocation) locationCount 
ON(TransLocation = locationCount.accLocation)
ORDER BY locationCount.counter;
