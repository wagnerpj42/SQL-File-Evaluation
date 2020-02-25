package sqlfe.util;

//import edu.uwec.cs.wagnerpj.sqltest.sqltests.TestResultSetEqualContent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Group of utility methods related to parsing query information and extracting certain parts of a query
 */
public class QueryParseUtil {

    /**
     * identifySelectToFrom()
     * 
     * returns the SELECT (...) FROM portion of a query
     * @param query The query in question
     * @return      The SELECT (...) FROM portion of a query
     */
    public static String identifySelectToFrom(String query){
        Matcher selectMatcher = Pattern.compile("(?i)SELECT(?=(\\s{1,10}|[(]))").matcher(query);
        Matcher fromMatcher = Pattern.compile("(?i)(?<=(\\s{1,999}|[)]|\"))FROM(?=(\\s{1,999}|[(]|\"))").matcher(query);
        if(!selectMatcher.find() || !fromMatcher.find()) return null;
        else return query.substring(selectMatcher.start(), fromMatcher.end()).toLowerCase();
    }	// end - method identifySelectToFrom

    /**
     * identifyFromToJoinOrWhere()
     * 
     * returns the FROM (...) JOIN portion of a query if SQL-99 JOIN/ON used, else FROM (...) WHERE if single table or SQL-92 comma join
     * @param query The query in question
     * @return      The FROM (...) JOIN/WHERE portion of a query
     */
    public static String identifyFromToJoinOrWhere(String query){
        Matcher fromMatcher  = Pattern.compile("(?i)FROM(?=(\\s{1,10}|[(]))").matcher(query);
        Matcher joinMatcher  = Pattern.compile("(?i)(?<=(\\s{1,999}|[)]|\"))JOIN(?=(\\s{1,999}|[(]|\"))").matcher(query);
        Matcher whereMatcher = Pattern.compile("(?i)(?<=(\\s{1,999}|[)]|\"))WHERE(?=(\\s{1,999}|[(]|\"))").matcher(query);
        
        boolean fromFound = fromMatcher.find();		// is a FROM present in the query?
        boolean joinFound = joinMatcher.find();		// is a JOIN present in the query?
        boolean whereFound = whereMatcher.find();	// is a WHERE present in the query?
        
        if (!fromFound) {							// not even a FROM - invalid
        	return null;
        } else if (fromFound && joinFound) {		// if found from and join, return substring between
        	return query.substring(fromMatcher.start(), joinMatcher.end()).toLowerCase();
        											// if found from and where but no join, return substring between
        } else if (fromFound && !joinFound && whereFound) {		
        	return query.substring(fromMatcher.start(), whereMatcher.end()).toLowerCase();
        											// if found from but no join or where, return from to end
        } else if (fromFound && !joinFound && !whereFound) {
        	return query.substring(fromMatcher.start(), query.length()).toLowerCase();
        } else {									// any other case	
        	return null;
        }
    }	// end - method identifyFromToJoinOrWhere
    
    /**
     * identifyFromToEnd()
     * 
     * Identifies and returns the "FROM (...)" portion of the query
     * @param query The query in question
     * @return      The FROM (...) [END_OF_QUERY] portion of a query
     */
    public static String identifyFromToEnd(String query){
        Matcher fromMatcher = Pattern.compile("(?i)(?<=(\\s{1,999}|[)]|\"))FROM(?=(\\s{1,999}|[(]|\"))").matcher(query);
        if(!fromMatcher.find()) return null;
        return query.substring(fromMatcher.start(), query.length()).toLowerCase();
    }	// end - method identifyFromToEnd

