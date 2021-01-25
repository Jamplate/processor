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
package org.jamplate.source.sketch;

import org.jamplate.source.reference.Reference;

import java.util.Objects;

/**
 * An abstract for the interface {@link Sketch} implementing the very basic functionality
 * of a sketch.
 *
 * @author LSafer
 * @version 0.2.0
 * @since 0.2.0 ~2021.01.17
 */
public abstract class AbstractSketch implements Sketch {
	@SuppressWarnings("JavaDoc")
	private static final long serialVersionUID = 7365834181285411955L;

	/**
	 * The source reference of this sketch. (the reference this sketch is reserving)
	 *
	 * @since 0.2.0 ~2021.01.12
	 */
	protected final Reference reference;

	/**
	 * True, if this sketch have been constructed using its constructor. (in other words
	 * 'not deserialized')
	 *
	 * @since 0.2.0 ~2021.01.17
	 */
	@SuppressWarnings("TransientFieldNotInitialized")
	protected final transient boolean constructed;

	/**
	 * Construct a new sketch with the given {@code reference}. The given source reference
	 * is the reference the constructed sketch will reserve.
	 *
	 * @param reference the source reference of the constructed sketch.
	 * @throws NullPointerException if the given {@code reference} is null.
	 * @since 0.2.0 ~2021.01.17
	 */
	protected AbstractSketch(Reference reference) {
		Objects.requireNonNull(reference, "reference");
		this.reference = reference;
		this.constructed = true;
	}

	@Override
	public boolean equals(Object object) {
		return this == object;
	}

	@Override
	public int hashCode() {
		return this.reference.hashCode() + this.getClass().hashCode();
	}

	@Override
	public Reference reference() {
		return this.reference;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " (" + this.reference + ")";
	}
}
