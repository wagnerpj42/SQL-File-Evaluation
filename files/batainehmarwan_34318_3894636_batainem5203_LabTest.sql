--- CS 260, Spring 2019, Lab Test
--- Name: Marwan Bataineh

--- Table List:
--- Customer ( _CustID_ , FName, LName, PIN)
--- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
---                         AccOpenDate, AccClosedDate, AccStatus)
--- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
---                         TransType, TransLocation)


--- 1. (15 points) List the customer id, both name fields, and the account closed 
---    date for customers whose accounts opened in the Central location but have 
---    been closed on or after January 1st, 2018.
SELECT C.CustID, C.FName, C.LName, A.AccClosedDate
FROM Customer C
JOIN Account A
ON(C.CustID = A.Customer)
WHERE A.AccOpenLocation = 'Central' AND A.AccClosedDate >= '01-JAN-2018';

--- 2. (16 points) For each account opening location, show how many active, closed,
---    and frozen, and accounts there are.  Display the results with the locations
---    and status values in alphabetical order.
---        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
---        Central        Active        10
---        Central        Closed        20
---        Central        Frozen         1
---        <similar for other branches>
SELECT DISTINCT A.AccOpenLocation, COUNT(A1.AccStatus) AS active, COUNT(A2.AccStatus) AS closed, COUNT(A3.AccStatus) AS frozen, COUNT(A.AccNumber)
FROM Account A
JOIN Account A1 ON A1.AccNumber < A.AccNumber
JOIN Account A2 ON A2.AccNumber < A.AccNumber
JOIN Account A3 ON A3.AccNumber < A.AccNumber
WHERE A1.AccStatus = 'Active' AND A2.AccStatus = 'Closed' AND A3.AccStatus = 'Frozen'
GROUP BY A.AccOpenLocation;

--- 3. (18 points) List the accounts (by account number, in numeric order) 
---    with an account balance greater than the average account balance 
---    for all accounts of the same account type (checking or savings).
SELECT DISTINCT A1.AccNumber AS checkings, A2.AccNumber AS savings
FROM Account A1
JOIN Account A2 ON A1.AccNumber < A2.AccNumber
WHERE A1.AccBalance >
    (SELECT AVG(A.AccBalance)
    FROM Account A)
    AND A2.AccBalancy >
    (SELECT AVG(A.AccBalance)
    FROM Account A)
ORDER BY AccNumber;

--- 4. (18 points) Display the transaction location and the average transaction 
---    amount for each transaction location where that average transaction amount
---    is less than the average transaction amount for all transactions.  Order the
---    results by average transaction amount, largest to smallest.
--- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
---    number to display with a limited number of decimal places; 
---    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
SELECT T.TransLocation, AVG(T.TransAmount)
FROM Transaction T
WHERE AVG(T.TransAmount) <
    (SELECT AVG(T2.TransAmount)
    FROM Transaction T2)
GROUP BY TransLocation;


--- 5. (16 points) Find each customer id, first name, and last name, plus the 
---    amount for the largest 'w' (withdrawal) transaction related to a 
---    savings account which that customer has opened.  Display the results 
---    in order by customer id.
SELECT C.CustID, C.FName, C.LName, MAX(T.TransAmount)
FROM Customer C
JOIN Account A ON C.CustID = A.Customer
JOIN Transaction T ON A.AccNumber = T.AccNumber
WHERE T.TransType = 'w' AND A.AccType = 'savings'
GROUP BY C.CustID, C.FName, C.LName;


--- 6. (17 points) List the customer id, first name and last name of any 
---    customers who have no active accounts.  This includes A) customers who 
---    have accounts, but they only have accounts that are closed and/or frozen, 
---    and also B) customers who do not have any accounts at all.
SELECT DISTINCT C.CustID, C.FName, C.LName
FROM Customer C
WHERE C.CustID IN
    (SELECT A.Customer FROM Account A
    WHERE AccStatus != 'Active');


--- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
---    display the number of accounts opened at that location, even if no 
---    accounts were opened at that location.
--- NOTE: at least one transaction location has no accounts opened at that location


