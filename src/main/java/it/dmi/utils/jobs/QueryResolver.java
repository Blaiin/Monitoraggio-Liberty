package it.dmi.utils.jobs;

import it.dmi.structure.data.entities.Configurazione;
import it.dmi.structure.internal.QueryType;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.update.Update;

import java.util.regex.Pattern;

@Slf4j
public class QueryResolver {

    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "'|--|(/\\*(?:.|[\\n\\r])*?\\*/)|" +
                    "(\\b(SELECT|UNION|INSERT|UPDATE|DELETE|DROP|ALTER|TRUNCATE)\\b.*\\b(SELECT|UNION|OR|AND)\\b)" +
                    "|(;)", Pattern.CASE_INSENSITIVE);

    public static QueryType resolveQuery(String query) throws JSQLParserException, IllegalArgumentException {
        Statement stmt = CCJSqlParserUtil.parse(query);
        if (stmt instanceof Select select) {
            if (select instanceof SetOperationList setOperationList) {
                for (Select listSelects : setOperationList.getSelects()) {
                    if (isCountQuery(listSelects)) {
                        return QueryType.SELECT_COUNT;
                    }
                }
                return QueryType.SELECT;
            }
            if (isCountQuery(select)) {
                return QueryType.SELECT_COUNT;
            }
            return QueryType.SELECT;
        }
        if (stmt instanceof Insert)
            return QueryType.INSERT;
        if (stmt instanceof Update)
            return QueryType.UPDATE;
        if (stmt instanceof Delete)
            return QueryType.DELETE;
        log.error("Not a valid or supported query.");
        throw new IllegalArgumentException("Not a valid or supported query.");
    }

    private static boolean isCountQuery(Select select) {
        if (select instanceof PlainSelect plainSelect) {
            for (SelectItem<?> selectItem : plainSelect.getSelectItems()) {
                if (selectItem.getExpression() instanceof Function function) {
                    if (function.getName().equalsIgnoreCase("count")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean DEV_filterSELECT_OR_COUNT (String sqlScript) throws JSQLParserException {
        QueryType queryType = resolveQuery(sqlScript);
        return queryType == QueryType.SELECT || queryType == QueryType.SELECT_COUNT;
    }

    public static boolean validateAndLog (Configurazione configurazione) {
        if (configurazione == null) {
            log.error("Configurazione is null.");
            return false;
        }
        if (configurazione.getSqlScript() == null) {
            log.error("SqlScript is null.");
            return false;
        }
        if (configurazione.getSqlScript().isEmpty()) {
            log.error("SqlScript is empty.");
            return false;
        }
        if (SQL_INJECTION_PATTERN.matcher(configurazione.getSqlScript()).find()) {
            log.error("SqlScript contains SQL injection.");
            return false;
        }
        try {
            QueryType queryType = resolveQuery(configurazione.getSqlScript());
            if (queryType == QueryType.NOT_SUPPORTED) {
                log.error("Query type not supported.");
                return false;
            }
            return true;
        } catch (JSQLParserException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}
