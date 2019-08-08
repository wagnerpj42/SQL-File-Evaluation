/*
 * AbstractTest - abstract class to hold common fixture data for JUnit tests
 * 
 * Created - Paul J. Wagner, 30-Oct-2017
 */
package edu.uwec.cs.wagnerpj.sqltest.junit;

import edu.uwec.cs.wagnerpj.sqltest.general.Query;

public abstract class AbstractTest {
	// data
	protected Query achievementAllQuery;// select all achievements
	protected Query creatureAllQuery;	// select all creatures
	protected Query creatureAllQueryLC;	// select all creatures, in lower case
	protected Query creatureAllQueryBC; // select all creatures, by column
	protected Query creatureAllQueryBC2;// select all creatures, by column, different order
	protected Query creatureOneColQuery;// select one column, all rows
	protected Query creatureOneRowQuery;// select one row with where clause
	protected Query creatureAndQuery;	// select multiple rows with where/and clause
	protected Query creatureZeroQuery;	// select with where to get no creature rows
	protected Query skillAllQuery;		// select all skills
	protected Query aspirationAllQuery;	// select all aspirations
	protected Query jobAllQuery;		// select all jobs
	protected Query nullQuery;			// empty string
	protected Query badQuery;			// select with non-existent column, no from/table, bad syntax
	protected Query badQuery2;			// select with from and table, but bad syntax (non-existing table)
	protected Query garbageQuery;		// random string
	protected Query nestedQuery;		// nested subquery with two selects
	protected Query groupByQuery;		// query with group by
	protected Query havingQuery;		// query with having
	protected Query orderByQuery;		// query with order by
	protected Query orderByDescQuery;	// query with order by descending part
	protected Query orderByDesc2Query;	// query with order by descending full
	protected Query joinOneQuery;		// query with one join
	protected Query joinManyQuery;		// query with multiple joins
	protected Query joinTableSelfQuery;	// query with table joined to itself
	protected Query joinOuterQuery;		// query with outer join
	protected Query joinOuter2Query;	// query with outer join but not word OUTER
	protected Query joinCrossQuery;		// query with cross join
	protected Query joinCross2Query;	// query with cross join, lower case
	protected Query avgQuery;			// query with avg function
	protected Query maxQuery;			// query with max function
	protected Query sumQuery;			// query with sum function	
	protected Query likeQuery;			// query with like keyword
	protected Query multiLineQuery;		// query on multiple lines
	protected Query unionQuery;			// query with union
	
