-- -- CS 260, Fall 2019, Lab Test
-- -- Name: Student 14

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
        Select c.CustID, c.FName, c.LName
        from Customer c
        join Account a on(c.CustID = a.Customer)
        where a.AccOpenLocation like 'Central' and (a.AccClosedDate > '01-MAR-2017' or a.AccClosedDate like '01-MAR-2017');


-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
        Select a.AccOpenLocation, a.AccStatus, count(AccStatus)
        from Account a group by a.AccOpenLocation, a.AccStatus
        order by (AccOpenLocation);
        


-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
        Select a.AccNumber
        from account a
        WHERE (a.AccBalance >      
                      (SELECT AVG(a2.AccBalance)
                      From Account a2
                      WHERE(a.AccType = a2.AccType)))
        Order by a.AccNumber;

-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
-- -- NOTE: for cleaner display, you can (but do not need to do so) CAST a real 
-- --    number to display with a limited number of decimal places; 
-- --    e.g. CAST (AVG(value) AS NUMBER(*,2)) 
        select t1.TransLocation, CAST (AVG(t1.TransAmount) AS NUMBER(*,2))
        from Transaction t1 
        where t1.TransAmount < (
                        Select AVG(t2.TransAmount)
                        from Transaction t2) 
        group by t1.TransLocation
        order by AVG(t1.TransAmount) desc;


-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
        Select  c.CustID, c.FName, c.LName, MAX(t.transAmount)
        from Customer c
        join Account a on(c.CustID = a.Customer)
        join Transaction t on (a.AccNumber = t.AccNumber) 
        where t.TransType like 'w' group by c.CustID, c.FName, c.LName
        order by c.CustID;


-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
        Select  c.CustID, c.FName, c.LName
        from Customer c
        left join Account a on(c.CustID = a.Customer)
        where NOT(a.AccStatus like 'Active') or (a.AccNumber IS NULL);

-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location
        Select t.TransLocation, Count(a.AccNumber)
        from Transaction t
        left outer join Account a on (a.AccNumber = t.AccNumber)
        where t.transLocation = a.accopenLocation group by t.TransLocation;

