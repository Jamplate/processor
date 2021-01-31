/*
 *	Copyright 2021 Cufy
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *	    http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */
package org.jamplate.processor.maker;

import org.jamplate.model.reference.Reference;
import org.jamplate.model.sketch.Sketch;

import java.io.IOError;

/**
 * A function that takes a source and creates a sketch for it.
 *
 * @author LSafer
 * @version 0.2.0
 * @since 0.2.0 ~2021.01.30
 */
@FunctionalInterface
public interface Maker {
	/**
	 * Make a sketch from given {@code reference}.
	 *
	 * @param reference the reference of the sketch to be constructed.
	 * @return a new sketch from given {@code reference}.
	 * @throws NullPointerException     if the given {@code reference} is null.
	 * @throws IllegalArgumentException if the given {@code reference} is not satisfying
	 *                                  the conditions of this maker. (optional)
	 * @throws IllegalStateException    if the given {@code reference} is a deserialized
	 *                                  reference or has a deserialized document.
	 *                                  (optional)
	 * @throws IOError                  if any I/O error occurs.
	 * @since 0.2.0 ~2021.01.30
	 */
	Sketch make(Reference reference);
}
