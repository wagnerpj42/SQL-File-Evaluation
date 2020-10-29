-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 04
 
-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
SELECT C.CustID, C.Fname, C.Lname, A.AccClosedDate
FROM Customer C
JOIN Account A
ON A.Customer = C.CustID
WHERE A.AccOpenLocation = 'Central' 
    AND A.AccClosedDate >= '01-MAR-2017';


-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>



SELECT AccOpenLocation, AccStatus, COUNT(AccNumber)
FROM Account
GROUP BY AccOpenLocation, AccStatus, COUNT(AccNumber)
HAVING COUNT(AccNumber) = (
    SELECT COUNT(*)
    FROM Account
    WHERE AccStatus = 'Active'
    ) OR COUNT(AccNumber) = (
    SELECT COUNT(*)
    FROM Account
    WHERE AccStatus = 'Closed'
    ) OR COUNT(AccNumber) = (
    SELECT COUNT(*)
    FROM Account
    WHERE AccStatus = 'Frozen'
    ) 
    ORDER BY AccOpenLocation ASC, AccStatus ASC;

--I also tried this solution for question 2:
--SELECT AccOpenLocation, AccStatus, COUNT(AccNumber)
--FROM Account
--GROUP BY AccOpenLocation, AccStatus, COUNT(AccNumber)
--HAVING AccStatus = 'Active'
--    UNION
--(SELECT AccOpenLocation, AccStatus, COUNT(AccNumber)
--FROM Account
--GROUP BY AccOpenLocation, AccStatus, COUNT(AccNumber)
--HAVING AccStatus = 'Closed')   
--    UNION
--(SELECT AccOpenLocation, AccStatus, COUNT(AccNumber)
--FROM Account
--GROUP BY AccOpenLocation, AccStatus, COUNT(AccNumber)
--HAVING AccStatus = 'Frozen');






-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
SELECT A1.AccNumber
FROM Account A1
WHERE A1.AccBalance <
    (SELECT AVG(A2.Accbalance)
     FROM Account A2
     WHERE A1.AccType = A2.Acctype
     )
ORDER BY A1.AccNumber ASC;


-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
-- -- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
-- --    number to display with a limited number of decimal places; 
-- --    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
SELECT T1.TransLocation, AVG(T1.TransAmount)
FROM Transaction T1
GROUP BY T1.TransLocation
HAVING AVG(T1.TransAmount) < 
    ( SELECT AVG(T2.TransAmount)
      FROM Transaction T2
    )
ORDER BY AVG(T1.TransAmount) DESC;    


-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.

    SELECT DISTINCT C.CustID, C.Fname, C.Lname, T.TransAmount
    FROM Customer C
    JOIN Account A
    ON C.CustID = A.Customer
    JOIN Transaction T
    ON A.AccNumber = T.AccNumber
    WHERE T.TransType = 'w' 
        AND A.AccType = 'savings'
    ORDER BY C.CustID;




-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
SELECT CustID, Fname, Lname
FROM Customer C
WHERE C.custID NOT IN (
    SELECT C2.CustID
    FROM CUSTOMER C2
    JOIN Account A2
    ON A2.Customer = C2.CustID
    WHERE A2.AccStatus = 'Active'
    );



-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location


