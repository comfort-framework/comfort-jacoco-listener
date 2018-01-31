/*
 * Copyright (C) 2017 University of Goettingen, Germany
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

package de.ugoe.cs.comfort.listener;

import org.jacoco.agent.rt.IAgent;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mockito;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Fabian Trautsch
 * Based on: https://github.com/SonarSource/sonar-java/blob/master/sonar-jacoco-listeners/src/test/java/org/sonar/java/jacoco/JacocoControllerTest.java
 */
public class JacocoControllerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private IAgent agent;
    private JacocoController jacoco;

    @Before
    public void setUp() {
        agent = mock(IAgent.class);
        jacoco = new JacocoController(agent);
    }

    @Test
    public void test_onStart() throws Exception {
        jacoco.onTestStart();
        InOrder inOrder = Mockito.inOrder(agent);
        inOrder.verify(agent).setSessionId("");
        inOrder.verify(agent).dump(true);
        verifyNoMoreInteractions(agent);
    }

    @Test
    public void test_onFinish() throws Exception {
        when(agent.getExecutionData(false)).thenReturn(new byte[] {});
        jacoco.onTestFinish("test");
        InOrder inOrder = Mockito.inOrder(agent);
        inOrder.verify(agent).setSessionId("test");
        inOrder.verify(agent).dump(true);
        verifyNoMoreInteractions(agent);
    }

    @Test
    public void should_throw_exception_when_dump_failed() throws Exception {
        doThrow(IOException.class).when(agent).dump(anyBoolean());
        thrown.expect(JacocoController.JacocoControllerError.class);
        jacoco.onTestFinish("test");
    }

    @Test
    public void should_throw_exception_when_two_tests_started_in_parallel() {
        jacoco.onTestStart();
        thrown.expect(JacocoController.JacocoControllerError.class);
        thrown.expectMessage("Looks like several tests executed in parallel in the same JVM, thus coverage per test can't be recorded correctly.");
        jacoco.onTestStart();
    }

}
