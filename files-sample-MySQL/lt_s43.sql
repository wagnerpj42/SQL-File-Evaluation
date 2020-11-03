-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 43

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
SELECT c.CUSTID, c.FNAME, c.LNAME, a.ACCCLOSEDDATE FROM CUSTOMER c
INNER JOIN ACCOUNT a ON c.CUSTID = a.customer
WHERE a.ACCOPENLOCATION LIKE '%Central%'  AND
a.ACCCLOSEDDATE >= '2017-03-01';




-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
SELECT a.ACCOPENLOCATION, a.ACCSTATUS, COUNT(*) FROM ACCOUNT a
GROUP BY a.ACCOPENLOCATION, a.ACCSTATU
ORDER BY a.ACCOPENLOCATION, a.ACCSTATUS;
    


-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
SELECT a.ACCNUMBER FROM ACCOUNT a
WHERE a.ACCBALANCE > ANY
    (SELECT AVG(a.ACCBALANCE) FROM ACCOUNT a
      WHERE a.ACCTYPE = a.ACCTYPE
      ORDER BY a.ACCNUMBER);


-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
SELECT AVG(t.TRANSAMOUNT), t.TRANSLOCATION FROM TRANSACTION t
WHERE t.TRANSAMOUNT > ALL
     (SELECT AVG(t.TRANSAMOUNT) FROM TRANSACTION t
      ORDER BY AVG(t.transamount) );

-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
SELECT c.CUSTID,c.FNAME,c.LNAME, MAX(t.TRANSAMOUNT) FROM CUSTOMER c
INNER JOIN ACCOUNT a ON c.CUSTID = a.CUSTOMER
INNER JOIN TRANSACTION t on a.ACCNUMBER = t.ACCNUMBER
WHERE t.TRANSTYPE LIKE '%w%'
GROUP BY t.TRANSAMOUNT,c.CUSTID,c.FNAME,c.LNAME
ORDER BY c.CUSTID;



-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
SELECT c.CUSTID, c.FNAME, c.LNAME FROM CUSTOMER c
LEFT OUTER JOIN ACCOUNT a ON c.CUSTID = a.CUSTOMER
WHERE a.ACCSTATUS LIKE '%Closed%' OR
a.ACCSTATUS LIKE '%Frozen%'
GROUP BY c.CUSTID, c.FNAME, c.LNAME;


-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location
SELECT t.TRANSLOCATION, COUNT(*) FROM TRANSACTION t
LEFT OUTER JOIN ACCOUNT a ON t.ACCNUMBER = a.ACCNUMBER
GROUP BY(t.TRANSLOCATION);

