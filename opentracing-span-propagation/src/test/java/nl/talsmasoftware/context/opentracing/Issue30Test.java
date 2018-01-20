/*
 * Copyright 2016-2018 Talsma ICT
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
package nl.talsmasoftware.context.opentracing;

import io.opentracing.ScopeManager;
import io.opentracing.mock.MockTracer;
import io.opentracing.util.GlobalTracer;
import io.opentracing.util.GlobalTracerTestUtil;
import io.opentracing.util.ThreadLocalScopeManager;
import nl.talsmasoftware.context.ContextManagers;
import nl.talsmasoftware.context.ContextSnapshot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * <a href="https://github.com/talsma-ict/context-propagation/issues/30">Issue 30</a> is for a
 * possible {@code NullPointerException} in {@code ScopeContext.close()}.
 * <p>
 * <a href="https://github.com/hanson76">hanson76</a> wrote:
 * <blockquote>
 * span can be {@code null} in {@code ScopeContext} if {@code OpentracingSpanManager} is used with
 * ContextAware* when there is no active span.<br>
 * The problem is that {@code ContextManagers.createContextSnapshot()} only stores
 * {@code activeContext.getValue()} which is {@code null}<br>
 * {@code ContextManagers.reactivate()} then retreives {@code null} from the snapshot and
 * calls {@code OpentracingSpanManger.initializeNewContext(null)}
 * <p>
 * The test in ScopeContext.close only checks that closed is false before calling span.close.
 * <p>
 * The fix could be to set closed to true in initializeNewContext() if span is null,
 * or add a nullcheck in SpanContext.close.
 * </blockquote>
 *
 * @author Sjoerd Talsma
 */
public class Issue30Test {
    static final ScopeManager SCOPE_MANAGER = new ThreadLocalScopeManager();

    MockTracer mockTracer;

    @Before
    public void registerMockGlobalTracer() {
        GlobalTracerTestUtil.resetGlobalTracer();
        assertThat("Pre-existing GlobalTracer", GlobalTracer.isRegistered(), is(false));
        GlobalTracer.register(mockTracer = new MockTracer(SCOPE_MANAGER));
    }

    @After
    public void cleanup() {
        GlobalTracerTestUtil.resetGlobalTracer();
    }

    @Test
    public void testIssue30NullPointerException() throws IOException {
        ContextSnapshot snapshot = ContextManagers.createContextSnapshot();
        Closeable reactivation = snapshot.reactivate();
        reactivation.close(); // This throws NPE in issue 30
    }

}
