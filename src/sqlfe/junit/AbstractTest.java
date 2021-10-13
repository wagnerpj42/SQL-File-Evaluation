/*
 * AbstractTest - abstract class to hold common fixture data for JUnit tests
 * 
 * Created - Paul J. Wagner, 30-Oct-2017
 */
package sqlfe.junit;

import sqlfe.general.IDAO;
import sqlfe.general.MySQL5xDataAccessObject;
import sqlfe.general.MySQL80DataAccessObject;
import sqlfe.general.OracleDataAccessObject;
import sqlfe.general.Query;

@SuppressWarnings("unused")
public abstract class AbstractTest {
	// data
	protected IDAO  testDAO;			// DAO for testing
	
	protected Query achievementAllQuery;// select all achievements
	protected Query creatureAllQuery;	// select all creatures
	protected Query creatureAllQueryLC;	// select all creatures, in lower case
	protected Query creatureAllQueryBC; // select all creatures, by column
	protected Query creatureAllQueryBC2;// select all creatures, by column, different order
	protected Query creatureOneColQuery;// select one column, all rows
	protected Query creatureOneRowQuery;// select one row with where clause
	protected Query creatureAndQuery;	// select multiple rows with where/and clause
	protected Query creatureOrQuery;	// select multiple rows with where/or clause	
	protected Query creatureNotQuery;	// select multiple rows with where/not clause
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
	protected Query orderByDesc3Query;	// query with two order by desc/descending
	protected Query joinOneQuery;		// query with one join
	protected Query joinManyQuery;		// query with multiple joins
	protected Query joinTableSelfQuery;	// query with table joined to itself
	protected Query joinOuterQuery;		// query with outer join
	protected Query joinOuter2Query;	// query with outer join but not word OUTER
	protected Query joinCrossQuery;		// query with cross join
	protected Query joinCross2Query;	// query with cross join, lower case
	protected Query avgQuery;			// query with avg function
	protected Query maxQuery;			// query with max function
	protected Query minQuery;			// query with min function
	protected Query sumQuery;			// query with sum function	
	protected Query likeQuery;			// query with like keyword
	protected Query multiLineQuery;		// query on multiple lines
	protected Query unionQuery;			// query with union
	protected Query intersectQuery;		// query with intersect
	protected Query intersect2Query;	// query with intersect on own line
	protected Query minusQuery;			// query with minus
	protected Query implicitJoinQuery;	// query with multiple implicit joins
	protected Query complexNestedQuery;	// query with nested selects in FROM, JOIN and WHERE
	protected Query fromFormatQuery;	// query with FROM at end of a line
	protected Query andTestQuery;		// query for testing (AND)
	protected Query minTestQuery;		// query for testing (MIN)
	protected Query min2TestQuery;		// query 2 for tetsing (MIN)
	protected Query minusTestQuery;		// query for testing (MINUS)
	
