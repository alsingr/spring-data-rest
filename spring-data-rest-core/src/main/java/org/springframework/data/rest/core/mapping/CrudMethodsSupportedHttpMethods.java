/*
 * Copyright 2014 the original author or authors.
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
package org.springframework.data.rest.core.mapping;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.repository.core.CrudMethods;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

/**
 * {@link SupportedHttpMethods} that are determined by a {@link CrudMethods} instance.
 * 
 * @author Oliver Gierke
 * @since 2.3
 */
public class CrudMethodsSupportedHttpMethods implements SupportedHttpMethods {

	private final ExposureAwareCrudMethods exposedMethods;

	/**
	 * Creates a new {@link CrudMethodsSupportedHttpMethods} for the given {@link CrudMethods}.
	 * 
	 * @param crudMethods must not be {@literal null}.
	 */
	public CrudMethodsSupportedHttpMethods(CrudMethods crudMethods) {

		Assert.notNull(crudMethods, "CrudMethods must not be null!");

		this.exposedMethods = new DefaultExposureAwareCrudMethods(crudMethods);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.rest.core.mapping.SupportedHttpMethods#supports(org.springframework.http.HttpMethod, org.springframework.data.rest.core.mapping.ResourceType)
	 */
	@Override
	public boolean supports(HttpMethod method, ResourceType type) {

		Assert.notNull(method, "HTTP method must not be null!");
		Assert.notNull(type, "Resource type must not be null!");

		return getMethodsFor(type).contains(method);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.rest.core.mapping.SupportedHttpMethods#getSupportedHttpMethods(org.springframework.data.rest.core.mapping.ResourceType)
	 */
	@Override
	public Set<HttpMethod> getMethodsFor(ResourceType resourcType) {

		Assert.notNull(resourcType, "Resource type must not be null!");

		Set<HttpMethod> methods = new HashSet<HttpMethod>();
		methods.add(HttpMethod.OPTIONS);

		switch (resourcType) {
			case COLLECTION:

				if (exposedMethods.exposesFindAll()) {
					methods.add(HttpMethod.GET);
					methods.add(HttpMethod.HEAD);
				}

				if (exposedMethods.exposesSave()) {
					methods.add(HttpMethod.POST);
				}

				break;

			case ITEM:

				if (exposedMethods.exposesDelete() && exposedMethods.exposesFindOne()) {
					methods.add(HttpMethod.DELETE);
				}

				if (exposedMethods.exposesFindOne()) {
					methods.add(HttpMethod.GET);
					methods.add(HttpMethod.HEAD);
				}

				if (exposedMethods.exposesSave()) {
					methods.add(HttpMethod.PUT);
					methods.add(HttpMethod.PATCH);
				}

				break;

			default:
				throw new IllegalArgumentException(String.format("Unsupported resource type %s!", resourcType));
		}

		return Collections.unmodifiableSet(methods);
	}

	/**
	 * @author Oliver Gierke
	 */
	private static class DefaultExposureAwareCrudMethods implements ExposureAwareCrudMethods {

		private final CrudMethods crudMethods;

		/**
		 * @param exposedMethods
		 */
		public DefaultExposureAwareCrudMethods(CrudMethods crudMethods) {
			this.crudMethods = crudMethods;
		}

		/* 
		 * (non-Javadoc)
		 * @see org.springframework.data.rest.core.mapping.ExposureAwareCrudMethods#exposesSave()
		 */
		@Override
		public boolean exposesSave() {
			return exposes(crudMethods.getSaveMethod());
		}

		/* 
		 * (non-Javadoc)
		 * @see org.springframework.data.rest.core.mapping.ExposureAwareCrudMethods#exposesDelete()
		 */
		@Override
		public boolean exposesDelete() {
			return exposes(crudMethods.getDeleteMethod());
		}

		/* 
		 * (non-Javadoc)
		 * @see org.springframework.data.rest.core.mapping.ExposureAwareCrudMethods#exposesFindOne()
		 */
		@Override
		public boolean exposesFindOne() {
			return exposes(crudMethods.getFindOneMethod());
		}

		/* 
		 * (non-Javadoc)
		 * @see org.springframework.data.rest.core.mapping.ExposureAwareCrudMethods#exposesFindAll()
		 */
		@Override
		public boolean exposesFindAll() {
			return exposes(crudMethods.getFindAllMethod());
		}

		private static boolean exposes(Method method) {

			if (method == null) {
				return false;
			}

			RestResource annotation = AnnotationUtils.findAnnotation(method, RestResource.class);
			return annotation == null ? true : annotation.exported();
		}
	}

	/**
	 * @author Oliver Gierke
	 */
	interface ExposureAwareCrudMethods {

		/**
		 * Returns whether the repository exposes the save method.
		 * 
		 * @return
		 */
		boolean exposesSave();

		/**
		 * Returns whether the repository exposes the delete method.
		 * 
		 * @return
		 */
		boolean exposesDelete();

		/**
		 * Returns whether the repository exposes the method to find a single object.
		 * 
		 * @return
		 */
		boolean exposesFindOne();

		/**
		 * Returns whether the repository exposes the method to find all objects.
		 * 
		 * @return
		 */
		boolean exposesFindAll();
	}
}
