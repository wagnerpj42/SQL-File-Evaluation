--- CS 260, Spring 2019, Lab Test
--- Name: Sullivan R. Prellwitz

--- Table List:
--- Customer ( _CustID_ , FName, LName, PIN)
--- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
---                         AccOpenDate, AccClosedDate, AccStatus)
--- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
---                         TransType, TransLocation)


--- 1. (15 points) List the customer id, both name fields, and the account closed 
---    date for customers whose accounts opened in the Central location but have 
---    been closed on or after January 1st, 2018.
SELECT C.CUSTID, C.FNAME AS FIRST_NAME, C.LNAME AS LAST_NAME, A.ACCCLOSEDDATE AS ACCOUNT_CLOSE_DATE
FROM CUSTOMER C
JOIN ACCOUNT A ON C.CUSTID = A.CUSTOMER
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
SELECT A.ACCOPENLOCATION AS OPEN_LOCATION, A.ACCSTATUS AS ACCOUNT_STATUS, COUNT(ACCSTATUS)
FROM ACCOUNT A
GROUP BY A.ACCOPENLOCATION, A.ACCSTATUS
ORDER BY A.ACCOPENLOCATION ASC;

--- 3. (18 points) List the accounts (by account number, in numeric order) 
---    with an account balance greater than the average account balance 
---    for all accounts of the same account type (checking or savings).
SELECT A.ACCNUMBER
FROM ACCOUNT A
WHERE A.ACCBALANCE >
      (SELECT AVG(A2.ACCBALANCE)
        FROM ACCOUNT A2
        WHERE A2.ACCTYPE = A.ACCTYPE)
ORDER BY A.ACCNUMBER ASC;

--- 4. (18 points) Display the transaction location and the average transaction 
---    amount for each transaction location where that average transaction amount
---    is less than the average transaction amount for all transactions.  Order the
---    results by average transaction amount, largest to smallest.
--- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
---    number to display with a limited number of decimal places; 
---    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
SELECT T.TRANSLOCATION, CAST(AVG(T.TRANSAMOUNT) AS NUMBER(*,2)) AS AVERAGE_TRANSACTION_AMOUNT
FROM TRANSACTION T
HAVING AVG(T.TRANSAMOUNT) <
      (SELECT AVG(T2.TRANSAMOUNT)
        FROM TRANSACTION T2)
GROUP BY T.TRANSLOCATION
ORDER BY AVG(T.TRANSAMOUNT) DESC;

--- 5. (16 points) Find each customer id, first name, and last name, plus the 
---    amount for the largest 'w' (withdrawal) transaction related to a 
---    savings account which that customer has opened.  Display the results 
---    in order by customer id.
SELECT C.CUSTID, C.FNAME, C.LNAME, MAX(T.TRANSAMOUNT) AS LARGEST_WITHDRAWAL
FROM CUSTOMER C
JOIN ACCOUNT A ON A.CUSTOMER = C.CUSTID
JOIN TRANSACTION T ON A.ACCNUMBER = T.ACCNUMBER
WHERE A.ACCTYPE = 'savings'
AND T.TRANSTYPE = 'w'
GROUP BY C.CUSTID, C.FNAME, C.LNAME
ORDER BY C.CUSTID ASC;

--- 6. (17 points) List the customer id, first name and last name of any 
---    customers who have no active accounts.  This includes A) customers who 
---    have accounts, but they only have accounts that are closed and/or frozen, 
---    and also B) customers who do not have any accounts at all.
SELECT C.CUSTID, C.FNAME, C.LNAME
FROM CUSTOMER C
RIGHT OUTER JOIN ACCOUNT A on C.CUSTID = A.CUSTOMER
WHERE C.CUSTID IN
      (SELECT C1.CUSTID
        FROM CUSTOMER C1
        MINUS
        SELECT A1.CUSTOMER
        FROM ACCOUNT A1
        WHERE A1.ACCSTATUS = 'Active')
OR A.CUSTOMER IS NULL;

--- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
---    display the number of accounts opened at that location, even if no 
---    accounts were opened at that location.
--- NOTE: at least one transaction location has no accounts opened at that location
SELECT COUNT(A.CUSTOMER), T.TRANSLOCATION
FROM ACCOUNT A
RIGHT OUTER JOIN TRANSACTION T ON A.ACCNUMBER = T.ACCNUMBER
WHERE A.ACCOPENLOCATION = T.TRANSLOCATION
GROUP BY T.TRANSLOCATION;