    /**
	 * identifyFromToEndOrSetOperator()
	 *
     * Identifies the substring of a query from the FROM (inclusive) to the end of the query or to the first set-operator (exclusive)
     * @param query The query in question
     * @return      The FROM (inclusive) to the end of the query or to the first set-operator (exclusive)
     */
    public static String identifyFromToEndOrSetOperator(String query){
        Matcher fromMatcher = Pattern.compile("(?i)(?<=(\\s{1,999}|[)]|\"))FROM(?=(\\s{1,999}|[(]|\"))").matcher(query);
        if(!fromMatcher.find()) return null;
        else{
            //find all indexes in the query where a set operator begins
            //we will check if these set operators exist on the same level (i.e. not in a subselect) as the query in question
            ArrayList<Integer> setOperatorIndexes = identifyAllSetOperatorIndexes(query);
            int numParentheses = 0;
            int numQuotes = 0;
            for(int i = fromMatcher.end(); i < query.length(); i++){
                if(query.charAt(i) == '(' && numQuotes % 2 == 0) numParentheses++;
                else if(query.charAt(i) == ')' && numQuotes % 2 == 0) numParentheses--;
                else if(query.charAt(i) == '\"') numQuotes++;
                //if current index is beginning of a set operator and we aren't in a subselect (i.e. numParentheses == 0), stop parsing
                else if(setOperatorIndexes.contains(i) && numParentheses == 0){
                    return query.substring(fromMatcher.start(), i).toLowerCase();
                }

            }
            return query.substring(fromMatcher.start(), query.length()).toLowerCase();
        }
    }	// end - method identifyFromToEndOrSetOperator

    /**
	 * splitQuery()
	 * 
     * Splits a SELECT statement into portions, with each portion containing the substring that corresponds to one column in the result set
     * @param query The [SELECT (...) FROM] portion of a query. NOTE: Use identifySelectToFrom() to retrieve this substring
     * @return Returns an ArrayList of substrings, with each index containing the substring that contains all logic corresponding
     *          to a column in a result set. The commas, SELECT, or FROM that envelop the column logic will included in the substring as well.
     *
     *          E.g.        [SELECT | , ] column_logic_here [FROM | , ]
     *
     */
    public static ArrayList<String> splitQuery(String query){
        try {
            int numQuotations = 0;
            int numParentheses = 0;
            int start = 0;
            int lastCommaIndex = 0;
            ArrayList<String> ret = new ArrayList<String>();

            //split query at any comma that is not between parentheses or quotation marks
            for (int i = 0; i < query.length(); i++) {
                if (query.charAt(i) == ',' && numParentheses == 0 && numQuotations % 2 == 0) {
                    ret.add(query.substring(start, i) + ',');
                    start = i;
                    lastCommaIndex = i;
                }
                if (query.charAt(i) == '(' && numQuotations % 2 == 0) numParentheses++;
                if (query.charAt(i) == ')' && numQuotations % 2 == 0) numParentheses--;
                if (query.charAt(i) == '\"') numQuotations++;
            }
            ret.add(query.substring(lastCommaIndex));
            return ret;
        }
        catch(Exception e){
            throw e;
        }
    }	// end - method splitQuery

    /**
     * identifyColumnName()
     * 
     * Identifies a column name and functions used on the column name.
     * @param s One of the substrings returned by the splitQuery() method. String looks like "[SELECT | ,] column_logic [FROM | ,]"
     * @return  Index 0: Returns column name AND functions, functions separated by delimiter ":" character
     *          e.g.    func1:func2:columnName
     *          Index 1: Returns only the column name, but not any functions that may have been present
     *          e.g.    columnName
     * @throws Exception Exception thrown if no column name can be identified
     */
    public static String[] identifyColumnName(String s) throws Exception{
        try {
            String[] ret = new String[2];
            ret[0] = ""; ret[1] = "";
            //StringBuilder sb = new StringBuilder();

            //Look for something that looks like SELECT * FROM
            //IGNORABLE_KEYWORDS_1
            Pattern patternStar = Pattern.compile("(?i)SELECT\\s+(DISTINCT\\s+|ALL\\s+|UNIQUE\\s+)?\\*\\s+FROM\\b");
            Matcher matcherStar = patternStar.matcher(s);
            if(matcherStar.find()){
                ret[0] = "*-selectall"; //special return value to notify calling function that SELECT * FROM was found
                return ret;
            }

            //Identify column name
            //IGNORABLE_KEYWORDS_2
            Pattern patternCol = Pattern.compile("(?i)(?<=(SELECT(?=\\s|[(]|\")|,|\\.|=|[(]|\\bdistinct\\b|\\bunique\\b|\\ball\\b)\\s{0,999})(\\w+\\b(?<!\\b(distinct|unique|all))|\\*|\"[^\"]+\")(?=\\s{0,999}((?<=(\\s|[)]|\"))FROM\\b|\\b\\w+\\b|\"[^\"]+\"|,|=|(?<=(\\s|\"))AS|[)]))");
            Matcher matcherCol = patternCol.matcher(s);
            matcherCol.find();
            String columnName = s.substring(matcherCol.start(), matcherCol.end());
            ret[1] = columnName.toLowerCase();
            ret[0] = identifyColumnFunctions(s, ret[1]) + ret[1];

            if(ret[1].equals("*")){
                if(!ret[0].matches("(?i)count:\\*")) throw new Exception();
            }
            return ret;
        }
        catch(Exception e){
            throw e;
        }
    }	// end - method identifyColumnName

