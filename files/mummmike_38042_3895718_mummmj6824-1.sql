--- CS 260, Spring 2019, Lab Test
--- Name: Mike Mumm

--- Table List:
--- Customer ( _CustID_ , FName, LName, PIN)
--- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
---                         AccOpenDate, AccClosedDate, AccStatus)
--- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
---                         TransType, TransLocation)


--- 1. (15 points) List the customer id, both name fields, and the account closed 
---    date for customers whose accounts opened in the Central location but have 
---    been closed on or after January 1st, 2018.

SELECT C.custid, C.fname, C.lname, A.acccloseddate FROM Account A
  JOIN Customer C ON (C.custid = A.customer)
  WHERE A.accopenlocation = 'Central' AND A.acccloseddate >= '01-JAN-2018';



--- 2. (16 points) For each account opening location, show how many active, closed,
---    and frozen, and accounts there are.  Display the results with the locations
---    and status values in alphabetical order.
---        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
---        Central        Active        10
---        Central        Closed        20
---        Central        Frozen         1
---        <similar for other branches>
SELECT accopenlocation, accstatus, COUNT(customer) AS StatusCount FROM Account
  GROUP BY accopenlocation, accstatus
  ORDER BY accopenlocation;



--- 3. (18 points) List the accounts (by account number, in numeric order) 
---    with an account balance greater than the average account balance 
---    for all accounts of the same account type (checking or savings).
SELECT accnumber FROM Account
  WHERE accbalance >
            (SELECT AVG(accbalance) FROM Account)
  GROUP BY accnumber
  ORDER BY accnumber;




--- 4. (18 points) Display the transaction location and the average transaction 
---    amount for each transaction location where that average transaction amount
---    is less than the average transaction amount for all transactions.  Order the
---    results by average transaction amount, largest to smallest.
--- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
---    number to display with a limited number of decimal places; 
---    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
SELECT T.translocation, AVG(T.transamount) AS AVGTransaction FROM Transaction T
    GROUP BY T.translocation
    HAVING T.translocation IN 
            (SELECT T1.translocation FROM Transaction T1
            GROUP BY T1.translocation
             HAVING AVG(T.transamount) < 
                          (SELECT AVG(T2.transamount) FROM Transaction T2))
    ORDER BY AVG(T.transamount) DESC;
  



--- 5. (16 points) Find each customer id, first name, and last name, plus the 
---    amount for the largest 'w' (withdrawal) transaction related to a 
---    savings account which that customer has opened.  Display the results 
---    in order by customer id.
SELECT C.custid, C.fname, C.lname, MAX(T.transamount) AS MostSpent FROM Customer C
  JOIN Account A ON (A.customer = C.custid)
  JOIN Transaction T ON (T.accnumber = A.accnumber)
  WHERE A.acctype = 'savings' 
  GROUP BY C.custid, C.fname, C.lname
  ORDER BY C.custid;


--- 6. (17 points) List the customer id, first name and last name of any 
---    customers who have no active accounts.  This includes A) customers who 
---    have accounts, but they only have accounts that are closed and/or frozen, 
---    and also B) customers who do not have any accounts at all.
SELECT C.custid, C.fname, C.lname FROM Customer C
  JOIN Account A ON (A.customer = C.custid)
  WHERE A.accstatus = 'Closed' OR A.accstatus = 'Frozen' OR A.accnumber = NULL;
  



--- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
---    display the number of accounts opened at that location, even if no 
---    accounts were opened at that location.
--- NOTE: at least one transaction location has no accounts opened at that location
SELECT T.translocation, COUNT(T.accnumber) FROM Transaction T
  FULL OUTER JOIN Account A ON (A.accnumber = T.accnumber)
  GROUP BY T.translocation;
  

  
  


