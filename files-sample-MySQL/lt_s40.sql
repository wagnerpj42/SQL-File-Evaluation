-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 40

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
SELECT C.custid, C.fname, C.lname, A.acccloseddate
FROM Customer C
JOIN Account A
ON (C.custid = A.customer)
WHERE A.accopenlocation = 'Central'
AND A.acccloseddate >= '2017-03-01';


-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
SELECT A.accopenlocation, A.accstatus, COUNT(A.accstatus)
FROM Account A
GROUP BY A.accopenlocation, A.accstatus
ORDER BY A.accopenlocation;


-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
SELECT A.accnumber
FROM Account A
WHERE A.accbalance >
    (SELECT AVG(A1.accbalance)
    FROM Account A1
    WHERE A.acctype = A1.acctype
    GROUP BY A1.acctype);


-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
SELECT T.translocation, AVG(T.transamount)
FROM Transaction T
GROUP BY T.translocation
HAVING AVG(T.transamount) <
    (SELECT AVG(T1.transamount)
    FROM Transaction T1)
ORDER BY AVG(T.transamount) DESC;


-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
SELECT C.custid, C.fname, C.lname, MAX(T.transamount)
FROM Customer C
JOIN Account A
ON (C.custid = A.customer)
JOIN Transaction T
ON (A.accnumber = T.accnumber)
WHERE A.acctype = 'savings'
AND T.transtype = 'w'
GROUP BY C.custid, C.fname, C.lname
ORDER BY C.custid;


-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
SELECT DISTINCT C.custid, C.fname, C.lname
FROM Customer C
LEFT OUTER JOIN Account A
ON (C.custid = A.customer)
WHERE A.accnumber IS NULL
UNION
SELECT DISTINCT C.custid, C.fname, C.lname
FROM Customer C
LEFT OUTER JOIN Account A
ON (C.custid = A.customer)
WHERE A.accstatus = 'Closed'
UNION
SELECT DISTINCT C.custid, C.fname, C.lname
FROM Customer C
LEFT OUTER JOIN Account A
ON (C.custid = A.customer)
WHERE A.accstatus = 'Frozen';


-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location
SELECT T.translocation, COUNT(A.accnumber)
FROM Transaction T
LEFT OUTER JOIN Account A
ON (T.accnumber = A.accnumber)
GROUP BY T.translocation;

