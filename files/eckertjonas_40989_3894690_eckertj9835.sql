--- CS 260, Spring 2019, Lab Test
--- Name: Jonas Eckert

--- Table List:
--- Customer ( _CustID_ , FName, LName, PIN)
--- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
---                         AccOpenDate, AccClosedDate, AccStatus)
--- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
---                         TransType, TransLocation)


--- 1. (15 points) List the customer id, both name fields, and the account closed 
---    date for customers whose accounts opened in the Central location but have 
---    been closed on or after January 1st, 2018.
SELECT C.CUSTID, C.FNAME, C.LNAME, A.ACCCLOSEDDATE
FROM Customer C
JOIN Account A
ON (C.CUSTID = A.CUSTOMER)
WHERE A.ACCOPENLOCATION = 'Central'
AND A.ACCCLOSEDDATE >= '01-JAN-2018';




--- 2. (16 points) For each account opening location, show how many active, closed,
---    and frozen, and accounts there are.  Display the results with the locations
---    and status values in alphabetical order.
---        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
---        Central        Active        10
---        Central        Closed        20
---        Central        Frozen         1
---        <similar for other branches>
SELECT A1.ACCOPENLOCATION, A1.ACCSTATUS, COUNT(A1.ACCSTATUS) 
FROM Account A1
INNER JOIN Account A2
ON (A1.ACCSTATUS = A2.ACCSTATUS) 
GROUP BY A1.ACCOPENLOCATION, A1.ACCSTATUS
ORDER BY ACCOPENLOCATION, ACCSTATUS;




--- 3. (18 points) List the accounts (by account number, in numeric order) 
---    with an account balance greater than the average account balance 
---    for all accounts of the same account type (checking or savings).
SELECT A1.ACCNUMBER
FROM Account A1
WHERE A1.ACCBALANCE > 
  (SELECT AVG(ACCBALANCE)
  FROM Account A2
  WHERE A1.ACCTYPE = A2.ACCTYPE)
  ORDER BY A1.ACCNUMBER; 



--- 4. (18 points) Display the transaction location and the average transaction 
---    amount for each transaction location where that average transaction amount
---    is less than the average transaction amount for all transactions.  Order the
---    results by average transaction amount, largest to smallest.
--- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
---    number to display with a limited number of decimal places; 
---    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
SELECT TRANSLOCATION, CAST (AVG(TRANSAMOUNT) AS NUMBER(*,2))
FROM Transaction 
WHERE EXISTS
  (SELECT DISTINCT T1.TRANSLOCATION
  FROM Transaction T1
  JOIN Transaction T2
  ON T1.TRANSLOCATION = T2.TRANSLOCATION
  WHERE T1.TRANSAMOUNT <
    (SELECT AVG(TRANSAMOUNT)
    FROM Transaction))
GROUP BY TRANSLOCATION;





--- 5. (16 points) Find each customer id, first name, and last name, plus the 
---    amount for the largest 'w' (withdrawal) transaction related to a 
---    savings account which that customer has opened.  Display the results 
---    in order by customer id.
SELECT C.CUSTID, C.FNAME, C.LNAME, MAX(T.TRANSAMOUNT)
FROM Customer C
JOIN Account A
ON (A.CUSTOMER = C.CUSTID) 
GROUP BY  C.CUSTID, C.FNAME, C.LNAME 
  MINUS
SELECT T.TRANSTYPE
FROM Transaction T
JOIN Account A2
ON (T.ACCNUMBER = A2.ACCNUMBER)
WHERE T.TRANSTYPE != 'w'
AND ACCTYPE != 'savings';



--- 6. (17 points) List the customer id, first name and last name of any 
---    customers who have no active accounts.  This includes A) customers who 
---    have accounts, but they only have accounts that are closed and/or frozen, 
---    and also B) customers who do not have any accounts at all.
SELECT CUSTID, FNAME, LNAME
FROM Customer C
RIGHT OUTER JOIN Account A
ON (C.CUSTID = A.CUSTOMER)
WHERE A.ACCSTATUS != 'Active';

 





--- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
---    display the number of accounts opened at that location, even if no 
---    accounts were opened at that location.
--- NOTE: at least one transaction location has no accounts opened at that location