    /**
     * identifyColumnFunctions()
     * 
     * Utility function to identify all functions used on a column. Function is called by identifyColumnName() as utility function
     * @param columnString  Substring of a SELECT statement. NOTE: Use a value returned by splitQuery()
     * @param columnName    Column name in question. NOTE: Use index 1 of array returned by a call to identifyColumnName()
     * @return              All functions used on the column, with ':' character used as a delimiter.
     */
    private static String identifyColumnFunctions(String columnString, String columnName){
        StringBuilder sb = new StringBuilder();
        ArrayList<String> functionNames = new ArrayList<String>();
        //IGNORABLE_KEYWORDS_4
        Matcher columnMatcher = Pattern.compile("(?i)(?<=(SELECT(?=\\s|[(]|\")|,|\\.|[(]|\\bdistinct\\b|\\bunique\\b|\\ball\\b)\\s{0,999})\\Q" + columnName + "\\E(?=\\s{0,999}((?<=(\\s|[)]|\"))FROM\\b|\\b\\w+\\b|\"[^\"]+\"|,|(?<=(\\s|\"))AS|[)]))").matcher(columnString);
        if(!columnMatcher.find()) return "";

        //starting from column name, search backwards for functions used
        boolean appending = false;
        boolean hasChars = false;
        for(int i = columnMatcher.start(); i >= 0; i--){
            if(columnString.charAt(i) == '('){
                appending = true;
                if(hasChars){
                    functionNames.add(sb.reverse().toString().toLowerCase());
                    hasChars = false;
                    sb.setLength(0);
                }
            }
            else if(appending && (Character.isAlphabetic(columnString.charAt(i)) || Character.isDigit(columnString.charAt(i)) || columnString.charAt(i) == '_')){
                sb.append(columnString.charAt(i));
                hasChars = true;
            }
            else if(appending && hasChars){
                functionNames.add(sb.reverse().toString().toLowerCase());
                hasChars = false;
                sb.setLength(0);
                if(columnString.charAt(i) == ' ') appending = false;
            }
        }	// end - method identifyColumnFunctions()

        for(int i = functionNames.size() - 1; i >= 0; i--){
            //IGNORABLE_FUNCTIONS
            //IGNORABLE_KEYWORDS_3
            if(!functionNames.get(i).matches("(?i)(cast|to_char|round|distinct|all|unique)"))
                sb.append(functionNames.get(i)).append(":");
        }
        return sb.toString().toLowerCase();
    }

