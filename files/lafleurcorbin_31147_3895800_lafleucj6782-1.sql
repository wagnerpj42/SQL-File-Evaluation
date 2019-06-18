--- CS 260, Spring 2019, Lab Test
--- Name: Corbin LaFleur

--- Table List:
--- Customer ( _CustID_ , FName, LName, PIN)
--- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
---                         AccOpenDate, AccClosedDate, AccStatus)
--- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
---                         TransType, TransLocation)


--- 1. (15 points) List the customer id, both name fields, and the account closed 
---    date for customers whose accounts opened in the Central location but have 
---    been closed on or after January 1st, 2018.

SELECT C.CUSTID, C.FNAME, C.LNAME, A.acccloseddate 
FROM customer C 
JOIN ACCOUNT A 
ON A.CUSTOMER = C.CUSTID 
WHERE A.ACCOPENLOCATION = 'Central' 
AND A.ACCCLOSEDDATE >= '1-JAN-18';
--done


--- 2. (16 points) For each account opening location, show how many active, closed,
---    and frozen, and accounts there are.  Display the results with the locations
---    and status values in alphabetical order.
---        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
---        Central        Active        10
---        Central        Closed        20
---        Central        Frozen         1
---        <similar for other branches>
SELECT A.ACCOPENLOCATION, A.accStatus, COUNT(A.ACCNUMBER) 
FROM ACCOUNT A 
GROUP BY A.ACCOPENLOCATION, A.ACCSTATUS 
ORDER BY A.ACCOPENLOCATION, A.ACCSTATUS;
--done


--- 3. (18 points) List the accounts (by account number, in numeric order) 
---    with an account balance greater than the average account balance 
---    for all accounts of the same account type (checking or savings).
SELECT A1.ACCNUMBER FROM ACCOUNT A1 WHERE A1.ACCBALANCE > (
  SELECT AVG(A2.accBalance) 
  FROM Account A2 
  WHERE A1.acctype = A2.acctype 
  GROUP BY A2.acctype) 
ORDER BY A1.ACCNUMBER;
--done


--SELECT A2.acctype, AVG(A2.accBalance) FROM Account A2 GROUP BY A2.acctype;
--- 4. (18 points) Display the transaction location and the average transaction 
---    amount for each transaction location where that average transaction amount
---    is less than the average transaction amount for all transactions.  Order the
---    results by average transaction amount, largest to smallest.
--- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
---    number to display with a limited number of decimal places; 
---    e.g. CAST (AVG(value) AS NUMBER(*,2)) 

SELECT T.TRANSLOCATION, CAST (AVG(T.TRANSAMOUNT) AS NUMBER(*,2)) AS AveragePerLocation
FROM Transaction T 
GROUP BY T.TRANSLOCATION 
HAVING AVG(T.transamount)<
  (SELECT AVG(T2.transamount) 
  FROM TRANSACTION T2) 
ORDER BY AVG(T.TRANSAMOUNT) DESC;
--done


--- 5. (16 points) Find each customer id, first name, and last name, plus the 
---    amount for the largest 'w' (withdrawal) transaction related to a 
---    savings account which that customer has opened.  Display the results 
---    in order by customer id.
SELECT c2.CUSTID, c2.FNAME, c2.LNAME, ammount FROM customer c2 JOIN 
  (SELECT C.custID As customerid, MAX(T.TRANSAMOUNT) AS ammount 
  FROM customer C 
  JOIN Account A 
  ON C.CUSTID = A.CUSTOMER 
  JOIN Transaction T 
  ON A.accnumber = T.accNumber  
  WHERE T.transtype = 'w' 
  GROUP BY C.CUSTID)
ON customerid = c2.custID;
--done

--- 6. (17 points) List the customer id, first name and last name of any 
---    customers who have no active accounts.  This includes A) customers who 
---    have accounts, but they only have accounts that are closed and/or frozen, 
---    and also B) customers who do not have any accounts at all.

SELECT c1.CUSTID,c1.FNAME, c1.LNAME FROM customer c1 WHERE c1.CUSTID IN (
  SELECT C.CUSTID FROM CUSTOMER C
  MINUS
  SELECT A.CUSTOMER FROM Account A WHERE A.ACCSTATUS = 'Active');
--done

--- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
---    display the number of accounts opened at that location, even if no 
---    accounts were opened at that location.
--- NOTE: at least one transaction location has no accounts opened at that location

SELECT T.TRANSLOCATION, COUNT(A.ACCNUMBER) 
FROM transaction T 
LEFT OUTER JOIN account A 
ON A.ACCOPENLOCATION = T.TRANSLOCATION 
GROUP BY T.TRANSLOCATION;
--done