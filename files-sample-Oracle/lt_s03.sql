-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 03

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
    
    SELECT CR.CUSTID, CR.FNAME, CR.LNAME ,AV.ACCCLOSEDDATE FROM CUSTOMER CR
    JOIN ACCOUNT AV ON (AV.CUSTOMER = CUSTID AND AV.ACCOPENLOCATION LIKE '%Central%')
    WHERE (AV.ACCCLOSEDDATE >= '1-MAR-17');
    
-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>

    SELECT ACCOPENLOCATION, ACCSTATUS, COUNT(*) as AmountofAccounts FROM ACCOUNT
    GROUP BY ACCOPENLOCATION, ACCSTATUS
    ORDER BY ACCOPENLOCATION, ACCSTATUS;


-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
        SELECT ACCNUMBER, ACCTYPE FROM ACCOUNT B
        WHERE (ACCTYPE LIKE '%savings%' AND B.ACCBALANCE >
        (SELECT AVG(c.ACCBALANCE) FROM ACCOUNT c GROUP BY B.ACCBALANCE) OR ACCTYPE LIKE '%checking%' AND B.ACCBALANCE >
        (SELECT AVG(c.ACCBALANCE) FROM ACCOUNT c GROUP BY B.ACCBALANCE))
        ORDER BY ACCNUMBER;

-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
-- -- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
-- --    number to display with a limited number of decimal places; 
-- --    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
        
        SELECT TRANSLOCATION, CAST (AVG(TRANSAMOUNT) AS NUMBER(*,2)) as AverageTransaction  FROM TRANSACTION A
        WHERE (A.TRANSAMOUNT < (SELECT AVG(b.TRANSAMOUNT) FROM TRANSACTION b))
        GROUP BY TRANSLOCATION, TRANSAMOUNT
        ORDER BY TRANSAMOUNT DESC;
        
        

-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
    
        SELECT CT.CUSTID, CT.FNAME, CT.LNAME,AZ.ACCTYPE, TZ.TRANSTYPE ,MAX(TZ.TRANSAMOUNT) as TransactionAmount FROM CUSTOMER CT
        JOIN ACCOUNT AZ on (CT.CUSTID = AZ.CUSTOMER)
        JOIN TRANSACTION TZ on (TZ.ACCNUMBER = AZ.ACCNUMBER )
        WHERE(TRANSTYPE LIKE '%w%' AND AZ.ACCTYPE LIKE '%savings%')
        GROUP BY CT.CUSTID, CT.FNAME, CT.LNAME,AZ.ACCTYPE,TZ.TRANSTYPE, TZ.TRANSAMOUNT
        ORDER BY CT.CUSTID;
        

-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.

        SELECT CT.CUSTID, CT.FNAME, CT.LNAME, AZ.ACCSTATUS FROM CUSTOMER CT
        LEFT JOIN ACCOUNT AZ on (CT.CUSTID = AZ.CUSTOMER)
        WHERE(AZ.ACCSTATUS LIKE '%Closed%' or AZ.ACCSTATUS is null or AZ.ACCSTATUS LIKE '%frozen%' or CUSTID is null);

-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location

    SELECT TA.ACCOPENLOCATION, TN.TRANSLOCATION, COUNT(TRANSLOCATION) as AmountOfAccountsOpened FROM ACCOUNT TA
    LEFT JOIN TRANSACTION TN ON (TN.ACCNUMBER = TA.ACCNUMBER)
    WHERE(TN.TRANSLOCATION = TA.ACCOPENLOCATION OR TRANSLOCATION is null)
    GROUP BY TA.ACCOPENLOCATION, TN.TRANSLOCATION
    ORDER BY COUNT(TRANSLOCATION);