    /**
     * identifyColumnAlias()
     * 
     * Identifies any alias that follows a column name in the SELECT portion of a query
     * @param columnSubstring   Use one of the substrings returned by the splitQuery() utility method
     * @param columnName        Use index 1 of array returned by identifyColumnName() (i.e. column name, ignoring any functions used on it)
     * @return                  Name of alias (if any) of a column, otherwise returns empty string
     *                          e.g. INPUT:  ", col_name AS col_alias FROM"
     *                               OUTPUT: "col_alias"
     */
    public static String identifyColumnAlias(String columnSubstring, String columnName) {
        StringBuilder ret = new StringBuilder();
        boolean startsWithQuote = false;
        boolean candidateAliasFound = false;
        int endIndex = 0;
        int beginIndex = columnSubstring.length() - 2;

        //If last word in string is "FROM", then move begin index to index preceding the "f".
        if(Character.toLowerCase(columnSubstring.charAt(columnSubstring.length() - 1)) == 'm'){
            beginIndex = columnSubstring.length() - 5;
        }

        //iterate backwards through string and identify index of the first number, letter or quotation mark (i.e. first index of possible alias)
        for(int i = beginIndex; i > 0; i--){
            if(columnSubstring.charAt(i) == ' ') continue;
            else if(columnSubstring.charAt(i) == '"'){
                startsWithQuote = true;
                candidateAliasFound = true;
                endIndex = i;
                break;
            }
            else if(Character.isAlphabetic(columnSubstring.charAt(i)) || Character.isDigit(columnSubstring.charAt(i)) || columnSubstring.charAt(i) == '_'){
                candidateAliasFound = true;
                endIndex = i;
                break;
            }
            else return "";
        }	// end - method identifyColumnAlias

        //parse backwards from the index found in the loop above until the beginning of the word is reached
        if(candidateAliasFound){
            int i = endIndex;
            if(startsWithQuote){
                ret.append(columnSubstring.charAt(i));
                i--;
                while(columnSubstring.charAt(i) != '"'){
                    ret.append(columnSubstring.charAt(i));
                    i--;
                }
                ret.append("\"");
            }
            else{
                while(Character.isAlphabetic(columnSubstring.charAt(i)) || Character.isDigit(columnSubstring.charAt(i)) || columnSubstring.charAt(i) == '_'){
                    ret.append(columnSubstring.charAt(i));
                    i--;
                }
            }
        }

        String returnString = ret.reverse().toString();
        //if the word found is the column Name, then no alias was used
        if(returnString.toLowerCase().equals(columnName.toLowerCase())) return "";
        //otherwise return the word
        return returnString;

    }	// end - method 


    /**
     * identifyColumnPrefix()
     * 
     * Identifies any prefix used with a column name
     * @param query         A substring returned by splitQuery() that corresponds to column in question
     * @param columnName    Index 1 of array returned by identifyColumnName() (i.e. column name, ignoring any functions used on it)
     * @return              Returns prefix used with a column name, if any. Else returns null.
     *                      E.g. Input:     ", C.col_name FROM"
     *                           Output     "C"
     */
    public static String identifyColumnPrefix(String query, String columnName){
        Matcher prefixMatcher = Pattern.compile("(?i)(\\b\\w+|\"[^\"]+\")(?=\\s*\\.\\s*\\Q" + columnName + "\\E)").matcher(query);
        if(prefixMatcher.find()) return query.substring(prefixMatcher.start(), prefixMatcher.end()).toLowerCase();
        return null;
    }	// end - method identifyColumnPrefix


