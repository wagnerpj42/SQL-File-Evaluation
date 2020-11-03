-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 07

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)

-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
Select CustID, FName, LName, A.AccClosedDate
    From Customer C
    Join Account A On C.CustId = A.Customer
    Where A.AccOpenLocation = 'Central' AND A.AccClosedDate >= '2017-03-01';
-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
Select Distinct AccOpenLocation, AccStatus, Count(*) as NumOpened
    From Account A
    Group By AccOpenLocation, AccStatus
    Order by AccStatus;
-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
Select A.AccNumber, A.AccBalance, A.AccType
    From Account A
    Where A.AccBalance > (SELECT AVG(AccBalance) FROM Account B
    Where A.AccType = B.AccType)
    Order By A.AccNumber ASC;
-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
Select TransLocation, AVG(TransAmount) AS AverageTransAmount
    From Transaction T
    Where T.TransAmount < (Select AVG(Tb.TransAmount) From Transaction Tb)
    Group By TransLocation;
-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
Select CustID, FName, LName
    From Customer C
    Join Account A On C.CustId = A.Customer
    Join Transaction T On A.AccNumber = T.AccNumber
    Where T.TransType = 'w' AND T.TransAmount IN (Select Max(TransAmount) From Transaction Tb Where Tb.TransType = 'w');
-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
Select CustID, FName, LName, A.AccStatus
    From Customer C
    Join Account A On C.CustID = A.Customer
    Where A.AccStatus = 'Closed' OR A.AccStatus = 'Frozen' Or A.AccStatus != 'Active';
-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- --    NOTE: at least one transaction location has no accounts opened at that location
Select Distinct TransLocation, Count(*) as NumOfAountsOpened
    From Transaction T
    Where T.TransLocation In (Select AccOpenLocation From Account A)
    Group by TransLocation
    Order By TransLocation ASC;

