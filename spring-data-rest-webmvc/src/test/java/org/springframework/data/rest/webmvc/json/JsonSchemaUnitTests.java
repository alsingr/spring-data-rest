/*
 * Copyright 2015 the original author or authors.
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
package org.springframework.data.rest.webmvc.json;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.data.rest.webmvc.json.JsonSchema.Property;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;

/**
 * Unit tests for {@link JsonSchema}.
 * 
 * @author Oliver Gierke
 */
public class JsonSchemaUnitTests {

	static final TypeInformation<?> type = ClassTypeInformation.from(Sample.class);

	/**
	 * @see DATAREST-492
	 */
	@Test
	public void considersNumberPrimitivesJsonSchemaNumbers() {

		Property property = new JsonSchema.Property("foo", "bar", false);

		assertThat(property.with(type.getProperty("foo")).type, is("number"));
	}

	static class Sample {
		double foo;
	}
}