    /**
     * identifySubSelectStatementsToEndOrSetOperator()
     * 
     * @param query The FROM (...) [END OF QUERY] portion of a select statement
     *              NOTE: Use output of a call to identifyFromToEnd() or identifyFromToEndOrSetOperator()
     * @return Identifies all sub-selects (selects which follow a FROM or a JOIN keyword) exactly one level below the select
     *      * statement in question. Returns all sub-selects from SELECT to either closing parenthesis (exclusive) or to
     *      * first set operator (UNION, MINUS, INTERSECT)  that corresponds to sub-select (exclusive). Maps this sub-select to
     *      * the sub-select's alias name (if present)
     */
    public static Map<String, String> identifySubSelectStatementsToEndOrSetOperator(String query){
        //Find a sub-select statement, and parse until it's matching parenthesis is found. Continue until no more subselects exist
        Map<String, String> ret = new HashMap<>();
        String subSelect;
        String subSelectAlias;
        String queryPortion = query;
        int beginIndex;
        int endIndex;
        int numParentheses;
        int numQuotes;
        boolean setOperatorFound = false;
        ArrayList<Integer> setOperatorIndexes = identifyAllSetOperatorIndexes(query);   //find indexes of all set operators
        while(true) {
            numParentheses = 0;
            numQuotes = 0;
            setOperatorFound = false;
            Matcher nestedSelectMatcher = Pattern.compile("(?i)(?<=(FROM|JOIN)[\\s[(]]{0,100})\\(\\s*SELECT\\s").matcher(queryPortion); //Search for subselect
            if (nestedSelectMatcher.find()) {
                beginIndex = nestedSelectMatcher.start();
                for (int i = beginIndex; i < queryPortion.length(); i++) {
                    if (queryPortion.charAt(i) == '"') numQuotes++;
                    else if (queryPortion.charAt(i) == '(' && numQuotes % 2 == 0) numParentheses++;
                    else if (queryPortion.charAt(i) == ')' && numQuotes % 2 == 0) numParentheses--;
                    //If index is beginning of a set operator that does not belong to another subselect, end of select statement is reached
                    else if (setOperatorIndexes.contains(i) && numParentheses == 1) {
                        endIndex = i;
                        setOperatorFound = true;
                        subSelect = queryPortion.substring(beginIndex+1, endIndex);			   //select subselect
                        subSelectAlias = identifySubSelectAlias(queryPortion, subSelect);	   //search for subselect alias
                        ret.put(subSelect, subSelectAlias);									   //add subselect and it's alias to return mapping
                    }
                    //If matching parenthesis is found, end of select statement is reached
                    if (numParentheses == 0) {	//Once we find matching parenthesis, end of select statement is reached
                        endIndex = i;
                        if(!setOperatorFound){
                            subSelect = queryPortion.substring(beginIndex+1, endIndex);			   //select subselect
                            subSelectAlias = identifySubSelectAlias(queryPortion, subSelect);	   //search for subselect alias
                            ret.put(subSelect, subSelectAlias);									   //add subselect and it's alias to return mapping
                        }
                        queryPortion = queryPortion.substring(endIndex, queryPortion.length());//chop off this subselect from overall query string and repeat
                        break;
                    }
                }
            } else return ret;	//if no more subselects are found, return
        }
    }	// end - method identifyNestedSelectStatementsToEndOrSetOperator

    /**
     * 	identifySubSelectStatements()
     * 
     *  Identifies all sub-selects exactly one level below the select
     *  statement in question. Returns all sub-selects from SELECT (inclusive) to either closing parenthesis (exclusive) or to
     *  first set operator (UNION, MINUS, INTERSECT) that corresponds to sub-select (exclusive). Maps this sub-select to
     *  the sub-select's alias name (if present)
     * @param query Output of a call to identifyFromToEnd() or identifyFromToEndOrSetOperator()
     * @return      Returns the entire subselect from SELECT (inclusive) to the parenthesis ")" that ends the subselect (exclusive)
     *
     *              e.g. INPUT:         FROM (SELECT columnName2 FROM tableName) t1
     *                                  JOIN (SELECT columnName3 FROM anotherTableName) t2 ON (t1.columnName = t2.columnName3)
     *
     *                   OUTPUT:        The following mapping will be returned, containing both subselects found:
     *                                      SELECT columnName2 FROM tableName , t1
     *                                      SELECT columnName3 FROM anotherTableName , t2
     */
    public static Map<String, String> identifySubSelectStatements(String query){
        //Find a subselect statement, and parse until it's matching parenthesis is found. Continue until no more subselects exist
        Map<String, String> ret = new HashMap<>();
        String subSelect;
        String subSelectAlias;
        String queryPortion = query;
        int beginIndex;
        int endIndex;
        int numParentheses;
        int numQuotes;
        while(true) {
            numParentheses = 0;
            numQuotes = 0;
            //Find a SELECT statement that does not follow a set operator
            Matcher nestedSelectMatcher = Pattern.compile("(?i)(?<!(UNION|INTERSECT|MINUS)[\\s[(]]{0,100})\\(\\s*SELECT\\s").matcher(queryPortion); //Search for subselect
            if (nestedSelectMatcher.find()) {
                beginIndex = nestedSelectMatcher.start();
                for (int i = beginIndex; i < queryPortion.length(); i++) {
                    if (queryPortion.charAt(i) == '"') numQuotes++;
                    else if (queryPortion.charAt(i) == '(' && numQuotes % 2 == 0) numParentheses++;
                    else if (queryPortion.charAt(i) == ')' && numQuotes % 2 == 0) numParentheses--;
                        //If index is beginning of a set operator, end of select statement is reached
                    //If matching parenthesis is found, end of select statement is reached
                    if (numParentheses == 0) {	//Once we find matching parenthesis, end of select statement is reached
                        endIndex = i;
                        subSelect = queryPortion.substring(beginIndex+1, endIndex);			   //select subselect
                        subSelectAlias = identifySubSelectAlias(queryPortion, subSelect);	   //search for subselect alias
                        ret.put(subSelect, subSelectAlias);									   //add subselect and it's alias to return mapping

                        queryPortion = queryPortion.substring(endIndex, queryPortion.length());//chop off this subselect from overall query string and repeat
                        break;
                    }
                }
            } else return ret;	//if no more subselects are found, return
        }
    }	// end - method identifySubSelectStatements

