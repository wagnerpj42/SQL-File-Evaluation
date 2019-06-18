--- CS 260, Spring 2019, Lab Test
--- Name: Junfeng Dai

--- Table List:
--- Customer ( _CustID_ , FName, LName, PIN)
--- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
---                         AccOpenDate, AccClosedDate, AccStatus)
--- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
---                         TransType, TransLocation)


--- 1. (15 points) List the customer id, both name fields, and the account closed 
---    date for customers whose accounts opened in the Central location but have 
---    been closed on or after January 1st, 2018.
SELECT C.CustID,C.FName,C.LName,A.ACCCLOSEDDATE
FROM CUSTOMER C
JOIN ACCOUNT A
ON C.CustID=A.Customer
WHERE A.ACCOPENLOCATION='Central' AND TO_CHAR(A.ACCCLOSEDDATE,'YYYY')>=2018;


--- 2. (16 points) For each account opening location, show how many active, closed,
---    and frozen, and accounts there are.  Display the results with the locations
---    and status values in alphabetical order.
---        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
---        Central        Active        10
---        Central        Closed        20
---        Central        Frozen         1
---        <similar for other branches>
SELECT A.ACCOPENLOCATION,A.ACCSTATUS,COUNT(*)
FROM ACCOUNT A
GROUP BY A.ACCOPENLOCATION,A.ACCSTATUS
ORDER BY A.ACCOPENLOCATION,A.ACCSTATUS;


--- 3. (18 points) List the accounts (by account number, in numeric order) 
---    with an account balance greater than the average account balance 
---    for all accounts of the same account type (checking or savings).
SELECT A.ACCNUMBER
FROM ACCOUNT A
WHERE A.ACCBALANCE>(SELECT AVG(A1.ACCBALANCE)
                    FROM ACCOUNT A1
                    WHERE A.ACCTYPE=A1.ACCTYPE)
ORDER BY A.CUSTOMER;


--- 4. (18 points) Display the transaction location and the average transaction 
---    amount for each transaction location where that average transaction amount
---    is less than the average transaction amount for all transactions.  Order the
---    results by average transaction amount, largest to smallest.
--- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
---    number to display with a limited number of decimal places; 
---    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
SELECT T.TRANSLOCATION,CAST (AVG(T.ACCNUMBER) AS NUMBER(*,2))
FROM TRANSACTION T
GROUP BY T.TRANSLOCATION
HAVING AVG(T.TRANSAMOUNT)<(SELECT AVG(T1.TRANSAMOUNT)
                           FROM TRANSACTION T1)
ORDER BY AVG(T.ACCNUMBER)DESC;

--- 5. (16 points) Find each customer id, first name, and last name, plus the 
---    amount for the largest 'w' (withdrawal) transaction related to a 
---    savings account which that customer has opened.  Display the results 
---    in order by customer id.
SELECT C.CUSTID,C.FNAME,C.LNAME,MAX(T.TRANSAMOUNT)
FROM CUSTOMER C
JOIN ACCOUNT A
ON C.CUSTID=A.CUSTOMER
JOIN TRANSACTION T
ON T.ACCNUMBER=A.ACCNUMBER
WHERE T.TRANSTYPE='w' AND A.ACCTYPE='savings' AND A.ACCSTATUS='Active'
GROUP BY C.CUSTID,C.FNAME,C.LNAME
ORDER BY C.CUSTID;


--- 6. (17 points) List the customer id, first name and last name of any 
---    customers who have no active accounts.  This includes A) customers who 
---    have accounts, but they only have accounts that are closed and/or frozen, 
---    and also B) customers who do not have any accounts at all.
SELECT C.CUSTID,C.FNAME,C.LNAME
FROM CUSTOMER C
WHERE C.CUSTID NOT IN(SELECT C.CUSTID
                      FROM CUSTOMER C1
                      JOIN ACCOUNT A
                      ON A.CUSTOMER=C1.CUSTID
                      MINUS
                      SELECT C1.CUSTID
                      FROM CUSTOMER C1
                      JOIN ACCOUNT A
                      ON A.CUSTOMER=C1.CUSTID
                      WHERE A.ACCSTATUS='Closed' OR A.ACCSTATUS='Frozen');


--- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
---    display the number of accounts opened at that location, even if no 
---    accounts were opened at that location.
--- NOTE: at least one transaction location has no accounts opened at that location
SELECT T.TRANSLOCATION,COUNT(*)
FROM TRANSACTION T
LEFT OUTER JOIN ACCOUNT A
ON A.ACCOPENLOCATION=T.TRANSLOCATION
GROUP BY T.TRANSLOCATION;
