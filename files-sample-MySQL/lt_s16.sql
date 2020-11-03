-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 16

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
        
        SELECT c.CustID, c.FName, c.LName, a.AccClosedDate  
        FROM Customer c
        JOIN Account a ON (c.CustID = a.Customer)
        WHERE a.AccOpenLocation = 'Central' AND
        a.AccClosedDate >= '2017-03-01'
        ORDER BY c.CustID;


-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>

    SELECT a.AccOpenLocation AS "Account Location", a.AccStatus AS "Account Status", COUNT(a2.AccNumber) AS "Number of Accounts"
    FROM Account a
    LEFT OUTER JOIN Account a2 ON (a.AccNumber = a2.AccNumber)
    GROUP BY a.AccOpenLocation, a.AccStatus
    ORDER BY a.AccOpenLocation ASC;
    


-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
        
    SELECT * -- Listed the whole account for each account, ordered by AccNumber
    FROM Account a
    WHERE a.AccBalance > (
        SELECT AVG(a2.AccBalance)
        FROM Account a2
        WHERE a.AccType = a2.AccType
    )
    ORDER BY a.AccNumber ASC;

-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.

        SELECT t.TransLocation AS "Transaction Location", AVG(t.TransAmount) AS "Average Amount"
        FROM Transaction t
        WHERE (
            SELECT AVG(t2.TransAmount)
            FROM Transaction t2
            WHERE t.TransID = t2.TransID
        ) < (
            SELECT AVG(t3.TransAmount)
            FROM Transaction t3
        )
        GROUP BY t.TransLocation
        ORDER BY "Average Amount" DESC;

-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
        
        SELECT c.CustID, c.FName AS "First Name", c.LName AS "Last Name", MAX(t.TransAmount) AS "Largest Withdrawl"
        FROM Account a
        JOIN Customer c ON (c.CustID = a.Customer)
        JOIN Transaction t ON (t.AccNumber = a.AccNumber)
        WHERE t.TransType = 'w'
        GROUP BY c.CustID, c.FName, c.LName
        ORDER BY c.CustID;


-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.

    SELECT c.CustID AS "Customer ID", c.FName AS "First Name", c.LName AS "Last Name"
    FROM Customer c
    JOIN Account a ON (a.Customer = c.CustID)
    WHERE a.Customer NOT IN (
        SELECT a2.Customer
        FROM Account a2
        WHERE a2.AccStatus = 'Active' 
    );

-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location

    SELECT t.TransLocation AS "Transaction Location", COUNT(a.AccOpenLocation) AS "Number Opened"
    FROM Transaction t
    LEFT OUTER JOIN Account a ON (t.TransLocation = a.AccOpenLocation)
    GROUP BY t.TransLocation
    ORDER BY t.TransLocation;