    /**
     * identifySubSelectAlias()
     * 
     * Utility function for identifying a subselect alias name. Used by identifyNestedSelectStatementsToEndOrSetOperator()
     * and identifySubselectStatementsToEndOrSetOperator()
     * @param query     Output of identifyFromToEnd() or identifyFromToEndOrSetOperator()
     * @param subSelect subselect statement identified in calling function
     * @return  name of subselect's alias.
     */
    private static String identifySubSelectAlias(String query, String subSelect){
        //look for the word that follows a subselect, or the word that follows "AS" after the subselect
        Matcher subSelectAliasMatcher = Pattern.compile("(?i)(?<=\\Q" + subSelect + "\\E\\)\\s{0,999}(AS)?\\s{0,999})(\\w+\\b|\"[^\"]+\")(?<!\\bAS)").matcher(query);
        if(!subSelectAliasMatcher.find()) return null;
        else{
            String subSelectAliasName = query.substring(subSelectAliasMatcher.start(), subSelectAliasMatcher.end());
            //if the word found is one of these SQL keywords, it is not an alias
            if(!subSelectAliasName.matches("(?i)(ON|FROM|AS|JOIN|WHERE|SELECT|GROUP|HAVING|ORDER)")){
                return subSelectAliasName.toLowerCase();
            }
            else return null;
        }
    }	// end - method identifySubSelectAlias

    /**
     * identifyAllInnerJoinStatementOnClauses()
     * 
     * Identifies all equations that correspond to the ON clause of an inner join statement that exists on the same level
     * of a given SELECT statement (i.e. not in a subselect)
     * @param query Return value of either identifyFromToEnd() or identifyFromToEndOrSetOperator()
     * @return  ArrayList that contains all substrings of form "(col1 = col2)" that follow an ON keyword that corresponds to
     *          an inner join statement that exists on the same level as a SELECT statement (i.e. not in a subselect)
     */
    public static ArrayList<String> identifyAllInnerJoinStatementOnClauses(String query){
        ArrayList<String> ret = new ArrayList<String>();

        //identify indexes of all inner joins and on clauses
        //we will make sure that these indexes do not correspond to a subselect statement
        ArrayList<Integer> innerJoinedIndexes = identifyAllInnerJoinStatementIndexes(query);
        ArrayList<Integer> onIndexes = identifyAllOnClauseIndexes(query);

        StringBuilder sb = new StringBuilder();
        int numParentheses = 0;
        int numQuotes = 0;
        boolean joinFound = false;
        boolean onFound = false;
        boolean append = false;
        for(int i = 0; i < query.length(); i++){
            //if opening parentheses found, increment count
            if(query.charAt(i) == '(' && numQuotes % 2 == 0){
                numParentheses++;
                if(onFound) append = true;
                if(append) sb.append(query.charAt(i));
            }
            //if closing parentheses found, decrement count
            else if(query.charAt(i) == ')' && numQuotes % 2 == 0){
                numParentheses--;
                //if we found ON statement already, then we are done
                if(onFound){
                    sb.append(query.charAt(i));
                    ret.add(sb.toString());
                    sb.setLength(0);
                    onFound = false;
                    joinFound = false;
                    append = false;
                }
            }
            else if(query.charAt(i) == '"') numQuotes++;
            else if(innerJoinedIndexes.contains(i) && numParentheses == 0) joinFound = true;
            else if(onIndexes.contains(i) && joinFound) onFound = true;
            else if(append) sb.append(query.charAt(i));
        }
        return ret;
    }	// end - method identifyAllInnerJoinStatementOnClauses

