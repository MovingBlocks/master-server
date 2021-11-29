/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.master;

import io.micronaut.test.annotation.MicronautTest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.terasology.web.db.DataBase;

import javax.inject.Inject;
import java.util.Map;

@MicronautTest
class JooqDbTest {

    @Inject
    DataBase db;

    @Test
    void testConnection() throws Exception {
        String tableName = "servers1";

        db.createTable(tableName);
        db.insert(tableName, "myName", "localhost", 25000, "Tester", true);

        Map<String, Object> data = db.readAll(tableName).get(0);

        Assert.assertEquals("myName", data.get("name"));
        Assert.assertEquals("Tester", data.get("owner"));
        Assert.assertEquals(25000, data.get("port"));
        Assert.assertEquals("localhost", data.get("address"));
        Assert.assertEquals(true, data.get("active"));
    }
}
