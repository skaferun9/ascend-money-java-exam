package com.ascendcorp.exam.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ItemTest {

    @Mock
    DataSource dataSource;

    @InjectMocks
    Item item;

    @Test
    public void testFindTheGreatestFromAllData() throws SQLException {
        Connection jdbcConnection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(jdbcConnection);

        Long countItem = item.countItem(2L);
        assertEquals(4, countItem.toString());
    }
}