    /**
     * identifyEquivalentColumns()
     * 
     * Using one element of the list returned by a call to identifyAllInnerJoinStatementOnClauses(), will identify the
     * column names of the columns being joined as well as their respective aliases, and return both in the form of an Edge.
     * @param onClause  Return value of identifyAllInnerJoinStatementOnClauses(). Looks like "(col1 = col2)"
     * @return Returns size 2 array of edges.
     *                            Index 0 returns an edge connecting the column's aliases
     *                            Index 1 returns an edge connecting the column's names
     *                            e.g. INPUT:                         (a.col1 = b.col2)
     *                           OUTPUT:  Two edges..   a <-----> b,      col1 <-----> col2
     * @throws Exception Exception will be thrown if column names cannot be identified.
     */
    public static Edge[] identifyEquivalentColumns(String onClause) throws Exception{
        String col1 = "";
        String col2 = "";
        String col1Alias = "";
        String col2Alias = "";
        Edge[] ret = new Edge[2];

        String s1 = "";
        String s2 = "";
        int numQuotes = 0;
        for(int i = 0; i < onClause.length(); i++){
            if(onClause.charAt(i) == '\"') numQuotes++;
            else if(onClause.charAt(i) == '=' && numQuotes % 2 == 0){
                s1 = onClause.substring(0, i+1);
                s2 = onClause.substring(i, onClause.length());
                break;
            }
        }

        col1 = identifyColumnName(s1)[0];
        col2 = identifyColumnName(s2)[0];
        col1Alias = identifyColumnPrefix(s1, col1);
        col2Alias = identifyColumnPrefix(s2, col2);

        Edge columns = new Edge(col1, col2);
        Edge aliases = new Edge(col1Alias, col2Alias);
        ret[0] = aliases;
        ret[1] = columns;

        return ret;
    }	// end - method identifyEquivalentColumns

    /**
     * identifyAllSetOperatorIndexes()
     * 
     * Utility function that returns indexes of all set operators in a query.
     * @param select query as string
     * @return arraylist of string positions of all set operators (INTERSECT, UNION, and MINUS) in the select query
     */
    private static ArrayList<Integer> identifyAllSetOperatorIndexes(String query){
        ArrayList<Integer> ret = new ArrayList<>();
        Matcher setOperatorMatcher = Pattern.compile("(?i)\\b(INTERSECT|UNION|MINUS)\\b").matcher(query);
        while(setOperatorMatcher.find()){
            ret.add(setOperatorMatcher.start());
        }
        return ret;
    }	// end - method identifyAllSetOperatorIndexes

    
    //Utility function that returns indexes of all inner joins in a query
    private static ArrayList<Integer> identifyAllInnerJoinStatementIndexes(String query){
        ArrayList<Integer> ret = new ArrayList<>();
        Matcher matcher = Pattern.compile("(?i)(?<!(NATURAL(\\s{0,999}INNER|\\s{0,999}OUTER)?|CROSS|OUTER)\\s{1,999})JOIN\\b").matcher(query);
        while(matcher.find()){
            ret.add(matcher.start());
        }
        return ret;
    }	// end - method identifyAllInnerJoinStatementIndexes

    //Utility function that returns indexes of all ON clauses in a query.
    private static ArrayList<Integer> identifyAllOnClauseIndexes(String query){
        ArrayList<Integer> ret = new ArrayList<>();
        Matcher matcher = Pattern.compile("(?i)\\bON\\b").matcher(query);
        while(matcher.find()){
            ret.add(matcher.start());
        }
        return ret;
    }	// end - nmethod identifyAllOnClauses



    // Nested class Edge - connects two strings in a graph
    //    used here for testing connections between columns
    public static class Edge {
        String s1;
        String s2;

