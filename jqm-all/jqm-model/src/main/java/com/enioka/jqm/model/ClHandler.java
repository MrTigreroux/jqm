package com.enioka.jqm.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enioka.jqm.jdbc.DatabaseException;
import com.enioka.jqm.jdbc.DbConn;
import com.enioka.jqm.jdbc.QueryResult;

/**
 * <strong>Not part of any API - this an internal JQM class and may change without notice.</strong> <br>
 * Persistence class for storing the definition of the different event handlers hooked on a given class loader ({@link Cl}).
 */
public class ClHandler implements Serializable
{
    private static final long serialVersionUID = 5745009392739191779L;

    private Integer id;

    ClEvent eventType;

    String className;

    int classLoader;

    /**
     * A technical ID without any meaning. Generated by the database.
     */
    public Integer getId()
    {
        return id;
    }

    /**
     * See {@link #setId(Integer)}
     */
    public void setId(Integer id)
    {
        this.id = id;
    }

    /**
     * The type of event this is a hook for. Basically, the event the handler subscribes to.
     */
    public ClEvent getEventType()
    {
        return eventType;
    }

    /**
     * See {@link #getEventType()}
     */
    public void setEventType(ClEvent type)
    {
        this.eventType = type;
    }

    /**
     * The fully qualified name of the handler class. It must implement an interface (specific type depending on the event type).
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * See {@link #getClassName()}
     */
    public void setClassName(String className)
    {
        this.className = className;
    }

    private Map<String, String> getParameters(DbConn cnx)
    {
        return ClHandlerParameter.select_map(cnx, "cleh_select_all_for_cleh", this.id);
    }

    private Map<String, String> prmCache = new HashMap<String, String>();

    /**
     * A set of key/value pairs (without order) which are passed to the handler at runtime. The content of this dictionary is only used by
     * the handler, never by the engine.
     */
    public Map<String, String> getParameters()
    {
        return prmCache;
    }

    static ClHandler map(ResultSet rs, int colShift)
    {
        ClHandler tmp = new ClHandler();

        try
        {
            tmp.id = rs.getInt(1 + colShift);
            tmp.eventType = ClEvent.valueOf(rs.getString(2 + colShift));
            tmp.className = rs.getString(3 + colShift);
            tmp.classLoader = rs.getInt(4 + colShift);
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e);
        }
        return tmp;
    }

    public static List<ClHandler> select(DbConn cnx, String query_key, Object... args)
    {
        List<ClHandler> res = new ArrayList<ClHandler>();
        ResultSet rs = null;
        try
        {
            rs = cnx.runSelect(query_key, args);
            while (rs.next())
            {
                ClHandler tmp = map(rs, 0);
                res.add(tmp);
                tmp.prmCache = tmp.getParameters(cnx);
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e);
        }
        finally
        {
            cnx.closeQuietly(rs);
        }
        return res;
    }

    public static int create(DbConn cnx, ClEvent type, String className, int classloaderId, Map<String, String> parameters)
    {
        QueryResult r = cnx.runUpdate("cleh_insert", type, className, classloaderId);
        int newId = r.getGeneratedId();

        for (Map.Entry<String, String> prm : parameters.entrySet())
        {
            cnx.runUpdate("clehprm_insert", prm.getKey(), prm.getValue(), newId);
        }

        return newId;
    }
}
