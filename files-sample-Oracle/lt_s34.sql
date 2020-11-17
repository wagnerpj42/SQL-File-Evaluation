-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 34

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.

SELECT A.Customer, C.FNAME, C.LNAME, A.ACCCLOSEDDATE
FROM Account A
JOIN Customer C
ON (A.Customer = C.CustID)
WHERE (A.ACCOPENLOCATION = 'Central') AND (A.ACCCLOSEDDATE >= '01-MAR-17');

-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>

SELECT A.ACCOPENLOCATION, A.ACCSTATUS, Count(A.Customer)
FROM Account A
GROUP BY A.ACCOPENLOCATION, A.ACCSTATUS
ORDER BY A.ACCOPENLOCATION ASC, A.ACCSTATUS ASC;

-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).

SELECT B.ACCNUMBER
FROM Account B
JOIN (
    SELECT A.ACCTYPE, AVG(A.ACCBALANCE) AS AVGBAL
    FROM Account A
    GROUP BY  A.ACCTYPE) AB
ON B.ACCTYPE = AB.ACCTYPE
WHERE B.ACCBALANCE > AB.AVGBAL;


-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
-- -- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
-- --    number to display with a limited number of decimal places; 
-- --    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
SELECT DISTINCT T1.TRANSLOCATION, T3.AVGLOCATION
FROM TRANSACTION T1

JOIN (  SELECT TA.TRANSLOCATION, AVG(TA.TRANSAMOUNT) AS AVGLOCATION
        FROM TRANSACTION TA
        GROUP BY TA.TRANSLOCATION) T3
ON (T1.TRANSLOCATION = T3.TRANSLOCATION)

JOIN (  SELECT AVG(T.TRANSAMOUNT) AS AVGT
        FROM TRANSACTION T) T2
ON (T3.AVGLOCATION < T2.AVGT)
ORDER BY T3.AVGLOCATION DESC;


-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
SELECT A.CUSTOMER, C.FNAME, C.LNAME, T1.MAXW
FROM ACCOUNT A
JOIN (
    SELECT  T.ACCNUMBER, MAX(TRANSAMOUNT) AS MAXW
    FROM TRANSACTION T
    WHERE (TRANSTYPE = 'w')
    GROUP BY T.ACCNUMBER) T1
ON (A.ACCNUMBER = T1.ACCNUMBER)
JOIN CUSTOMER C
ON (A.CUSTOMER = C.CUSTID)
WHERE A.ACCTYPE = 'savings';

-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.

SELECT C.CUSTID, C.FNAME, C.LNAME 
FROM ACCOUNT A
RIGHT OUTER JOIN CUSTOMER C
ON (A.CUSTOMER = C.CUSTID)
WHERE C.CUSTID NOT IN (
    SELECT A1.CUSTOMER 
    FROM ACCOUNT A1
    WHERE A1.ACCSTATUS = 'Active');

-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location

SELECT T.TRANSLOCATION, COUNT(A.ACCNUMBER)
FROM TRANSACTION T
LEFT OUTER JOIN ACCOUNT A
ON T.TRANSLOCATION = A.ACCOPENLOCATION AND T.ACCNUMBER = A.ACCNUMBER
GROUP BY T.TRANSLOCATION;