        public Edge(String s1, String s2) {
            this.s1 = s1;
            this.s2 = s2;
        }

        public String getOne() {
            return s1;
        }

        public String getTwo() {
            return s2;
        }

        public void setOne(String s) {
            this.s1 = s;
        }

        public void setTwo(String s) {
            this.s2 = s;
        }

        public String toString() {
            return "(s1: " + s1 + " s2: " + s2 + ")";
        }
    }	// end - nested class Edge

    /**
     * areConnected()
     * 
     * Check if columns are associated due to JOIN operations present in the query.
     * @param s1 - first column
     * @param s2 - second column
     * @param graph - graph of edges between column names
     * @return boolean whether s1 and s2 are associated
     */
    public static boolean areConnected (String s1, String s2, ArrayList<Edge> graph){
        ArrayList<String> visited = new ArrayList<String>();
        List<String> queue = new LinkedList<String>();
        queue.add(s1);

        String current;
        while(!queue.isEmpty()) {
            current = queue.remove(0);
            visited.add(current);
            ArrayList<Edge> adjacent = getAdjacent(current, graph);
            for (Edge edge : adjacent) {
                String adjacentNode = edge.getTwo();
                if (adjacentNode.equals(s2)) return true;
                if (!visited.contains(adjacentNode)) {
                    queue.add(adjacentNode);
                }
            }
        }
        return false;
    }	// end - method areConnected

    /**
     * getAdjacent()
     * 
     * Get all column names equivalent to a supplied column name based on equivalence graph.
     * @param s - supplied column name
     * @param graph - equivalence graph for column associations
     * @return arraylist of edges between supplied column and others
     */
    public static ArrayList<Edge> getAdjacent(String s, ArrayList<Edge> graph){
        ArrayList<Edge> ret = new ArrayList<Edge>();
        for(Edge edge : graph){
            if(edge.getOne().equals(s)) ret.add(edge);
        }
        return ret;
    }	// end - method getAdjacent

    
    // temporary main for initial testing
    public static void main(String [] args) {
    	@SuppressWarnings("unused")
    	String simpleQueryString = "SELECT c_id FROM Creature";
		//String queryString = "SELECT C.c_id, c_name, s_desc FROM Creature C JOIN Achievement A ON C.c_id = A.c_id WHERE a.s_code = 'S';";
		//String oldStyleQueryString = "SELECT C.c_id, c_name, s_desc FROM Creature C, Achievement A WHERE a.s_code = 'S';";
    	//String oneTableQueryString = "SELECT C.c_id, c_name FROM Creature C WHERE a.s_code = 'S';";
		//String mixedQueryString = "SELECT c_id FROM Creature, Achievement JOIN Skill ON Skill.s_code = Achievement.s_code";
    	//String nestedQueryString = "SELECT C.c_id, c_name, s_desc FROM Creature C JOIN Achievement A ON C.c_id = A.c_id WHERE a.s_code IN (SELECT s_code FROM Skill WHERE s_desc = 'Swim');";
    	String complexNestedQueryString = "SELECT t1.columnName2, t2.columnName3 \r\n" + 
    									   "FROM (SELECT columnName2 FROM tableName) t1\r\n" + 
    									   "JOIN (SELECT columnName3 FROM anotherTableName) t2 ON (t1.columnName = t2.columnName3)" +
    									   "WHERE t2.columnName3 IN (SELECT columnName4 FROM aThirdTableName);" ;
    	String result = identifySelectToFrom(simpleQueryString);
    	System.out.println("result is: >" + result + "<");
    	
    	result = identifyFromToEnd(simpleQueryString);
    	System.out.println("result is: >" + result + "<");   
    	
    	result = identifyFromToJoinOrWhere(simpleQueryString);
    	System.out.println("result is: >" + result + "<");  
    	
    	Map<String, String> subResults = identifySubSelectStatements(complexNestedQueryString);
    	for (Map.Entry<String, String> entry : subResults.entrySet()) {
    	     System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
    	}
    }	// end - method main
    
}	// end - class QueryParseUtil
