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
import org.junit.runner.Description;
import org.mockito.InOrder;
import org.testng.ITestClass;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestNG;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Fabian Trautsch
 * Based on: https://github.com/SonarSource/sonar-java/blob/master/sonar-jacoco-listeners/src/test/java/org/sonar/java/jacoco/TestNGListenerTest.java
 */
public class TestNGListenerTest {

    public static class Success {
        @org.testng.annotations.Test
        public void test() {
        }
    }

    public static class Failure {
        @org.testng.annotations.Test
        public void test() {
            org.testng.Assert.fail();
        }
    }

    public static class Skip {
        @org.testng.annotations.Test
        public void test() {
            throw new org.testng.SkipException("Skip me");
        }
    }

    private JacocoController jacoco;
    private TestNGListener listener;

    @Before
    public void setUp() {
        jacoco = mock(JacocoController.class);
        listener = new TestNGListener();
        listener.jacoco = jacoco;
    }

    @Test
    public void should_have_public_no_arg_constructor() throws Exception {
        TestNGListener.class.getConstructor();
    }

    @Test
    public void lazy_initialization_of_controller() throws Exception {
        TestNGListener testNGListener = new TestNGListener();
        assertNull(testNGListener.jacoco);
        testNGListener.jacoco = jacoco;
        assertEquals(testNGListener.jacoco, testNGListener.getJacocoController());
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

    @Test
    public void test_skip() {
        execute(Skip.class);
        String testName = getClass().getCanonicalName() + "$Skip%%test";
        InOrder orderedExecution = inOrder(jacoco);
        orderedExecution.verify(jacoco).onTestStart();
        orderedExecution.verify(jacoco).onTestFinish(testName);
    }

    private void execute(Class cls) {
        TestNG testNg = new TestNG(false);
        testNg.addListener(listener);
        testNg.setTestClasses(new Class[] {cls});
        testNg.run();
    }

    // JUnit

    @Test
    public void testStarted() {
        listener.testStarted(mockDescription());
        verify(jacoco).onTestStart();
    }

    @Test
    public void testFinished() {
        listener.testFinished(mockDescription());
        verify(jacoco).onTestFinish("class%%method");
    }

    // TestNG

    @Test
    public void onTestStart() {
        listener.onTestStart(mockTestResult());
        verify(jacoco).onTestStart();
    }

    @Test
    public void onTestSuccess() {
        listener.onTestSuccess(mockTestResult());
        verify(jacoco).onTestFinish("class%%method");
    }

    @Test
    public void onTestFailure() {
        listener.onTestFailure(mockTestResult());
        verify(jacoco).onTestFinish("class%%method");
    }

    @Test
    public void onTestSkipped() {
        listener.onTestSkipped(mockTestResult());
        verify(jacoco).onTestFinish("class%%method");
    }

    @Test
    public void onTestFailedButWithinSuccessPercentage() {
        listener.onTestFailedButWithinSuccessPercentage(mockTestResult());
        verify(jacoco).onTestFinish("class%%method");
    }

    private ITestResult mockTestResult() {
        ITestResult testResult = mock(ITestResult.class);
        ITestClass testClass = mock(ITestClass.class);
        when(testResult.getTestClass()).thenReturn(testClass);
        ITestNGMethod testMethod = mock(ITestNGMethod.class);
        when(testResult.getMethod()).thenReturn(testMethod);
        when(testClass.getName()).thenReturn("class");
        when(testMethod.getMethodName()).thenReturn("method");
        return testResult;
    }

    private Description mockDescription() {
        Description description = mock(Description.class);
        when(description.getClassName()).thenReturn("class");
        when(description.getMethodName()).thenReturn("method");
        return description;
    }

}
