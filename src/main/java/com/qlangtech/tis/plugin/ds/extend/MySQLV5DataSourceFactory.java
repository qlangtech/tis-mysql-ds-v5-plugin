/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qlangtech.tis.plugin.ds.extend;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import com.alibaba.citrus.turbine.Context;
import com.qlangtech.tis.annotation.Public;
import com.qlangtech.tis.db.parser.DBConfigParser;
import com.qlangtech.tis.extension.TISExtension;
import com.qlangtech.tis.lang.TisException;
import com.qlangtech.tis.plugin.annotation.FormField;
import com.qlangtech.tis.plugin.annotation.FormFieldType;
import com.qlangtech.tis.plugin.annotation.Validator;
import com.qlangtech.tis.plugin.ds.DBConfig;
import com.qlangtech.tis.plugin.ds.DataSourceFactory;
import com.qlangtech.tis.plugin.ds.JdbcUrlBuilder;
import com.qlangtech.tis.runtime.module.misc.IFieldErrorHandler;


@Public
public class MySQLV5DataSourceFactory extends DataSourceFactory {

    @FormField(ordinal = 1, type = FormFieldType.INPUTTEXT
            , validate = {Validator.require, Validator.hostWithoutPort})
    public String serverNode;

    @FormField(ordinal = 2, type = FormFieldType.INPUTTEXT
            , validate = {Validator.require, Validator.identity})
    public String dbName;

    @FormField(ordinal = 3, type = FormFieldType.INPUTTEXT
            , validate = {Validator.require, Validator.user_name})
    public String userName;
    @FormField(ordinal = 4, type = FormFieldType.PASSWORD
            , validate = {Validator.require, Validator.none_blank})
    public String password;

    @FormField(ordinal = 3, type = FormFieldType.INPUTTEXT
            , validate = {Validator.require, Validator.integer})
    public Integer port;

    private transient com.mysql.jdbc.Driver driver;

    @Override
    public DBConfig getDbConfig() {
        final DBConfig dbConfig = new DBConfig(new JdbcUrlBuilder() {
            @Override
            public String buidJdbcUrl(DBConfig db, String ip, String dbName) {
                return ("jdbc:mysql://" + ip + ":" + port + "/" + dbName);
            }
        });
        dbConfig.setName(dbName);
        dbConfig.setDbEnum(DBConfigParser.parseDBEnum(this.dbName, this.serverNode));
        return dbConfig;
    }


    @Override
    public JDBCConnection getConnection(String jdbcUrl) throws SQLException {
        if (driver == null) {
            driver = new com.mysql.jdbc.Driver();
        }
        java.util.Properties info = new java.util.Properties();

        if (this.userName != null) {
            info.put("user", this.userName);
        }
        if (password != null) {
            info.put("password", password);
        }
        return new JDBCConnection(driver.connect(jdbcUrl, info), jdbcUrl);
    }


    @Override
    public void visitFirstConnection(IConnProcessor connProcessor) {
        try {
            final DBConfig dbConfig = getDbConfig();
            dbConfig.vistDbName((config, jdbcUrl, ip, databaseName) -> {
                try (JDBCConnection conn = getConnection(jdbcUrl)) {
                    connProcessor.vist(conn);
                }
                return true;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void refresh() {

    }

    @TISExtension
    public static class DefaultDescriptor extends BaseDataSourceFactoryDescriptor {

        public boolean validatePassword(IFieldErrorHandler msgHandler, Context context, String fieldName, String value) {

            Pattern p1 = Pattern.compile("[A-Z]+");
            Pattern p2 = Pattern.compile("[a-z]+");
            Pattern p3 = Pattern.compile("[0-9]+");
            if (!(p1.matcher(value).find() && p2.matcher(value).find() && p3.matcher(value).find())) {
                msgHandler.addFieldError(context, fieldName, "需要同时有大写、小写、和数字");
                return false;
            }
            return true;
        }

        @Override
        protected String getDataSourceName() {
            return "MySQL DataSource";
        }

        @Override
        protected boolean supportFacade() {
            return false;
        }

        @Override
        protected void validateConnection(JDBCConnection conn) throws TisException {
            try {
                try (Statement statement = conn.getConnection().createStatement()) {
                    try (ResultSet result = statement.executeQuery("select 1")) {
                        result.next();
                        result.getInt(1);
                    }
                }
            } catch (SQLException e) {
                throw TisException.create(e.getMessage(), e);
            }
        }

        @Override
        public EndType getEndType() {
            return EndType.MySQL;
        }
    }
}
