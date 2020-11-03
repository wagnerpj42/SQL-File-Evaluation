-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 47

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
SELECT c.custid, c.fname, c.lname, a.acccloseddate
FROM Customer c
JOIN Account a ON c.custid = a.customer
WHERE a.accopenlocation = 'Central' AND a.acccloseddate >= '2017-03-01';


-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
SELECT a.accopenlocation, a.accstatus, count(a.accnumber)
FROM Account a
GROUP BY a.accstatus, a.accopenlocation
ORDER BY a.accopenlocation ASC, a.accstatus ASC;


-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
SELECT DISTINCT a.accnumber
FROM Account a
WHERE a.accbalance >
    (SELECT AVG(asub.accbalance)
    FROM Account asub
    WHERE asub.acctype = a.acctype)
ORDER BY a.accnumber ASC;


-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
SELECT t.translocation, AVG(t.transamount) AS averageamount
FROM Transaction t
WHERE t.transamount > (
    SELECT AVG(tsub.transamount)
    FROM Transaction tsub)
GROUP BY t.translocation
ORDER BY AVG(t.transamount) DESC;

    
-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
SELECT DISTINCT c.custid, c.fname, c.lname, t.transamount
FROM Account a
JOIN Customer c ON a.customer = c.custid
JOIN Transaction t ON t.accnumber = a.accnumber
WHERE a.acctype = 'savings' AND t.transtype = 'w' AND t.transid IN (
    SELECT tsub.transid
    FROM Transaction tsub
    WHERE tsub.accnumber = a.accnumber AND tsub.transtype = 'w' AND tsub.transamount = (
        SELECT MAX(tsub2.transamount)
        FROM Transaction tsub2
        WHERE tsub2.accnumber = a.accnumber AND tsub2.transtype = 'w'
        )
    )
ORDER BY c.custid;


-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
SELECT DISTINCT c.custid, c.fname, c.lname
FROM Customer c
LEFT JOIN Account a ON c.custid = a.customer
WHERE a.accnumber NOT IN (
    SELECT asub.accnumber
    FROM Account asub
    WHERE a.accstatus = 'Active');
    

-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location
SELECT t.translocation, COUNT(a.accnumber)
FROM Transaction t
RIGHT JOIN Account a ON t.accnumber = a.accnumber
WHERE a.accopenlocation = t.translocation
GROUP BY t.translocation;

