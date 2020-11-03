-- -- CS 260, Fall 2019, Lab Test
-- -- Name: STUDENT 32

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
SELECT C.CUSTID, C.FNAME, C.LNAME, A.ACCCLOSEDDATE
FROM ACCOUNT A
JOIN CUSTOMER C
ON A.CUSTOMER = C.CUSTID
WHERE ACCOPENLACATION = 'Central'
AND 0 <
    (SELECT DATEDIFF(DAY,'2017-03-01',GETDATE()) 
    )
;


-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
SELECT ACCOPENLOCATION, ACCSTATUS, COUNT(ACCSTATUS)
FROM ACCOUNT 
GROUP BY ACCOPENLOCATION,ACCSTATUS
ORDER BY ACCOPENLOCATION,ACCSTATUS
;



-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
SELECT A.ACCNUMBER
FROM ACCOUNT A
WHERE A.ACCBALANCE > 
    (SELECT AVG(ACCBALANCE)
    FROM ACCOUNT AA
    WHERE A.ACCTYPE = AA.ACCTYPE
    )
ORDER BY A.ACCNUMBER
;


-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
SELECT TRANSLOCATION, AVG(TRANSAMOUNT)
FROM TRANSACTION T
WHERE T.TRANSAMOUNT <
    (SELECT AVG(TRANSAMOUNT)
    FROM TRANSACTION
    )
GROUP BY TRANSLOCATION
ORDER BY AVG(TRANSAMOUNT) DESC
;


-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
SELECT C.CUSTID, C.FNAME, C.LNAME, MAX(TRANSAMOUNT)
FROM CUSTOMER C
JOIN ACCOUNT A
ON C.CUSTID = A.CUSTOMER  
JOIN TRANSACTION T
ON A.ACCNUMBER = T.ACCNUMBER
WHERE TRANSTYPE = 'w'
AND A.ACCTYPE = 'savings'
GROUP BY C.CUSTID, C.FNAME, C.LNAME
;






-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
SELECT CUSTID, FNAME, LNAME
FROM CUSTOMER
LEFT JOIN ACCOUNT
ON CUSTID = CUSTOMER
WHERE ACCSTATUS = 'Closed'
OR ACCSTATUS = 'Frozen'
;


-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location
SELECT TRANSLOCATION, COUNT(ACCOPENLOCATION)
FROM TRANSACTION
LEFT JOIN ACCOUNT
ON TRANSLOCATION = ACCOPENLOCATION
GROUP BY TRANSLOCATION
;

