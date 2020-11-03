-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 24

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
SELECT c.custid, c.FNAME, c.LNAME, a.ACCCLOSEDDATE
FROM ACCOUNT a
JOIN CUSTOMER c
ON (c.custid = a.customer)
WHERE a.ACCOPENLOCATION = 'Central' AND a.acccloseddate >  '01-MAR-17';





-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
SELECT accopenlocation, COUNT(accstatus)
FROM account
GROUP BY accstatus
HAVING COUNT > -1;

    






-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
SELECT a.accnumber
FROM Account a
WHERE a.acctype IN
    (SELECT b.acctype
    FROM account b
    GROUP BY acctype
    HAVING AVG(a.accbalance) > accbalance AND AVG (b.accbalance)> accbalance);





-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
-- -- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
-- --    number to display with a limited number of decimal places; 
-- --    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
SELECT t.translocation, CAST (AVG(t.transamount) AS NUMBER(*,2))
FROM transaction t
WHERE  AVG(tranamount) < t.transammount;



-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.

SELECT c.custid, c.fname, c.lname, t.transamount
FROM customer c
JOIN account a
ON(c.custid = a.accnumber)
WHERE a.accnumber IN
    (SELECT t.accnumber
    FROM transaction t
    WHERE t.transtype = 'w'
        MINUS
        SELECT t.transammount
        FROM transaction
        WHERE t.transammount = MAX);
    




-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
SELECT c.custid, c.fname, c.lname
FROM customer c
JOIN account a
ON(c.custid = a.accnumber)
WHERE accstatus = lower('closed');


-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location


