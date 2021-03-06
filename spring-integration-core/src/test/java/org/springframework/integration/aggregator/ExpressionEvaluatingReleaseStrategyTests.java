/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.aggregator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.integration.store.SimpleMessageGroup;

/**
 * @author Alex Peters
 * @author Dave Syer
 * @author Gary Russell
 * @author Artem Bilan
 *
 */
public class ExpressionEvaluatingReleaseStrategyTests {

	private ExpressionEvaluatingReleaseStrategy strategy;

	private final SimpleMessageGroup messages = new SimpleMessageGroup("foo");

	@Before
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setup() {
		for (int i = 0; i < 5; i++) {
			messages.add(new GenericMessage(i + 1));
		}
	}

	@Test
	public void testCompletedWithSizeSpelEvaluated() throws Exception {
		strategy = new ExpressionEvaluatingReleaseStrategy("#root.size()==5");
		strategy.setBeanFactory(mock(BeanFactory.class));
		assertThat(strategy.canRelease(messages), is(true));
	}

	@Test
	public void testCompletedWithFilterSpelEvaluated() throws Exception {
		strategy = new ExpressionEvaluatingReleaseStrategy("!messages.?[payload==5].empty");
		strategy.setBeanFactory(mock(BeanFactory.class));
		assertThat(strategy.canRelease(messages), is(true));
	}

	@Test
	public void testCompletedWithFilterSpelReturnsNotCompleted() throws Exception {
		strategy = new ExpressionEvaluatingReleaseStrategy("!messages.?[payload==6].empty");
		strategy.setBeanFactory(mock(BeanFactory.class));
		assertThat(strategy.canRelease(messages), is(false));
	}

}
