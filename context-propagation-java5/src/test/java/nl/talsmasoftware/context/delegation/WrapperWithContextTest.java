/*
 * Copyright 2016-2017 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.talsmasoftware.context.delegation;

import nl.talsmasoftware.context.ContextSnapshot;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * @author Sjoerd Talsma
 */
public class WrapperWithContextTest {

    @Test
    public void testCreateWrapperWithoutDelegate() {
        WrapperWithContext<Object> wrapper = new WrapperWithContext<Object>(mock(ContextSnapshot.class), null) {
        };
        assertThat(wrapper, is(notNullValue()));
        assertThat(wrapper.delegate(), is(nullValue()));
    }

    @Test(expected = NullPointerException.class)
    public void testCreateWrapperWithoutContext() {
        new WrapperWithContext<Object>(null, mock(Wrapper.class)) {
        };
        fail("Exception expected.");
    }

    @Test
    public void testEqualsHashcode() {
        Set<WrapperWithContext<Object>> set = new LinkedHashSet<WrapperWithContext<Object>>();
        ContextSnapshot snapshot = mock(ContextSnapshot.class);
        Wrapper<Object> delegate = mock(Wrapper.class);

        WrapperWithContext<Object> wrapper = new WrapperWithContext<Object>(snapshot, delegate) {
        };
        assertThat(set.add(wrapper), is(true));
        assertThat(set.add(wrapper), is(false));
        assertThat(set.add(new WrapperWithContext<Object>(mock(ContextSnapshot.class), delegate) {
        }), is(true));
        assertThat(set.add(new WrapperWithContext<Object>(snapshot, mock(Wrapper.class)) {
        }), is(true));
        assertThat(set, hasSize(3));
    }

    @Test
    public void testToString() {
        ContextSnapshot snapshot = mock(ContextSnapshot.class);
        Wrapper<Object> delegate = mock(Wrapper.class);
        WrapperWithContext<Object> wrapper = new WrapperWithContext<Object>(snapshot, delegate) {
        };

        assertThat(wrapper, hasToString(containsString(snapshot.toString())));
        assertThat(wrapper, hasToString(containsString(wrapper.toString())));
    }
}