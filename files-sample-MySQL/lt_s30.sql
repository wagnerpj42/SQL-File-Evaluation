-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 30 (added by instructor)

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
SELECT CustID,FName, LName
FROM Customer
WHERE AccNumber IN(
    SELECT AccNumber
    FROM Account
    WHERE AccClosedDate IN
    (SELECT AccClosedDate
    FROM Account
    WHERE AccClosedDate > '2017-03-01'));


-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
SELECT AccOpenLocation, AccStatus,COUNT(AccStatus)
FROM Account
GROUP BY AccOpenLocation
HAVING (COUNT(AccStatus)<0);



-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
SELECT AccNumber, AccBalance, AccType
FROM Account A1
WHERE AccBalance > 
    (SELECT AVG (A2.AccBalance)
    From Account A2
    WHERE A1.AccType = A2.AccType
    GROUP BY AccType);


-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
SELECT TransLocation, TransAmount
FROM Transaction T1
WHERE AVG(TransAmount) <
    (SELECT AVG(TransAmount)
    WHERE T1.TransLocation = TransLocation;


-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
SELECT CustID, FName, LName
FROM Customer C
WHERE TransAmount=
    (SELECT MAX TransAmount
    FROM Transaction C2
    WHERE c.custid = C2.custid);


-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
SELECT CustID, FName, LName
FROM Customer
WHERE AccNumber IN
    (SELECT AccNumber
    FROM Account
    WHERE AccStatus in
    (SELECT AccStatus
    FROM Account    
    WHERE AccStatus = 'closed' or AccStatus = 'frozen'));


-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location
SELECT TransLocation
FROM Transaction
WHERE AccNumber IN
    (SELECT AccNumber
    FROM Account
    WHERE AccOpenLocation =
        (SELECT COUNT(AccOpenLocation)
        FROM Account
        GROUP BY Account
        HAVING COUNT(AccOpenLocation)));
        

