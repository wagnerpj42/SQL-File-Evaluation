-- -- CS 260, Fall 2019, Lab Test
-- -- Name:Student 31

-- -- Table List:
-- -- Customer ( CustID , FName, LName, PIN)
-- -- Account ( AccNumber , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( TransID , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
select c.CustId, c.FName, c.LName, a.AccClosedDate from Customer c
join Account a on (c.CustID = a.Customer)
where  a.AccClosedDate > '2017-03-01' and a.AccOpenLocation = 'Central';



-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
select a.AccOpenLocation, a.AccStatus, count(a.AccStatus)as NumOfAcc from Account a
where a.customer in (
select a1.Customer from Account a1
where a.AccOpenLocation=a1.AccOpenLocation)
group by a.AccOpenLocation, a.AccStatus
order by a.AccOpenLocation,a.accstatus;



-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
select a.Customer, a.AccBalance from Account a 
where a.AccBalance > (
select avg(a1.AccBalance) from Account a1
where a1.AccType in (
select a2.AccType from account a2
where a1.AccType=a2.AccType));




-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
select t.TransLocation, AVG(t.TransAmount) as avgAmt from Transaction t
where t.TransAmount > (
select avg(t1.TransAmount) from Transaction t1
where t1.TransLocation in (
select t2.TransLocation from Transaction t2
where t1.TransLocation = t2.TransLocation))
group by t.TransLocation
order by avgAmt desc;



-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.,, max(t.TransAmount)as maxAmt
select c.CustID, c.FName, c.LName  from  Customer c
where c.custid in (
select a.Customer from Account a
where a.acctype = 'savings' )
and c.custid in ( 
select c1.CustID , c1.FName, c1.LName   from Customer c1
where c1.CustID in (
select t.TransID from Transaction t
where t.TransType = 'w'))
group by c.CustID, c1.FName, c1.LName;


-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.
select c.CustID, c.FName, c.LName from Customer c
where c.CustID not in 
(select a.AccNumber from Account a
where a.AccStatus = 'Active');




-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location


