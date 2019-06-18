--- CS 260, Spring 2019, Lab Test
--- Name: Feifan,Han

--- Table List:
--- Customer ( _CustID_ , FName, LName, PIN)
--- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
---                         AccOpenDate, AccClosedDate, AccStatus)
--- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
---                         TransType, TransLocation)


--- 1. (15 points) List the customer id, both name fields, and the account closed 
---    date for customers whose accounts opened in the Central location but have 
---    been closed on or after January 1st, 2018.
SELECT C.CUSTID,C.FNAME,C.LNAME,A.ACCCLOSEDDATE FROM CUSTOMER C
JOIN ACCOUNT A
ON C.CUSTID=A.CUSTOMER
WHERE A.ACCOPENLOCATION='Central' AND A.ACCOPENDATE<'01-JAN-2018' AND A.ACCSTATUS='Closed';


--- 2. (16 points) For each account opening location, show how many active, closed,
---    and frozen, and accounts there are.  Display the results with the locations
---    and status values in alphabetical order.
---        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
---        Central        Active        10
---        Central        Closed        20
---        Central        Frozen         1
---        <similar for other branches>
SELECT A.ACCOPENLOCATION,A.ACCSTATUS,COUNT(A.ACCSTATUS) FROM ACCOUNT A 
GROUP BY A.ACCOPENLOCATION, A.ACCSTATUS
ORDER BY A.ACCOPENLOCATION;


--- 3. (18 points) List the accounts (by account number, in numeric order) 
---    with an account balance greater than the average account balance 
---    for all accounts of the same account type (checking or savings).
SELECT ACCNUMBER,ACCTYPE FROM ACCOUNT 
GROUP BY ACCTYPE,ACCBALANCE,ACCNUMBER
HAVING ACCBALANCE>(SELECT AVG(ACCBALANCE) FROM ACCOUNT)
ORDER BY ACCNUMBER;


--- 4. (18 points) Display the transaction location and the average transaction 
---    amount for each transaction location where that average transaction amount
---    is less than the average transaction amount for all transactions.  Order the
---    results by average transaction amount, largest to smallest.
--- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
---    number to display with a limited number of decimal places; 
---    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
SELECT TRANSLOCATION,CAST(AVG(TRANSAMOUNT) AS NUMBER(*,2)) AS AVERAGE FROM TRANSACTION
GROUP BY TRANSLOCATION
HAVING AVG(TRANSAMOUNT)<(SELECT AVG(TRANSAMOUNT) FROM TRANSACTION)
ORDER BY AVERAGE DESC;



--- 5. (16 points) Find each customer id, first name, and last name, plus the 
---    amount for the largest 'w' (withdrawal) transaction related to a 
---    savings account which that customer has opened.  Display the results 
---    in order by customer id.
SELECT C.CUSTID,C.FNAME,C.LNAME,MAX(T.TRANSAMOUNT) AS MAXTRANS FROM CUSTOMER C
JOIN ACCOUNT A
ON C.CUSTID=A.CUSTOMER
JOIN TRANSACTION T
ON T.ACCNUMBER=A.ACCNUMBER
WHERE A.ACCTYPE='savings' AND T.TRANSTYPE='w' 
GROUP BY C.CUSTID, C.FNAME, C.LNAME
ORDER BY C.CUSTID;


--- 6. (17 points) List the customer id, first name and last name of any 
---    customers who have no active accounts.  This includes A) customers who 
---    have accounts, but they only have accounts that are closed and/or frozen, 
---    and also B) customers who do not have any accounts at all.

SELECT DISTINCT CUSTID,FNAME,LNAME FROM CUSTOMER
MINUS
(SELECT DISTINCT C1.CUSTID,C1.FNAME,C1.LNAME FROM CUSTOMER C1
JOIN ACCOUNT A1
ON C1.CUSTID=A1.CUSTOMER
WHERE A1.ACCSTATUS='Active');




--- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
---    display the number of accounts opened at that location, even if no 
---    accounts were opened at that location.
--- NOTE: at least one transaction location has no accounts opened at that location
SELECT T.TRANSLOCATION,COUNT(A.ACCNUMBER) AS NUMBERACC FROM ACCOUNT A
RIGHT OUTER JOIN TRANSACTION T
ON A.ACCOPENLOCATION=T.TRANSLOCATION
GROUP BY T.TRANSLOCATION
ORDER BY T.TRANSLOCATION;


