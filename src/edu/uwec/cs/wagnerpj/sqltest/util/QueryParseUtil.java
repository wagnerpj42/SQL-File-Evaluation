package edu.uwec.cs.wagnerpj.sqltest.util;

import edu.uwec.cs.wagnerpj.sqltest.sqltests.TestResultSetEqualContent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryParseUtil {

    /**
     * Returns the SELECT (...) FROM portion of a query
     * @param query The query in question
     * @return      The SELECT (...) FROM portion of a query
     */
    public static String identifySelectToFrom(String query){
        Matcher selectMatcher = Pattern.compile("(?i)SELECT(?=(\\s{1,10}|[(]))").matcher(query);
        Matcher fromMatcher = Pattern.compile("(?i)(?<=(\\s{1,999}|[)]|\"))FROM(?=(\\s{1,999}|[(]|\"))").matcher(query);
        if(!selectMatcher.find() || !fromMatcher.find()) return null;
        else return query.substring(selectMatcher.start(), fromMatcher.end()).toLowerCase();
    }

    /**
     * Identifies the FROM (...) [END_OF_QUERY] portion of the query
     * @param query The query in question
     * @return      The FROM (...) [END_OF_QUERY] portion of a query
     */
    public static String identifyFromToEnd(String query){
        Matcher fromMatcher = Pattern.compile("(?i)(?<=(\\s{1,999}|[)]|\"))FROM(?=(\\s{1,999}|[(]|\"))").matcher(query);
        if(!fromMatcher.find()) return null;
        return query.substring(fromMatcher.start(), query.length()).toLowerCase();
    }

    /**
     * Identifies a query from the FROM (inclusive) to the end of the query or to the first set-operator (exclusive)
     * @param query The query in question
     * @return      The FROM (inclusive) to the end of the query or to the first set-operator (exclusive)
     */
    public static String identifyFromToEndOrSetOperator(String query){
        Matcher fromMatcher = Pattern.compile("(?i)(?<=(\\s{1,999}|[)]|\"))FROM(?=(\\s{1,999}|[(]|\"))").matcher(query);
        if(!fromMatcher.find()) return null;
        else{
            ArrayList<Integer> setOperatorIndexes = identifyAllSetOperatorIndexes(query);
            int numParentheses = 0;
            int numQuotes = 0;
            for(int i = fromMatcher.end(); i < query.length(); i++){
                if(query.charAt(i) == '(' && numQuotes % 2 == 0) numParentheses++;
                else if(query.charAt(i) == ')' && numQuotes % 2 == 0) numParentheses--;
                else if(query.charAt(i) == '\"') numQuotes++;
                else if(setOperatorIndexes.contains(i) && numParentheses == 0){
                    return query.substring(fromMatcher.start(), i).toLowerCase();
                }

            }
            return query.substring(fromMatcher.start(), query.length()).toLowerCase();
        }
    }

    /**
     * Splits a SELECT statement into portions, with each portion corresponding to the logic of one column in the result set
     * @param query SELECT (...) FROM portion of a query. HINT: Use identifySelectToFrom() to retrieve this value
     * @return An ArrayList of substrings of the form [SELECT or ","] [COLUMN_LOGIC] [FROM or ","]
     */
    public static ArrayList<String> splitQuery(String query){
        try {
            int numQuotations = 0;
            int numParentheses = 0;
            int start = 0;
            int lastCommaIndex = 0;
            ArrayList<String> ret = new ArrayList<String>();

            for (int i = 0; i < query.length(); i++) {
                if (query.charAt(i) == ',' && numParentheses == 0 && numQuotations % 2 == 0) {
                    ret.add(query.substring(start, i) + ',');
                    start = i;
                    lastCommaIndex = i;
                }
                if (query.charAt(i) == '(') numParentheses++;
                if (query.charAt(i) == ')') numParentheses--;
                if (query.charAt(i) == '\"') numQuotations++;
            }
            ret.add(query.substring(lastCommaIndex));
            return ret;
        }
        catch(Exception e){
            throw e;
        }
    }

    /**
     * Identifies a column name and functions used on the column name.
     * @param s A substring returned from splitQuery()
     * @return  Index 0: Returns functions + columnName with the ':' character used as a delimiter between function names and column name
     *          example: avg:col1
     * @throws Exception Exception thrown if no column name can be identified
     */
    public static String[] identifyColumnName(String s) throws Exception{
        try {
            //Return two values
            //Index 0: The column name
            //Index 1: The column name with any functions stripped off
            String[] ret = new String[2];
            ret[0] = ""; ret[1] = "";
            StringBuilder sb = new StringBuilder();

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
    }

    /**
     * Utility function to identify all functions used on a column. Utilized by identifyColumnName()
     * @param columnString  Substring of a SELECT statement returned by splitQuery()
     * @param columnName    Column name identified in identifyColumnName()
     * @return              All functions used on the column, with ':' character used as a delimiter.
     */
    private static String identifyColumnFunctions(String columnString, String columnName){
        StringBuilder sb = new StringBuilder();
        ArrayList<String> functionNames = new ArrayList<String>();
        //IGNORABLE_KEYWORDS_4
        Matcher columnMatcher = Pattern.compile("(?i)(?<=(SELECT(?=\\s|[(]|\")|,|\\.|[(]|\\bdistinct\\b|\\bunique\\b|\\ball\\b)\\s{0,999})\\Q" + columnName + "\\E(?=\\s{0,999}((?<=(\\s|[)]|\"))FROM\\b|\\b\\w+\\b|\"[^\"]+\"|,|(?<=(\\s|\"))AS|[)]))").matcher(columnString);
        if(!columnMatcher.find()) return "";

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
        }

        for(int i = functionNames.size() - 1; i >= 0; i--){
            //IGNORABLE_FUNCTIONS
            //IGNORABLE_KEYWORDS_3
            if(!functionNames.get(i).matches("(?i)(cast|to_char|round|distinct|all|unique)"))
                sb.append(functionNames.get(i)).append(":");
        }
        return sb.toString().toLowerCase();
    }

    /**
     * Identifies any alias that follows a column name
     * @param columnSubstring   Return value of splitQuery()
     * @param columnName        Index 1 of array returned by identifyColumnName()
     * @return                  Name of alias (if any) of column, otherwise returns empty string
     */
    public static String identifyColumnAlias(String columnSubstring, String columnName) {
        StringBuilder ret = new StringBuilder();
        boolean startsWithQuote = false;
        boolean candidateAliasFound = false;
        int endIndex = 0;
        int beginIndex = columnSubstring.length() - 2;
        if(Character.toLowerCase(columnSubstring.charAt(columnSubstring.length() - 1)) == 'm'){
            beginIndex = columnSubstring.length() - 5;
        }

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
        }

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
        if(returnString.toLowerCase().equals(columnName.toLowerCase())) return "";
        return returnString;

    }


    /**
     * Identifies any prefix used with a column name
     * @param query         SELECT statement substring returned by splitQuery()
     * @param columnName    Index 1 of array returned by identifyColumnName()
     * @return              Returns prefix used with a column name
     */
    public static String identifyColumnPrefix(String query, String columnName){
        Matcher prefixMatcher = Pattern.compile("(?i)(\\b\\w+|\"[^\"]+\")(?=\\s*\\.\\s*\\Q" + columnName + "\\E)").matcher(query);
        if(prefixMatcher.find()) return query.substring(prefixMatcher.start(), prefixMatcher.end()).toLowerCase();
        return null;
    }


    /**
     * @param query Output of a call to identifyFromToEnd() or identifyFromToEndOrSetOperator()
     * @return Identifies all nested Selects (selects which follow a FROM or a JOIN keyword) exactly one level below the select
     *      * statement in question. Returns all subselects from SELECT to either closing parenthesis (exclusive) or to
     *      * first set operator (UNION, MINUS, INTERSECT)  that corresponds to subselect (exclusive). Maps this subselect to
     *      * the subselect's alias name (if present)
     */
    @SuppressWarnings("Duplicates")
    public static Map<String, String> identifyNestedSelectStatementsToEndOrSetOperator(String query){
        //Find a subselect statement, and parse until it's matching parenthesis is found. Continue until no more subselects exist
        Map<String, String> ret = new HashMap<>();
        String subSelect;
        String subSelectAlias;
        String queryPortion = query;
        int beginIndex;
        int endIndex;
        int numParentheses;
        int numQuotes;
        boolean setOperatorFound = false;
        ArrayList<Integer> setOperatorIndexes = identifyAllSetOperatorIndexes(query);
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
                        //If index is beginning of a set operator, end of select statement is reached
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
    }

    /**
     * @param query Output of a call to identifyFromToEnd() or identifyFromToEndOrSetOperator()
     * @return Identifies all subselects exactly one level below the select
     *         statement in question. Returns all subselects from SELECT to either closing parenthesis (exclusive) or to
     *         first set operator (UNION, MINUS, INTERSECT)  that corresponds to subselect (exclusive). Maps this subselect to
     *         the subselect's alias name (if present)
     */
    @SuppressWarnings("Duplicates")
    public static Map<String, String> identifySubselectStatementsToEndOrSetOperator(String query){
        //Find a subselect statement, and parse until it's matching parenthesis is found. Continue until no more subselects exist
        Map<String, String> ret = new HashMap<>();
        String subSelect;
        String subSelectAlias;
        String queryPortion = query;
        int beginIndex;
        int endIndex;
        int numParentheses;
        int numQuotes;
        boolean setOperatorFound = false;
        ArrayList<Integer> setOperatorIndexes = identifyAllSetOperatorIndexes(query);
        while(true) {
            numParentheses = 0;
            numQuotes = 0;
            setOperatorFound = false;
            Matcher nestedSelectMatcher = Pattern.compile("(?i)\\(\\s*SELECT\\s").matcher(queryPortion); //Search for subselect
            if (nestedSelectMatcher.find()) {
                beginIndex = nestedSelectMatcher.start();
                for (int i = beginIndex; i < queryPortion.length(); i++) {
                    if (queryPortion.charAt(i) == '"') numQuotes++;
                    else if (queryPortion.charAt(i) == '(' && numQuotes % 2 == 0) numParentheses++;
                    else if (queryPortion.charAt(i) == ')' && numQuotes % 2 == 0) numParentheses--;
                        //If index is beginning of a set operator, end of select statement is reached
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
    }

    /**
     * Utility function for identifying a subselect alias name. Used by identifyNestedSelectStatementsToEndOrSetOperator()
     * and identifySubselectStatementsToEndOrSetOperator()
     * @param query     Output of identifyFromToEnd() or identifyFromToEndOrSetOperator()
     * @param subSelect subselect statement identified in calling function
     * @return  name of subselect's alias
     */
    private static String identifySubSelectAlias(String query, String subSelect){
        Matcher subSelectAliasMatcher = Pattern.compile("(?i)(?<=\\Q" + subSelect + "\\E\\)\\s{0,999}(AS)?\\s{0,999})(\\w+\\b|\"[^\"]+\")(?<!\\bAS)").matcher(query);
        if(!subSelectAliasMatcher.find()) return null;
        else{
            String subSelectAliasName = query.substring(subSelectAliasMatcher.start(), subSelectAliasMatcher.end());
            if(!subSelectAliasName.matches("(?i)(ON|FROM|AS|JOIN|WHERE|SELECT|GROUP|HAVING|ORDER)")){
                return subSelectAliasName.toLowerCase();
            }
            else return null;
        }
    }

    /**
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
    }

    /**
     * Given the output returned by a call to identifyAllInnerJoinStatementOnClauses(), will identify the
     * column names of the equivalent columns and their respective aliases, and return them in the form of an
     * Edge.
     * @param onClause  Return value of identifyAllInnerJoinStatementOnClauses(). Looks like "(col1 = col2)"
     * @return Returns two edges. Index 0 returns an edge connecting the column's names
     *                            Index 1 returns an edge connecting the column's names
     * @throws Exception Exception will be thrown if column names cannot be identified.
     */
    public static Edge[] identifyEquivalentColumns(String onClause) throws Exception{
        String col1 = "";
        String col2 = "";
        String col1Alias = "";
        String col2Alias = "";
        Edge[] ret = new Edge[2];

        //step one: find equals sign, split string into two.
        //step two, use identifyColumnName and identify Alias Name
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
    }

    //Utility function that returns indexes of all set operators in a query.
    private static ArrayList<Integer> identifyAllSetOperatorIndexes(String query){
        ArrayList<Integer> ret = new ArrayList<>();
        Matcher setOperatorMatcher = Pattern.compile("(?i)\\b(INTERSECT|UNION|MINUS)\\b").matcher(query);
        while(setOperatorMatcher.find()){
            ret.add(setOperatorMatcher.start());
        }
        return ret;
    }

    //Utility function that returns indexes of all inner joins in a query
    private static ArrayList<Integer> identifyAllInnerJoinStatementIndexes(String query){
        ArrayList<Integer> ret = new ArrayList<>();
        Matcher matcher = Pattern.compile("(?i)(?<!(NATURAL(\\s{0,999}INNER|\\s{0,999}OUTER)?|CROSS|OUTER)\\s{1,999})JOIN\\b").matcher(query);
        while(matcher.find()){
            ret.add(matcher.start());
        }
        return ret;
    }

    //Utility function that returns indexes of all ON clauses in a query.
    private static ArrayList<Integer> identifyAllOnClauseIndexes(String query){
        ArrayList<Integer> ret = new ArrayList<>();
        Matcher matcher = Pattern.compile("(?i)\\bON\\b").matcher(query);
        while(matcher.find()){
            ret.add(matcher.start());
        }
        return ret;
    }

    //Utility function that checks if an on clause is comparing columns on equality rather than inequality.
    private static boolean isValidOnClause(String onClause){
        int numQuotes = 0;
        for(int i = 0; i < onClause.length(); i++){
            if(onClause.charAt(i) == '\"') numQuotes++;
            else if ((onClause.charAt(i) == '>' || onClause.charAt(i) == '<' || onClause.charAt(i) == '!') && numQuotes % 2 == 0) return false;
            else if(onClause.charAt(i) == '=' && numQuotes % 2 == 0) return true;
        }

        return false;
    }

    //Connects two strings in a graph.
    public static class Edge{
        String s1;
        String s2;

        public Edge(String s1, String s2){
            this.s1 = s1;
            this.s2 = s2;
        }

        public String getOne(){
            return s1;
        }

        public String getTwo(){
            return s2;
        }

        public void setOne(String s){
            this.s1 = s;
        }

        public void setTwo(String s){
            this.s2 = s;
        }

        public String toString(){
            return "(s1: " + s1 + " s2: " + s2 + ")";
        }
    }

    //Check if columns are associated due to JOIN operations present in the query.
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
    }

    //Get all column names equivalent to a supplied column name based on equivalence graph.
    public static ArrayList<Edge> getAdjacent(String s, ArrayList<Edge> graph){
        ArrayList<Edge> ret = new ArrayList<Edge>();
        for(Edge edge : graph){
            if(edge.getOne().equals(s)) ret.add(edge);
        }
        return ret;
    }
}
