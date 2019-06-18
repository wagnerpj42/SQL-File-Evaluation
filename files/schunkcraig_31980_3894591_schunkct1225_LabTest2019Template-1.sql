--- CS 260, Spring 2019, Lab Test
--- Name: Craig Schunk

--- Table List:
--- Customer ( _CustID_ , FName, LName, PIN)
--- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
---                         AccOpenDate, AccClosedDate, AccStatus)
--- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
---                         TransType, TransLocation)


--- 1. (15 points) List the customer id, both name fields, and the account closed 
---    date for customers whose accounts opened in the Central location but have 
---    been closed on or after January 1st, 2018.
SELECT c.custid, c.fname, c.lname, TO_CHAR(a.acccloseddate, 'Month dd, yyyy') AS CloseDate
  FROM Account a
  INNER JOIN Customer c ON (a.customer = c.custid)
  WHERE a.accopenlocation = 'Central' AND a.accstatus = 'Closed' AND a.acccloseddate >= TO_DATE('January 01, 2018', 'Month dd, yyyy');


--- 2. (16 points) For each account opening location, show how many active, closed,
---    and frozen, and accounts there are.  Display the results with the locations
---    and status values in alphabetical order.
---        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
---        Central        Active        10
---        Central        Closed        20
---        Central        Frozen         1
---        <similar for other branches>
SELECT a.accopenlocation, a.accstatus, COUNT(a.accnumber) AS NumberOfAccounts --much faster than COUNT(*) (accnumber should be PK)
  FROM Account a
  GROUP BY a.accopenlocation, a.accstatus
  ORDER BY a.accopenlocation, a.accstatus;


--- 3. (18 points) List the accounts (by account number, in numeric order) 
---    with an account balance greater than the average account balance 
---    for all accounts of the same account type (checking or savings).
SELECT a2.accnumber
  FROM Account a2
  WHERE a2.accbalance >
    (SELECT AVG(a.accbalance)
    FROM Account a
    GROUP BY a.acctype
    HAVING a.acctype = a2.acctype)
  ORDER BY a2.accnumber;


--- 4. (18 points) Display the transaction location and the average transaction 
---    amount for each transaction location where that average transaction amount
---    is less than the average transaction amount for all transactions.  Order the
---    results by average transaction amount, largest to smallest.
--- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
---    number to display with a limited number of decimal places; 
---    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
SELECT t2.translocation, CAST(AVG(t2.transamount) AS NUMBER(*, 2)) AS AverageTransactionAmount
  FROM Transaction t2
  GROUP BY t2.translocation
  HAVING AVG(t2.transamount) < 
    (SELECT AVG(t.transamount)
    FROM Transaction t)
  ORDER BY AVG(t2.transamount) DESC; --assuming you mean the average transaction amount for all transactions at all locations
  


--- 5. (16 points) Find each customer id, first name, and last name, plus the 
---    amount for the largest 'w' (withdrawal) transaction related to a 
---    savings account which that customer has opened.  Display the results 
---    in order by customer id.
SELECT a.customer, c.fname, c.lname, MAX(t.transamount) AS LargestWithdrawl
  FROM Transaction t
  INNER JOIN Account a ON (t.accnumber = a.accnumber)
  INNER JOIN Customer c ON (c.custid = a.customer)
  WHERE t.transtype = 'w' AND a.acctype = 'savings'
  GROUP BY a.customer, c.fname, c.lname
  ORDER BY a.customer;

--- 6. (17 points) List the customer id, first name and last name of any 
---    customers who have no active accounts.  This includes A) customers who 
---    have accounts, but they only have accounts that are closed and/or frozen, 
---    and also B) customers who do not have any accounts at all.
SELECT c2.custid, c2.fname, c2.lname
  FROM Customer c2
  WHERE c2.custid IN (
    (SELECT c.custid
      FROM Customer c)
    MINUS
      (SELECT a.customer
        FROM Account a
        WHERE a.accstatus = 'Active'));


--- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
---    display the number of accounts opened at that location, even if no 
---    accounts were opened at that location.
--- NOTE: at least one transaction location has no accounts opened at that location
SELECT t.translocation, COUNT(DISTINCT a.accnumber) AS AccountsOpened
  FROM Transaction t
  LEFT JOIN Account a ON (a.accopenlocation = t.translocation)
  GROUP BY t.translocation;