	// methods
	// default constructor - essentially cross-test fixture setup
	protected AbstractTest() {
		achievementAllQuery = new Query("SELECT * FROM Achievement");
		creatureAllQuery    = new Query("SELECT * FROM Creature");
		creatureAllQueryLC  = new Query("select * from creature");
		creatureAllQueryBC  = new Query("SELECT c_id, c_name, c_type, reside_t_id FROM Creature");
		creatureAllQueryBC2 = new Query("SELECT c_name, c_type, c_id, reside_t_id FROM Creature");
		creatureOneColQuery = new Query("SELECT c_id FROM Creature");
		creatureOneRowQuery = new Query("SELECT c_id FROM Creature WHERE c_id = 1");
		creatureAndQuery	= new Query("SELECT c_id FROM Creature WHERE c_id >= 1 AND c_id <= 8");
		creatureZeroQuery	= new Query("SELECT c_id FROM Creature WHERE c_id = 100");
		skillAllQuery       = new Query("SELECT * FROM Skill");
		jobAllQuery			= new Query("SELECT * FROM Job");
		aspirationAllQuery  = new Query("SELECT * FROM Aspiration");
		nullQuery           = new Query("");
		badQuery            = new Query("select something");	
		badQuery2			= new Query("SELECT * FROM CreatureTable");
		garbageQuery		= new Query("xlkjxlkjxlk ;lkj");
		nestedQuery			= new Query("SELECT DISTINCT c_name FROM Creature WHERE c_id IN " +
											"(SELECT c_id FROM Achievement)");
		groupByQuery		= new Query("SELECT c_id, COUNT(DISTINCT s_code) FROM Achievement " +
											"GROUP BY c_id");
		havingQuery			= new Query("SELECT c_id, COUNT(DISTINCT s_code) FROM Achievement " +
											"GROUP BY c_id HAVING c_id >= 2");
		orderByQuery		= new Query("SELECT * FROM Achievement ORDER BY c_id, s_code");
		orderByDescQuery	= new Query("SELECT * FROM Achievement ORDER BY c_id, s_code DESC");
		orderByDesc2Query	= new Query("SELECT * FROM Achievement ORDER BY c_id, s_code DESCENDING");
		joinOneQuery		= new Query("SELECT * FROM Creature C " +
											"JOIN Achievement A ON C.c_id = A.c_id");
		joinManyQuery		= new Query("SELECT c_id FROM Creature, Achievement " + 
											"JOIN Skill ON Skill.s_code = Achievement.s_code");
		joinTableSelfQuery  = new Query("SELECT C1.c_id, C2.c_id FROM Creature C1 " +
											"JOIN Creature C2 ON C1.c_id = C2.c_id");
		joinOuterQuery		= new Query("SELECT C.c_id, COUNT(s_code) FROM Creature C " +
											"LEFT OUTER JOIN Achievement A ON " +
											"C.c_id = A.c_id GROUP BY C.c_id"); 
		joinOuter2Query		= new Query("SELECT C.c_id, COUNT(s_code) FROM Creature C " +
											"LEFT JOIN Achievement A ON " +
											"C.c_id = A.c_id GROUP BY C.c_id");
		joinCrossQuery		= new Query("SELECT * FROM Creature CROSS JOIN Achievement");
		joinCross2Query		= new Query("SELECT * FROM Creature cross join Achievement");
		avgQuery			= new Query("SELECT s_code, AVG(DISTINCT c_id) FROM Achievement " +
											"GROUP BY s_code");
		maxQuery			= new Query("SELECT s_code, MAX(DISTINCT c_id) FROM Achievement " +
											"GROUP BY s_code");
		sumQuery			= new Query("SELECT s_code, SUM(DISTINCT c_id) FROM Achievement " +
											"GROUP BY s_code");
		likeQuery			= new Query("SELECT C.c_id FROM Creature C " +
											"WHERE c_name LIKE '%a%'");
		multiLineQuery		= new Query("SELECT C.c_id, COUNT(s_code) FROM Creature C \n" +
											"LEFT JOIN Achievement A ON \n" +
											"C.c_id = A.c_id GROUP BY C.c_id");
		unionQuery			= new Query("SELECT c_id FROM Creature UNION SELECT c_id FROM Achievement");
	}	// end - constructor/query initialization
	
	// finalize - essentially cross-test fixture teardown
	protected void finalize() throws Throwable {
		achievementAllQuery = null;
		creatureAllQuery    = null;
		creatureAllQueryLC  = null;
		creatureAllQueryBC  = null;
		creatureAllQueryBC2 = null;
		creatureOneColQuery = null;
		creatureOneRowQuery = null;
		creatureAndQuery	= null;
		creatureZeroQuery	= null;
		skillAllQuery       = null;
		jobAllQuery			= null;
		aspirationAllQuery	= null;
		nullQuery           = null;
		badQuery            = null;
		badQuery2			= null;
		garbageQuery		= null;
		nestedQuery			= null;
		groupByQuery		= null;
		havingQuery			= null;
		orderByQuery		= null;
		orderByDescQuery	= null;
		orderByDesc2Query	= null;
		joinOneQuery		= null;
		joinManyQuery		= null;
		joinTableSelfQuery  = null;
		joinOuterQuery		= null;
		joinOuter2Query		= null;
		joinCrossQuery		= null;
		joinCross2Query		= null;
		avgQuery			= null;
		maxQuery			= null;
		sumQuery			= null;
		likeQuery			= null;
		multiLineQuery		= null;
		unionQuery			= null;
		
		super.finalize();
	}	// end - method finalize

}	// end - abstract class AbstractTest
