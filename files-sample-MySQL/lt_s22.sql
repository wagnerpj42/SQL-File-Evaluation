-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 22

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
SELECT c.custid,c.fname,c.lname,a.acccloseddate
FROM customer c
JOIN account a ON c.custid = a.customer
WHERE a.accopenlocation LIKE 'Central' AND a.acccloseddate >= '2017-03-01';


-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
SELECT a.accopenlocation, a.accstatus, COUNT(a.accstatus)
FROM account a
GROUP BY a.accopenlocation , a.accstatus 
ORDER BY a.accopenlocation ASC;
   



-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).

SELECT a.accnumber
FROM account a
WHERE a.acctype IN
    (SELECT b.acctype
    FROM account b
    GROUP BY acctype
    HAVING AVG(b.accbalance) < a.accbalance)
    ORDER BY a.accnumber ASC;

-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
SELECT b.translocation, AVG(b.transamount)
FROM transaction a
JOIN transaction b ON  a.transid = b.transid
GROUP BY b.translocation
HAVING AVG(b.transamount) = AVG(a.transamount);





-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.

    SELECT t.accnumber, MAX(t.transamount)
    FROM transaction t
    JOIN account a
    ON a.accnumber = t.accnumber
    WHERE t.transtype LIKE 'w'
     AND a.acctype LIKE 'savings'
    GROUP BY t.accnumber;
          
   -- SELECT c.custid,c.fname,c.lname,MAX(t.transamount)
   -- FROM customer c
   -- JOIN account a ON c.custid = a.customer
    --JOIN transaction t ON a.accnumber = t.accnumber
    --WHERE t.transtype LIKE 'w'
   -- GROUP BY c.custid;
    




--SELECT MAX(transamount)
--FROM transaction
--GROUP BY transtype;


-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
SELECT c.custid, c.fname,c.lname
FROM customer c
JOIN account a
ON c.custid = a.customer
WHERE c.custid NOT IN (
SELECT c.custid
FROM customer c
JOIN account a
ON c.custid = a.customer
WHERE a.acctype LIKE 'Active');


-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location


