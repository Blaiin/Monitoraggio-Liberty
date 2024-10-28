package it.dmi.processors.jobs;

import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
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
        if(query == null || query.isEmpty()) throw new IllegalArgumentException("Query null or empty.");
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

    public static boolean acceptSelectOrCount(Configurazione c) {
        try {
            return DEV_filterSELECT_OR_COUNT(c.getSqlScript());
        } catch (JSQLParserException e) {
            log.error("Could not resolve query to a supported type.");
            throw new RuntimeException(e);
        }
    }

    public static boolean acceptCount(Configurazione c) {
        try {
            return DEV_filterCOUNT(c.getSqlScript());
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean DEV_filterSELECT_OR_COUNT (String sqlScript) throws JSQLParserException {
        QueryType queryType = resolveQuery(sqlScript);
        return queryType == QueryType.SELECT || queryType == QueryType.SELECT_COUNT;
    }

    public static boolean DEV_filterCOUNT(String script) throws JSQLParserException {
        QueryType type = resolveQuery(script);
        return type == QueryType.SELECT_COUNT;
    }

    public static boolean validateAndLog(QuartzTask task) {
        switch (task) {
            case Configurazione c -> {
                if (c.getSqlScript() == null || c.getSqlScript().isEmpty()) {
                    log.error("Configurazione script(sql) is null or empty.");
                    return false;
                }
                if (SQL_INJECTION_PATTERN.matcher(c.getSqlScript()).find()) {
                    log.error("Configurazione script(sql) contains SQL injection.");
                    return false;
                }
                try {
                    QueryType queryType = resolveQuery(c.getSqlScript());
                    if (queryType == QueryType.NOT_SUPPORTED) {
                        log.error("Configurazione query type not supported.");
                        return false;
                    }
                    return true;
                } catch (JSQLParserException | IllegalArgumentException e) {
                    throw new RuntimeException(e);
                }
            }
            case Azione a -> {
                if (a.getSqlScript() == null || a.getSqlScript().isEmpty()) {
                    log.error("Azione script(sql) is null or empty.");
                    return false;
                }
                if (SQL_INJECTION_PATTERN.matcher(a.getSqlScript()).find()) {
                    log.error("Azione script(sql) contains SQL injection.");
                    return false;
                }
                try {
                    QueryType queryType = resolveQuery(a.getSqlScript());
                    if (queryType == QueryType.NOT_SUPPORTED) {
                        log.error("Azione query type not supported.");
                        return false;
                    }
                    return true;
                } catch (JSQLParserException | IllegalArgumentException e) {
                    throw new RuntimeException(e);
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + task);
        }
    }
}
