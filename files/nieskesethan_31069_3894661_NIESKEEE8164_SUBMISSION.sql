--- CS 260, Spring 2019, Lab Test
--- Name: ETHAN NIESKES

--- Table List:
--- Customer ( _CustID_ , FName, LName, PIN)
--- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
---                         AccOpenDate, AccClosedDate, AccStatus)
--- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
---                         TransType, TransLocation)


--- 1. (15 points) List the customer id, both name fields, and the account closed 
---    date for customers whose accounts opened in the Central location but have 
---    been closed on or after January 1st, 2018.
SELECT CustID, FName, LName, AccClosedDate, AccOpenLocation
FROM Customer
JOIN Account ON Account.customer = Customer.CustID
WHERE AccOpenLocation IN
      (SELECT AccOpenLocation
       FROM Account
        WHERE AccClosedDate IN
          (SELECT AccClosedDate
           FROM Account
           WHERE AccOpenLocation = 'Central'
           AND AccClosedDate >= (TO_DATE('January 01, 2018', 'MONTH/DD/YYYY'))
           AND AccClosedDate != NULL)
          ------Incomplete, Can't get rid of NULL values in table
          )
;

--- 2. (16 points) For each account opening location, show how many active, closed,
---    and frozen, and accounts there are.  Display the results with the locations
---    and status values in alphabetical order.
---        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
---        Central        Active        10
---        Central        Closed        20
---        Central        Frozen         1
---        <similar for other branches>
SELECT  AccOpenLocation, AccStatus
FROM Account
--JOIN Customer ON Account.Customer = Customer.CustID
GROUP BY AccStatus, AccOpenLocation
HAVING COUNT(*) >= 0
ORDER BY AccOpenLocation, AccStatus;


--- 3. (18 points) List the accounts (by account number, in numeric order) 
---    with an account balance greater than the average account balance 
---    for all accounts of the same account type (checking or savings).
SELECT AccNumber
FROM Account
WHERE AccBalance IN
    (SELECT AVG(AccBalance)
     FROM Account 
     WHERE AccBalance IN
        (SELECT AccBalance
         FROM Account A
         WHERE AccType IN
            (SELECT DISTINCT AccType
             FROM Account 
             WHERE AccBalance < A.AccBalance)
          )
      )
;

--- 4. (18 points) Display the transaction location and the average transaction 
---    amount for each transaction location where that average transaction amount
---    is less than the average transaction amount for all transactions.  Order the
---    results by average transaction amount, largest to smallest.
--- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
---    number to display with a limited number of decimal places; 
---    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
SELECT TransLocation, AVG(TransAmount)
FROM Transaction

;


--- 5. (16 points) Find each customer id, first name, and last name, plus the 
---    amount for the largest 'w' (withdrawal) transaction related to a 
---    savings account which that customer has opened.  Display the results 
---    in order by customer id.
SELECT CustID, FName, LName, MAX(TransAmount)
FROM Customer
JOIN Account ON Account.Customer = Customer.CustID
JOIN Transaction ON Account.AccNumber = Transaction.AccNumber
WHERE TransAmount IN
    (SELECT TransAmount
     FROM Transaction
     WHERE TransType = 'w'
     AND AccType = 'savings')     
GROUP BY CustID, FName, LName, TransAmount
ORDER BY CustID;


--- 6. (17 points) List the customer id, first name and last name of any 
---    customers who have no active accounts.  This includes A) customers who 
---    have accounts, but they only have accounts that are closed and/or frozen, 
---    and also B) customers who do not have any accounts at all.
SELECT CustID, FName, LName, AccStatus
FROM Account
JOIN Customer ON Account.Customer = Customer.CustID
    MINUS
  SELECT AccStatus
  FROM Account
  WHERE AccStatus = 'Active'
  ;
    


--- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
---    display the number of accounts opened at that location, even if no 
---    accounts were opened at that location.
--- NOTE: at least one transaction location has no accounts opened at that location


