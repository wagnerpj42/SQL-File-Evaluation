

--- CS 260, Spring 2019, Lab Test
--- Name: Nicholas Fischer

--- Table List:
--- Customer ( _CustID_ , FName, LName, PIN)
--- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
---                         AccOpenDate, AccClosedDate, AccStatus)
--- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
---                         TransType, TransLocation)


--- 1. (15 points) List the customer id, both name fields, and the account closed 
---    date for customers whose accounts opened in the Central location but have 
---    been closed on or after January 1st, 2018.
SELECT custid, fname, lname, acccloseddate FROM Customer C
JOIN Account A ON (C.custid = a.customer)
WHERE A.acccloseddate >= '01-JAN-2018'
AND A.accopenlocation = 'Central';


--- 2. (16 points) For each account opening location, show how many active, closed,
---    and frozen, and accounts there are.  Display the results with the locations
---    and status values in alphabetical order.
---        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
---        Central        Active        10
---        Central        Closed        20
---        Central        Frozen         1
---        <similar for other branches>
SELECT A.accopenlocation, A.accstatus, COUNT(*)
FROM Account A 
GROUP BY A.accopenlocation, A.accstatus
ORDER BY A.accopenlocation ASC;


--- 3. (18 points) List the accounts (by account number, in numeric order) 
---    with an account balance greater than the average account balance 
---    for all accounts of the same account type (checking or savings).
SELECT A.Accnumber FROM Account A 
WHERE A.accbalance > (SELECT AVG(B.accbalance) FROM Account B 
                      WHERE A.acctype = B.acctype)
                      ORDER BY A.accnumber ASC;


--- 4. (18 points) Display the transaction location and the average transaction 
---    amount for each transaction location where that average transaction amount
---    is less than the average transaction amount for all transactions.  Order the
---    results by average transaction amount, largest to smallest.
--- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
---    number to display with a limited number of decimal places; 
---    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
SELECT T.translocation, AVG(T.transamount)AS AverageTransact FROM Transaction T
GROUP BY T.translocation
HAVING AVG(T.transamount) <(SELECT AVG(A.transamount) FROM Transaction A)
ORDER BY AVG(T.transamount) DESC;
 


--- 5. (16 points) Find each customer id, first name, and last name, plus the 
---    amount for the largest 'w' (withdrawal) transaction related to a 
---    savings account which that customer has opened.  Display the results 
---    in order by customer id.
SELECT C.custid, C.fname, C.lname, MAX(T.Transamount) FROM Customer C
JOIN Account A ON(C.custid = A.customer)
JOIN Transaction T ON (T.accnumber = A.accnumber)
WHERE A.acctype = 'savings' AND T.transtype = 'w'
GROUP BY C.custid,C.fname,C.lname
ORDER BY C.custid ASC;



--- 6. (17 points) List the customer id, first name and last name of any 
---    customers who have no active accounts.  This includes A) customers who 
---    have accounts, but they only have accounts that are closed and/or frozen, 
---    and also B) customers who do not have any accounts at all.
SELECT DISTINCT C.custid, C.fname, C.lname FROM Customer C 
LEFT OUTER JOIN Account A ON (C.custid = A.customer)
WHERE A.accstatus != 'Active';


--- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
---    display the number of accounts opened at that location, even if no 
---    accounts were opened at that location.
--- NOTE: at least one transaction location has no accounts opened at that location
SELECT DISTINCT T.Translocation, COUNT(*) FROM Account A 
RIGHT OUTER JOIN Transaction T ON (T.translocation = A.accopenlocation)
GROUP BY T.Translocation;
--LEFT OUTER JOIN Account A ON (T.translocation = A.accopenlocation)
--GROUP BY T.translocation;

