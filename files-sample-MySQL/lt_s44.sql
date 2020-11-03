-- -- CS 260, Fall 2019, Lab Test
-- -- Name: STUDENT 44

-- -- Table List:
-- -- Customer ( _CustID_ , FName, LName, PIN)
-- -- Account ( _AccNumber_ , AccType, AccBalance, Customer (FK), AccOpenLocation, 
-- --                         AccOpenDate, AccClosedDate, AccStatus)
-- -- Transaction ( _TransID_ , AccNumber (FK), TransAmount, TransDateTime, 
-- --                         TransType, TransLocation)


-- -- 1. (15 points) List the customer id, both name fields, and the account closed 
-- --    date for customers whose accounts opened in the Central location but have 
-- --    been closed on or after March 1st, 2017.
select c.custid, c.fname,c.lname,a.acccloseddate
from customer c
join account  a on c.custid = a.customer
where a.accopenlocation ='Central' and a.accstatus = 'Closed' and a.acccloseddate >= '2017-03-01';


-- -- 2. (16 points) For each account opening location, show how many active, closed,
-- --    and frozen, and accounts there are.  Display the results with the locations
-- --    and account status values in alphabetical order.
-- --        EXAMPLE OUTPUT (not real numbers - just shown to illustrate format)
-- --        Central        Active        10
-- --        Central        Closed        20
-- --        Central        Frozen         1
-- --        <similar for other branches>
select accopenlocation, accstatus, count(*)
from account
group by accopenlocation, accstatus
order by accopenlocation, accstatus asc;


-- -- 3. (18 points) List the accounts (by account number, in numeric order) 
-- --    with an account balance greater than the average account balance 
-- --    for all accounts of the same account type (checking or savings).
select a1.accnumber
from account a1
where a1.accbalance >(
    select avg(a2.accbalance)
    from account a2
    where a1.acctype=a2.acctype
)
order by a1.accnumber asc;


-- -- 4. (18 points) Display the transaction location and the average transaction 
-- --    amount for each transaction location where that average transaction amount
-- --    is less than the average transaction amount for all transactions.  Order the
-- --    results by average transaction amount, largest to smallest.
select t.translocation, AVG(t.transamount)
from transaction t
group by t.translocation
having avg(t.transamount)<(
    select avg(transamount)
    from transaction
)
order by avg(t.transamount) desc;


-- -- 5. (16 points) Find each customer id, first name, and last name, plus the 
-- --    amount for the largest 'w' (withdrawal) transaction related to a 
-- --    savings account which that customer has opened.  Display the results 
-- --    in order by customer id.
select c.custid,c.fname,c.lname,max(t.transamount)
from customer c
join account a on c.custid = a.customer
join transaction t on t.accnumber=a.accnumber
where t.transtype = 'w' and a.acctype='savings'
group by c.custid,c.fname,c.lname
order by c.custid asc;

-- -- 6. (17 points) List the customer id, first name and last name of any 
-- --    customers who have no active accounts.  This includes A) customers who 
-- --    have accounts, but they only have accounts that are closed and/or frozen, 
-- --    and also B) customers who do not have any accounts at all.

select  c.custid,c.fname,c.lname
from customer c
where custid not in(
    select custid
    from customer 
    join account  on custid = customer
    where accstatus = 'Active'
);


-- -- 7. EXTRA CREDIT / ANSWER OPTIONAL (5 points): For each transaction location, 
-- --    display the number of accounts opened at that location, even if no 
-- --    accounts were opened at that location.
-- -- NOTE: at least one transaction location has no accounts opened at that location
select translocation, count(distinct a.accnumber)
from transaction t
left join account a on (t.translocation = a.accopenlocation)
group by translocation;
