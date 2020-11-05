-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 09

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
SELECT CUSTID, FNAME, LNAME, ACCCLOSEDDATE
FROM CUSTOMER C
JOIN ACCOUNT A
ON (C.CUSTID = A.CUSTOMER)
WHERE ACCCLOSEDDATE > '2017-03-01';


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
GROUP BY ACCOPENLOCATION, ACCSTATUS
ORDER BY ACCOPENLOCATION, ACCSTATUS;
--ASSUMED THAT YOU DIDN'T WANT TO DISPLAY IF THEY HAD 0 ACTIVE, CLOSED, OR FROZEN ACCOUNTS

-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
SELECT ACCNUMBER, ACCTYPE, ACCBALANCE
FROM ACCOUNT A1
WHERE ACCBALANCE > (
    SELECT AVG(ACCBALANCE)
    FROM ACCOUNT A2
    WHERE A1.ACCTYPE = A2.ACCTYPE);


-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
SELECT TRANSLOCATION, AVG(TRANSAMOUNT)
FROM TRANSACTION 
GROUP BY TRANSLOCATION
HAVING AVG(TRANSAMOUNT) < (
    SELECT AVG(TRANSAMOUNT)
    FROM TRANSACTION
)
ORDER BY AVG(TRANSAMOUNT) DESC;


-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
SELECT CUSTID, FNAME, LNAME, MAX(TRANSAMOUNT)
FROM ACCOUNT A
JOIN CUSTOMER C
ON (C.CUSTID = A.CUSTOMER)
JOIN TRANSACTION T
ON (A.ACCNUMBER = T.ACCNUMBER)
WHERE TRANSTYPE = 'w' AND ACCTYPE = 'savings'
GROUP BY CUSTID, FNAME, LNAME
ORDER BY CUSTID;

-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
SELECT CUSTID, FNAME, LNAME
FROM CUSTOMER
WHERE CUSTID NOT IN (
SELECT CUSTID
FROM CUSTOMER
JOIN ACCOUNT 
ON (CUSTOMER.CUSTID = ACCOUNT.CUSTOMER)
WHERE ACCSTATUS = 'Active');



-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location
SELECT TRANSLOCATION, COUNT(A.ACCOPENLOCATION)
FROM TRANSACTION T
LEFT JOIN ACCOUNT A
ON (T.TRANSLOCATION = A.ACCOPENLOCATION)
GROUP BY TRANSLOCATION;


