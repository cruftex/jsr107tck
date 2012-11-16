/**
 *  Copyright 2011 Terracotta, Inc.
 *  Copyright 2011 Oracle, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jsr107.tck;

import org.jsr107.tck.util.ExcludeListExcluder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import javax.cache.*;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Cache implementations must support storeByValue.
 * <p/>
 * Tests aspects where storeByValue makes a difference
 *
 * @author Yannis Cosmadopoulos
 * @author Greg Luck
 * @since 1.0
 */
public class StoreByValueTest extends CacheTestSupport<Date, Date> {
    /**
     * Rule used to exclude tests
     */
    @Rule
    public MethodRule rule = new ExcludeListExcluder(this.getClass());

    @Before
    public void setUp() {
        super.setUp();
    }

    @After
    public void teardown() {
        getCacheManager().removeCache(getTestCacheName());
        try {
            Caching.close();
        } catch (CachingShutdownException e) {
            //expected
        }
    }


    @Test
    public void get_Existing_MutateValue() {
        long now = System.currentTimeMillis();
        Date existingKey = new Date(now);
        Date existingValue = new Date(now);
        cache.put(existingKey, existingValue);
        existingValue.setTime(now + 1);
        assertEquals(new Date(now), cache.get(existingKey));
    }

    @Test
    public void get_Existing_MutateKey() {
        long now = System.currentTimeMillis();
        Date existingKey = new Date(now);
        Date existingValue = new Date(now);
        cache.put(existingKey, existingValue);
        existingKey.setTime(now + 1);
        assertEquals(new Date(now), cache.get(new Date(now)));
    }

    @Test
    public void getAndPut_NotThere() {
        if (cache == null) return;

        long now = System.currentTimeMillis();
        Date existingKey = new Date(now);
        Date existingValue = new Date(now);
        assertNull(cache.getAndPut(existingKey, existingValue));
        existingValue.setTime(now + 1);
        assertEquals(new Date(now), cache.get(existingKey));
    }

    @Test
    public void getAndPut_Existing_MutateValue() {
        long now = System.currentTimeMillis();
        Date existingKey = new Date(now);
        Date value1 = new Date(now);
        cache.getAndPut(existingKey, value1);
        Date value2 = new Date(now + 1);
        value1.setTime(now + 2);
        assertEquals(new Date(now), cache.getAndPut(existingKey, value2));
        value2.setTime(now + 3);
        assertEquals(new Date(now + 1), cache.get(existingKey));
    }

    @Test
    public void getAndPut_Existing_NonSameKey_MutateValue() throws Exception {
        long now = System.currentTimeMillis();
        Date key1 = new Date(now);
        Date value1 = new Date(now);
        cache.getAndPut(key1, value1);
        value1.setTime(now + 1);
        Date key2 = new Date(now);
        Date value2 = new Date(now + 2);
        assertEquals(new Date(now), cache.getAndPut(key2, value2));
        value2.setTime(now + 3);
        assertEquals(new Date(now + 2), cache.get(key1));
        assertEquals(new Date(now + 2), cache.get(key2));
    }

    @Test
    public void getAndPut_Existing_NonSameKey_MutateKey() {
        long now = System.currentTimeMillis();
        Date key1 = new Date(now);
        Date value1 = new Date(now);
        cache.getAndPut(key1, value1);
        key1.setTime(now + 1);
        Date key2 = new Date(now);
        Date value2 = new Date(now + 2);
        assertEquals(new Date(now), cache.getAndPut(key2, value2));
        assertEquals(new Date(now + 2), cache.get(key2));
    }
}