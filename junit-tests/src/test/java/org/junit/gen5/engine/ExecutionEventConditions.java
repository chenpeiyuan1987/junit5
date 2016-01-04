/*
 * Copyright 2015-2016 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.junit.gen5.engine;

import static java.util.function.Predicate.isEqual;
import static org.assertj.core.api.Assertions.allOf;
import static org.junit.gen5.commons.util.FunctionUtils.where;
import static org.junit.gen5.engine.ExecutionEvent.*;
import static org.junit.gen5.engine.ExecutionEvent.Type.*;
import static org.junit.gen5.engine.TestExecutionResult.Status.*;
import static org.junit.gen5.engine.TestExecutionResultConditions.status;

import org.assertj.core.api.Condition;
import org.junit.gen5.engine.ExecutionEvent.Type;

/**
 * Collection of AssertJ conditions for {@link ExecutionEvent}.
 */
public class ExecutionEventConditions {

	public static Condition<ExecutionEvent> engine() {
		return new Condition<>(byTestDescriptor(EngineAwareTestDescriptor.class::isInstance), "is an engine");
	}

	public static Condition<ExecutionEvent> test(String uniqueIdSubstring) {
		return allOf(test(), uniqueIdSubstring(uniqueIdSubstring));
	}

	public static Condition<ExecutionEvent> test() {
		return new Condition<>(byTestDescriptor(TestDescriptor::isTest), "is a test");
	}

	public static Condition<ExecutionEvent> container(String uniqueIdSubstring) {
		return allOf(container(), uniqueIdSubstring(uniqueIdSubstring));
	}

	public static Condition<ExecutionEvent> container() {
		return new Condition<>(byTestDescriptor(TestDescriptor::isContainer), "is a container");
	}

	public static Condition<ExecutionEvent> uniqueIdSubstring(String uniqueIdSubstring) {
		return new Condition<>(
			byTestDescriptor(where(TestDescriptor::getUniqueId, uniqueId -> uniqueId.contains(uniqueIdSubstring))),
			"has descriptor with uniqueId substring '%s'", uniqueIdSubstring);
	}

	public static Condition<ExecutionEvent> skippedWithReason(String expectedReason) {
		return allOf(type(SKIPPED), reason(expectedReason));
	}

	public static Condition<ExecutionEvent> started() {
		return type(STARTED);
	}

	public static Condition<ExecutionEvent> abortedWithReason(Condition<TestExecutionResult> resultCondition) {
		return finished(allOf(status(ABORTED), resultCondition));
	}

	public static Condition<ExecutionEvent> finishedWithFailure(Condition<TestExecutionResult> resultCondition) {
		return finished(allOf(status(FAILED), resultCondition));
	}

	public static Condition<ExecutionEvent> finishedSuccessfully() {
		return finished(status(SUCCESSFUL));
	}

	public static Condition<ExecutionEvent> finished(Condition<TestExecutionResult> resultCondition) {
		return allOf(type(FINISHED), result(resultCondition));
	}

	public static Condition<ExecutionEvent> type(Type expectedType) {
		return new Condition<>(byType(expectedType), "type is %s", expectedType);
	}

	public static Condition<ExecutionEvent> result(Condition<TestExecutionResult> condition) {
		return new Condition<>(byPayload(TestExecutionResult.class, condition::matches), "event with result where %s",
			condition);
	}

	public static Condition<ExecutionEvent> reason(String expectedReason) {
		return new Condition<>(byPayload(String.class, isEqual(expectedReason)), "event with reason '%s'",
			expectedReason);
	}

}
