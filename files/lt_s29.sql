-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 29 (added by instructor)

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
SELECT C.CUSTID, C.FNAME, C.LNAME, A.ACCCLOSEDDATE FROM ACCOUNT A
JOIN CUSTOMER C 
ON (C.CUSTID = A.CUSTOMER) 
WHERE (LOWER(A.ACCOPENLOCATION) = 'central' AND A.ACCCLOSEDDATE >= '01-MAR-2017');

-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
SELECT a.accopenlocation, a.accstatus, count(a.accstatus) FROM ACCOUNT A
group by accopenlocation,accstatus
ORDER BY ACCOPENLOCATION ASC;

-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
SELECT ACCNUMBER, ACCTYPE FROM ACCOUNT A
WHERE (A.ACCBALANCE > 
    (SELECT AVG(A2.ACCBALANCE) FROM ACCOUNT A2
    WHERE A2.ACCTYPE = A.ACCTYPE));
    -- DO NOT KNOW WHY THIS IS NOT WORKING ORDER BY ACCNUMBER));


-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
-- -- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
-- --    number to display with a limited number of decimal places; 
-- --    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
SELECT T.TRANSLOCATION, AVG(T.TRANSAMOUNT)  FROM TRANSACTION T
JOIN TRANSACTION T2
ON(T2.TRANSLOCATION = T.TRANSLOCATION)
--WHERE AVG(T.TRANSAMOUNT) > AVG(T2.TRANSAMOUNT)
GROUP BY T.TRANSLOCATION; 
--WHERE (AVG(T.TRANSAMOUNT) >
    --(SELECT AVG(T2.TRANSAMOUNT) FROM TRANSAMOUNT TS));
    --GROUP BY T.TRANSLOCATION, AVG(T.TRANSAMOUNT)));





-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
SELECT C.CUSTID, C.FNAME, C.LNAME, MAX(TRANSAMOUNT) FROM CUSTOMER C
JOIN ACCOUNT A 
ON(C.CUSTID = A.CUSTOMER) 
JOIN TRANSACTION T 
ON (T.ACCNUMBER = A.ACCNUMBER)
WHERE lower(TRANSTYPE) = 'w'
GROUP BY C.CUSTID, C.FNAME, C.LNAME
ORDER BY C.CUSTID ASC;


-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
SELECT CUSTID, FNAME, LNAME FROM CUSTOMER c 
FULL OUTER JOIN  ACCOUNT A 
ON(C.CUSTID = A.CUSTOMER)
WHERE LOWER(A.ACCSTATUS) = 'closed' or lower(a.accstatus) = 'frozen' OR LOWER(A.ACCSTATUS) = '0';

 

-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location