	// methods
	// default constructor - essentially cross-test fixture setup
	protected AbstractTest() {
		// DAO setup - change type of object instantiated (if necessary) and arguments to make this functional 
		// testDAO			= new OracleDataAccessObject("host", "port", "sysid", "user", "password", true);				// DAO and params must be changed to run unit tests 
		testDAO					= new OracleDataAccessObject("localhost", "1521", "toldidb", "paul", "toldi8cs", true); 
		//testDAO				= new MySQL80DataAccessObject("localhost", "3307", "test", "paul", "toldi42cs*", true);	
		//testDAO				= new MySQL5xDataAccessObject("localhost", "3306", "test", "wagnerpj", "toldics", true);
		//testDA0				= new MockDataAccessObject("xyz", "xyz", "test", "user", "pass", true);		// won't work with unit testing as can't execute queries and get reasonable results back
		
		achievementAllQuery = new Query("SELECT * FROM Achievement");
		creatureAllQuery    = new Query("SELECT * FROM Creature");
		creatureAllQueryLC  = new Query("select * from creature");
		creatureAllQueryBC  = new Query("SELECT c_id, c_name, c_type, reside_t_id FROM Creature");
		creatureAllQueryBC2 = new Query("SELECT c_name, c_type, c_id, reside_t_id FROM Creature");
		creatureOneColQuery = new Query("SELECT c_id FROM Creature");
		creatureOneRowQuery = new Query("SELECT c_id FROM Creature WHERE c_id = 1");
		creatureAndQuery	= new Query("SELECT c_id FROM Creature WHERE c_id >= 1 AND c_id <= 8");
		creatureOrQuery		= new Query("SELECT c_id FROM Creature WHERE c_id <= 1 OR c_id >= 8");
		creatureNotQuery	= new Query("SELECT c_id FROM Creature WHERE NOT c_id = 1");
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
		orderByDesc3Query	= new Query("SELECT * FROM Achievement ORDER BY c_id DESC, s_code DESCENDING");
		joinOneQuery		= new Query("SELECT a.c_id, c.c_name FROM Creature C " +
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
		minQuery			= new Query("SELECT s_code, MIN(DISTINCT c_id) FROM Achievement " +
											"GROUP BY s_code");
		sumQuery			= new Query("SELECT s_code, SUM(DISTINCT c_id) FROM Achievement " +
											"GROUP BY s_code");
		likeQuery			= new Query("SELECT C.c_id FROM Creature C " +
											"WHERE c_name LIKE '%a%'");
		multiLineQuery		= new Query("SELECT C.c_id, COUNT(s_code) FROM Creature C \n" +
											"LEFT JOIN Achievement A ON \n" +
											"C.c_id = A.c_id GROUP BY C.c_id");
		unionQuery			= new Query("SELECT c_id FROM Creature UNION SELECT c_id FROM Achievement");
		intersectQuery		= new Query("SELECT c_id FROM Creature INTERSECT SELECT c_id FROM Achievement");
		intersect2Query		= new Query("SELECT c_id FROM Creature INTERSECT\nSELECT c_id FROM Achievement");
		minusQuery			= new Query("SELECT c_id FROM Creature MINUS SELECT c_id FROM Achievement");
		implicitJoinQuery	= new Query("SELECT c_name, s_desc FROM Creature, Achievement, Skill WHERE Creature.c_id = Achievement.c_id " +
											"AND Achievement.s_code = Skill.s_code");
    	complexNestedQuery 	= new Query("SELECT t1.columnName2, t2.columnName3 \r\n" + 
				   							"FROM (SELECT columnName2 FROM tableName) t1\r\n" + 
				   							"JOIN (SELECT columnName3 FROM anotherTableName) t2 ON (t1.columnName = t2.columnName3)" +
				   							"WHERE t2.columnName3 IN (SELECT columnName4 FROM aThirdTableName);") ;
    	fromFormatQuery 	= new Query("SELECT c_id FROM\nCreature");
    	andTestQuery		= new Query("SELECT sandid, sanderling as SANDALIAS FROM Sandstorm WHERE sandid = 3 AND sanderling LIKE '%and%'");
    	minTestQuery		= new Query("SELECT MIN(remindid) FROM Reminder MINUS SELECT MIN(remindid) FROM Reminder WHERE minstring LIKE '%min%'");
    	min2TestQuery		= new Query("SELECT MIN(remindid) FROM Reminder MINUS SELECT MIN(remindid) FROM Reminder WHERE minstring LIKE '% min %'");
    	minusTestQuery		= new Query("SELECT remindid, minstring AS MINSTRING\r\n"
    									+ "FROM Reminder\r\n"
    									+ "    MINUS\r\n"
    									+ "SELECT remindid, minstring AS MINSTRING\r\n"
    									+ "FROM Reminder    \r\n"
    									+ "WHERE minstring LIKE '%minus%' ");
	}	// end - constructor/query initialization
	
	// finalize - essentially cross-test fixture teardown
	protected void finalize() throws Throwable {
		testDAO				= null;
		
		achievementAllQuery = null;
		creatureAllQuery    = null;
		creatureAllQueryLC  = null;
		creatureAllQueryBC  = null;
		creatureAllQueryBC2 = null;
		creatureOneColQuery = null;
		creatureOneRowQuery = null;
		creatureAndQuery	= null;
		creatureOrQuery		= null;
		creatureNotQuery	= null;
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
		orderByDesc3Query	= null;
		joinOneQuery		= null;
		joinManyQuery		= null;
		joinTableSelfQuery  = null;
		joinOuterQuery		= null;
		joinOuter2Query		= null;
		joinCrossQuery		= null;
		joinCross2Query		= null;
		avgQuery			= null;
		maxQuery			= null;
		minQuery			= null;
		sumQuery			= null;
		likeQuery			= null;
		multiLineQuery		= null;
		unionQuery			= null;
		intersectQuery		= null;
		intersect2Query		= null;
		minusQuery			= null;
		implicitJoinQuery	= null;
		complexNestedQuery	= null;
		fromFormatQuery		= null;
		andTestQuery		= null;
		minTestQuery		= null;
		min2TestQuery		= null;
		minusTestQuery		= null;
		
		super.finalize();
	}	// end - method finalize

}	// end - abstract class AbstractTest
