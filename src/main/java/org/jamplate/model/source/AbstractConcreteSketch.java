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
package org.jamplate.model.source;

import java.util.regex.Pattern;

/**
 * An abstract of the interface {@link Sketch} that implements the basic functionality of
 * a concrete sketch. (a concrete sketch is a sketch that cannot have inner sketches)
 *
 * @author LSafer
 * @version 0.2.0
 * @since 0.2.0 ~2021.01.17
 */
public abstract class AbstractConcreteSketch extends AbstractSketch {
	/**
	 * Construct a new sketch for the given {@code source}. The given source is the source
	 * the constructed sketch will reserve.
	 *
	 * @param source the source of the constructed sketch.
	 * @throws NullPointerException if the given {@code source} is null.
	 * @since 0.2.0 ~2021.01.17
	 */
	protected AbstractConcreteSketch(Source source) {
		super(source);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public Source find(Pattern pattern) {
		return null;
	}

	@Override
	public Source find(Pattern startPattern, Pattern endPattern) {
		return null;
	}

	@Override
	public void put(Sketch sketch) {
		throw new UnsupportedOperationException("Sketch.put");
	}
}
