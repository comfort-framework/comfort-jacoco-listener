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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.mockito.InOrder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

/**
 * @author Fabian Trautsch
 * Based on: https://github.com/SonarSource/sonar-java/blob/master/sonar-jacoco-listeners/src/main/java/org/sonar/java/jacoco/JUnitListener.java
 */
public class JUnitListenerTest {

    public static class Success {
        @org.junit.Test
        public void test() {
        }
    }

    public static class Failure {
        @org.junit.Test
        public void test() {
            org.junit.Assert.fail();
        }
    }

    private JacocoController jacoco;
    private JUnitListener listener;

    @Before
    public void setUp() {
        jacoco = mock(JacocoController.class);
        JacocoController.singleton = jacoco;
        listener = new JUnitListener();
        listener.jacoco = jacoco;
    }

    @Test
    public void should_have_public_no_arg_constructor() throws Exception {
        JUnitListener.class.getConstructor();
    }

    @Test
    public void lazy_initialization_of_controller() throws Exception {
        JUnitListener jUnitListener = new JUnitListener();
        assertNull(jUnitListener.jacoco);
        assertEquals(jacoco, jUnitListener.getJacocoController());
        jUnitListener.jacoco = jacoco;
        assertEquals(jUnitListener.jacoco, jUnitListener.getJacocoController());
    }

    @Test
    public void test_success() {
        execute(Success.class);
        String testName = getClass().getCanonicalName() + "$Success%%test";
        InOrder orderedExecution = inOrder(jacoco);
        orderedExecution.verify(jacoco).onTestStart();
        orderedExecution.verify(jacoco).onTestFinish(testName);
    }

    @Test
    public void test_failure() {
        execute(Failure.class);
        String testName = getClass().getCanonicalName() + "$Failure%%test";
        InOrder orderedExecution = inOrder(jacoco);
        orderedExecution.verify(jacoco).onTestStart();
        orderedExecution.verify(jacoco).onTestFinish(testName);
    }

    private void execute(Class cls) {
        JUnitCore junit = new JUnitCore();
        junit.addListener(listener);
        junit.run(cls);
    }

}

