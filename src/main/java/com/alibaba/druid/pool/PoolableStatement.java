/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class PoolableStatement extends PoolableWrapper implements Statement {

    private final static Log        LOG            = LogFactory.getLog(PoolableStatement.class);

    private final Statement         stmt;
    protected PoolableConnection    conn;
    protected final List<ResultSet> resultSetTrace = new ArrayList<ResultSet>();
    protected boolean               closed         = false;
    protected int                   fetchRowPeak   = -1;

    public PoolableStatement(PoolableConnection conn, Statement stmt){
        super(stmt);

        this.conn = conn;
        this.stmt = stmt;
    }

    protected void recordFetchRowCount(int fetchRowCount) {
        if (fetchRowPeak < fetchRowCount) {
            fetchRowPeak = fetchRowCount;
        }
    }

    public int getFetchRowPeak() {
        return fetchRowPeak;
    }

    protected SQLException checkException(Throwable error) throws SQLException {
        return conn.handleException(error);
    }

    public PoolableConnection getPoolableConnection() {
        return conn;
    }

    public void setPoolableConnection(PoolableConnection conn) {
        this.conn = conn;
    }

    public Statement getStatement() {
        return stmt;
    }

    protected void checkOpen() throws SQLException {
        if (closed) {
            throw new SQLException("statement is closed");
        }
    }

    protected void clearResultSet() {
        for (ResultSet rs : resultSetTrace) {
            try {
                if (!rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException ex) {
                LOG.error("clearResultSet error", ex);
            }
        }
        resultSetTrace.clear();
    }

    public void incrementExecuteCount() {
        this.getPoolableConnection().getConnectionHolder().getDataSource().incrementExecuteCount();
    }

    @Override
    public final ResultSet executeQuery(String sql) throws SQLException {
        checkOpen();

        incrementExecuteCount();
        transactionRecord(sql);

        try {
            ResultSet rs = stmt.executeQuery(sql);

            PoolableResultSet poolableResultSet = new PoolableResultSet(this, rs);
            resultSetTrace.add(poolableResultSet);

            return poolableResultSet;
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    protected void transactionRecord(String sql) throws SQLException {
        conn.transactionRecord(sql);
    }

    @Override
    public final int executeUpdate(String sql) throws SQLException {
        checkOpen();

        incrementExecuteCount();
        transactionRecord(sql);

        try {
            return stmt.executeUpdate(sql);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void close() throws SQLException {
        if (!this.closed) {
            clearResultSet();
            stmt.close();
            this.closed = true;
            conn.getConnectionHolder().removeTrace(this);
        }
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        checkOpen();

        try {
            return stmt.getMaxFieldSize();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        checkOpen();

        try {
            stmt.setMaxFieldSize(max);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final int getMaxRows() throws SQLException {
        checkOpen();

        try {
            return stmt.getMaxRows();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        checkOpen();

        try {
            stmt.setMaxRows(max);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final void setEscapeProcessing(boolean enable) throws SQLException {
        checkOpen();

        try {
            stmt.setEscapeProcessing(enable);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final int getQueryTimeout() throws SQLException {
        checkOpen();

        try {
            return stmt.getQueryTimeout();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        checkOpen();

        try {
            stmt.setQueryTimeout(seconds);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final void cancel() throws SQLException {
        checkOpen();

        try {
            stmt.cancel();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final SQLWarning getWarnings() throws SQLException {
        checkOpen();

        try {
            return stmt.getWarnings();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final void clearWarnings() throws SQLException {
        checkOpen();

        try {
            stmt.clearWarnings();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final void setCursorName(String name) throws SQLException {
        checkOpen();

        try {
            stmt.setCursorName(name);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final boolean execute(String sql) throws SQLException {
        checkOpen();

        incrementExecuteCount();
        transactionRecord(sql);

        try {
            return stmt.execute(sql);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final ResultSet getResultSet() throws SQLException {
        checkOpen();

        try {
            ResultSet rs = stmt.getResultSet();

            PoolableResultSet poolableResultSet = new PoolableResultSet(this, rs);
            resultSetTrace.add(poolableResultSet);

            return poolableResultSet;
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final int getUpdateCount() throws SQLException {
        checkOpen();

        try {
            return stmt.getUpdateCount();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final boolean getMoreResults() throws SQLException {
        checkOpen();

        try {
            return stmt.getMoreResults();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        checkOpen();

        try {
            stmt.setFetchDirection(direction);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final int getFetchDirection() throws SQLException {
        checkOpen();

        try {
            return stmt.getFetchDirection();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        checkOpen();

        try {
            stmt.setFetchSize(rows);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final int getFetchSize() throws SQLException {
        checkOpen();

        try {
            return stmt.getFetchSize();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final int getResultSetConcurrency() throws SQLException {
        checkOpen();

        try {
            return stmt.getResultSetConcurrency();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final int getResultSetType() throws SQLException {
        checkOpen();

        try {
            return stmt.getResultSetType();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final void addBatch(String sql) throws SQLException {
        checkOpen();

        transactionRecord(sql);

        try {
            stmt.addBatch(sql);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final void clearBatch() throws SQLException {
        checkOpen();

        try {
            stmt.clearBatch();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public int[] executeBatch() throws SQLException {
        checkOpen();

        incrementExecuteCount();

        try {
            return stmt.executeBatch();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final Connection getConnection() throws SQLException {
        checkOpen();

        return conn;
    }

    @Override
    public final boolean getMoreResults(int current) throws SQLException {
        checkOpen();

        try {
            return stmt.getMoreResults(current);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final ResultSet getGeneratedKeys() throws SQLException {
        checkOpen();

        try {
            ResultSet rs = stmt.getGeneratedKeys();

            PoolableResultSet poolableResultSet = new PoolableResultSet(this, rs);

            resultSetTrace.add(poolableResultSet);

            return poolableResultSet;
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        checkOpen();

        incrementExecuteCount();
        transactionRecord(sql);

        try {
            return stmt.executeUpdate(sql, autoGeneratedKeys);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final int executeUpdate(String sql, int columnIndexes[]) throws SQLException {
        checkOpen();

        incrementExecuteCount();
        transactionRecord(sql);

        try {
            return stmt.executeUpdate(sql, columnIndexes);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final int executeUpdate(String sql, String columnNames[]) throws SQLException {
        checkOpen();

        incrementExecuteCount();
        transactionRecord(sql);

        try {
            return stmt.executeUpdate(sql, columnNames);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        checkOpen();

        incrementExecuteCount();
        transactionRecord(sql);

        try {
            return stmt.execute(sql, autoGeneratedKeys);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final boolean execute(String sql, int columnIndexes[]) throws SQLException {
        checkOpen();

        incrementExecuteCount();
        transactionRecord(sql);

        try {
            return stmt.execute(sql, columnIndexes);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final boolean execute(String sql, String columnNames[]) throws SQLException {
        checkOpen();

        incrementExecuteCount();
        transactionRecord(sql);

        try {
            return stmt.execute(sql, columnNames);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final int getResultSetHoldability() throws SQLException {
        checkOpen();

        try {
            return stmt.getResultSetHoldability();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public final void setPoolable(boolean poolable) throws SQLException {
        if (poolable) {
            return;
        }

        throw new SQLException("not support");
    }

    @Override
    public final boolean isPoolable() throws SQLException {
        return false;
    }

    public String toString() {
        return stmt.toString();
    }

    public void closeOnCompletion() throws SQLException {

    }

    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }
}
