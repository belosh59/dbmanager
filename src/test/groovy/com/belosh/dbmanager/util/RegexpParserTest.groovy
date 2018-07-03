package com.belosh.dbmanager.util

import org.junit.Assert
import org.junit.Test

class RegexpParserTest {

    @Test
    void testSplitSemicolon() {
        String sql = "select * from users;\n   select genre.id + '.,;' from genre;select * from t;       "

        String[] sqls = RegexpParser.splitSemicolon(sql)

        print(sqls)

        Assert.assertEquals("select * from users", sqls[0])
        Assert.assertEquals("select genre.id + '.,;' from genre", sqls[1])
        Assert.assertEquals("select * from t", sqls[2])
        Assert.assertEquals(3, sqls.size())
    }
}
