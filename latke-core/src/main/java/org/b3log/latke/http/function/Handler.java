/*
 * Copyright (c) 2009-present, b3log.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.latke.http.function;

import org.b3log.latke.http.RequestContext;

import java.io.Serializable;

/**
 * Represents an request handler that accepts a context as the single input argument and returns no result.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Dec 1, 2018
 * @since 2.4.30
 */
@FunctionalInterface
public interface Handler extends Serializable {

    /**
     * Performs request handling with the specified context.
     *
     * @param context the specified context
     */
    void handle(final RequestContext context);
}
