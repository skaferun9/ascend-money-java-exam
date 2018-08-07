package com.ascendcorp.exam.repository;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;


public class Item {

    final static Logger log = Logger.getLogger(Item.class);

    @Autowired
    private DataSource dataSource;

    @NonNull
    private final Environment environment;

    @Autowired
    public Item(Environment environment) {
        this.environment = environment;
    }

    public Long countItem(Long type) {
        Long count = 0L;
        Connection connection = null;
        CallableStatement callableStatement = null;
        String countItem = "CALL count_item(?,?)";
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            callableStatement = connection.prepareCall(countItem);
            callableStatement.setLong(1, type);
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.executeQuery();
            count = callableStatement.getLong(2);
            connection.commit();
        } catch (Exception e) {
            log.error("Error ccccc : " + e);
        } finally {
            try {
                if (callableStatement != null) {
                    callableStatement.close();
                }
                connection.close();
            }catch (SQLException e){

            }
        }
        log.info("Total get item : " + count);
        return count;

    }
}
