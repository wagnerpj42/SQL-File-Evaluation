-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 08

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)

--SELECT * FROM Customer;
--SELECT * FROM Account;
--SELECT * FROM Transaction;

-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
SELECT C.CUSTID, C.FNAME, C.LNAME, A.ACCCLOSEDDATE
FROM Customer C
JOIN Account A ON (A.CUSTOMER = C.CUSTID)
WHERE (A.ACCOPENLOCATION) LIKE 'Central' AND (A.ACCCLOSEDDATE >= '01-MAR-2017');


-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
SELECT ACCOPENLOCATION, ACCSTATUS, COUNT(ACCSTATUS)
FROM Account
GROUP BY ACCOPENLOCATION, ACCSTATUS
ORDER BY ACCOPENLOCATION, ACCSTATUS ASC;


-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
SELECT DISTINCT A.ACCNUMBER
FROM Account A
JOIN Account A2 ON (A.ACCBALANCE > A2.ACCBALANCE AND A.ACCTYPE = A2.ACCTYPE);

-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
-- -- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
-- --    number to display with a limited number of decimal places; 
-- --    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
SELECT T.TRANSLOCATION, CAST(AVG(T.TRANSAMOUNT) AS NUMBER(*,2))
FROM Transaction T
WHERE T.TRANSAMOUNT <
    (SELECT AVG(T2.TRANSAMOUNT)
    FROM Transaction T2
    WHERE (T.TRANSTYPE = T2.TRANSTYPE))
GROUP BY T.TRANSLOCATION;


-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
SELECT C.CUSTID, C.FNAME, C.LNAME, T.TRANSAMOUNT
FROM Transaction T
JOIN Account A ON (A.ACCNUMBER = T.ACCNUMBER)
JOIN Customer C ON (A.CUSTOMER = C.CUSTID)
WHERE (A.ACCTYPE LIKE 's%') AND (T.TRANSTYPE LIKE 'w')
ORDER BY C.CUSTID;


-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
SELECT C.CUSTID, C.FNAME, C.LNAME
FROM Customer C
MINUS 
    (SELECT C2.CUSTID, C2.FNAME, C2.LNAME
    FROM Customer C2
    WHERE C2.CUSTID IN
        (SELECT A.CUSTOMER
        FROM Account A
        WHERE (ACCSTATUS LIKE 'Closed') OR (ACCSTATUS LIKE 'Frozen'))
    );

-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location


