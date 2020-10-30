-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 38

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
SELECT C.CUSTID, C.FNAME,C.LNAME, TO_CHAR(A.ACCCLOSEDDATE,'DD-MON-YYYY')
FROM CUSTOMER C
JOIN ACCOUNT A 
ON A.CUSTOMER = C.CUSTID
WHERE A.ACCOPENLOCATION = 'Central'
AND A.ACCCLOSEDDATE >= '01-MAR-2017';


-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
SELECT ACCOPENLOCATION, ACCSTATUS, COUNT(ACCSTATUS) AS NUMSTATUS
FROM ACCOUNT 
GROUP BY ACCOPENLOCATION,ACCSTATUS
ORDER BY ACCSTATUS;

-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
SELECT A.ACCNUMBER 
FROM ACCOUNT A
WHERE A.ACCBALANCE > (
        SELECT AVG(AC.ACCBALANCE) 
        FROM ACCOUNT AC
        WHERE AC.ACCTYPE ='checking'
        OR AC.ACCTYPE = 'savings')
ORDER BY A.ACCNUMBER;        


-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
-- -- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
-- --    number to display with a limited number of decimal places; 
-- --    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
SELECT T.TRANSLOCATION, CAST(AVG(T.TRANSAMOUNT) AS NUMBER(*,2)) AS AVGTRANS
FROM TRANSACTION T
GROUP BY T.TRANSLOCATION
HAVING AVG(T.TRANSAMOUNT) < (
        SELECT AVG(TR.TRANSAMOUNT) 
        FROM TRANSACTION TR)
ORDER BY AVG(T.TRANSAMOUNT) DESC;     


-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
SELECT CUSTID, FNAME, LNAME, MAX(T.TRANSAMOUNT) AS  MAXTRANS
FROM CUSTOMER C
JOIN ACCOUNT A
ON A.CUSTOMER = C.CUSTID
JOIN TRANSACTION T
ON T.ACCNUMBER = A.ACCNUMBER
WHERE T.TRANSTYPE = 'w'
AND ACCTYPE ='savings'
GROUP BY CUSTID,FNAME,LNAME
ORDER BY CUSTID;



-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
SELECT CUSTID, FNAME,LNAME
FROM CUSTOMER C
FULL OUTER JOIN ACCOUNT A
ON A.CUSTOMER = C.CUSTID
WHERE ACCSTATUS != 'Active'
UNION
SELECT CUSTID, FNAME,LNAME
FROM CUSTOMER CU
RIGHT JOIN ACCOUNT AC
ON AC.CUSTOMER = CU.CUSTID
WHERE ACCNUMBER IS NULL ;




-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location

SELECT T.TRANSLOCATION, COUNT(T.ACCNUMBER) 
FROM TRANSACTION T
RIGHT JOIN ACCOUNT A
ON T.ACCNUMBER = A.ACCNUMBER
GROUP BY T.TRANSLOCATION;
