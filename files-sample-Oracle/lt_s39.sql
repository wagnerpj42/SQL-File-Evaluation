-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 39

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
SELECT CUSTID,FNAME,LNAME,AccClosedDate
FROM CUSTOMER C
JOIN ACCOUNT A ON (A.CUSTOMER = C.CUSTID)
WHERE AccStatus = 'Closed'
AND AccOpenLocation = 'Central'
AND AccClosedDate >= '01-MAR-2017';


-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
SELECT AccOpenLocation,AccStatus,COUNT(*)
FROM ACCOUNT
GROUP BY AccOpenLocation,AccStatus
ORDER BY AccOpenLocation,AccStatus;


-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
SELECT A1.ACCNUMBER
FROM ACCOUNT A1
WHERE A1.ACCNUMBER >
      (SELECT AVG(A2.ACCNUMBER)
       FROM ACCOUNT A2
       WHERE A2.ACCTYPE=A1.ACCTYPE);


-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
-- -- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
-- --    number to display with a limited number of decimal places; 
-- --    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
SELECT TRANSLOCATION,CAST (AVG(TRANSAMOUNT) AS NUMBER(*,2))
FROM TRANSACTION
GROUP BY TRANSLOCATION
HAVING AVG(TRANSAMOUNT)<
       (SELECT AVG(TRANSAMOUNT)
        FROM TRANSACTION)
ORDER BY AVG(TRANSAMOUNT) DESC;



-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
SELECT CUSTID,FNAME,LNAME,MAX(TRANSAMOUNT)
FROM CUSTOMER C
JOIN ACCOUNT A ON (A.CUSTOMER = C.CUSTID)
JOIN TRANSACTION T ON (T.ACCNUMBER = A.ACCNUMBER)
WHERE ACCTYPE = 'savings'
AND TRANSTYPE = 'w'
GROUP BY  CUSTID,FNAME,LNAME
ORDER BY CUSTID;



-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
SELECT DISTINCT CUSTID,FNAME,LNAME
FROM CUSTOMER C
JOIN ACCOUNT A ON (A.CUSTOMER = C.CUSTID)
WHERE A.ACCSTATUS !='active'
UNION
SELECT CUSTID,FNAME,LNAME
FROM CUSTOMER
WHERE NOT EXISTS
     (SELECT *
      FROM ACCOUNT
      WHERE CUSTOMER = CUSTID);

-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location
SELECT TRANSLOCATION, COUNT(DISTINCT A.ACCNUMBER)
FROM TRANSACTION T
JOIN ACCOUNT A ON (A.ACCNUMBER = T.ACCNUMBER)
GROUP BY TRANSLOCATION;

