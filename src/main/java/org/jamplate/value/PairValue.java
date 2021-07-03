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
package org.jamplate.value;

import org.jamplate.model.Memory;
import org.jamplate.model.Pipe;
import org.jamplate.model.Value;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * A value that evaluates to an object pair and can be evaluates to a raw pair entry.
 *
 * @author LSafer
 * @version 0.3.0
 * @since 0.3.0 ~2021.06.30
 */
@SuppressWarnings("UnqualifiedInnerClassAccess")
public final class PairValue implements Value<Entry<Value, Value>> {
	@SuppressWarnings("JavaDoc")
	private static final long serialVersionUID = 2938159775955369455L;

	/**
	 * The evaluating function that evaluates to the raw value of this value.
	 *
	 * @since 0.3.0 ~2021.06.29
	 */
	@NotNull
	private final Pipe<Entry<Value, Value>> pipe;

	/**
	 * Construct a new pair value that evaluates to the result of parsing the given {@code
	 * source}.
	 *
	 * @param source the source text.
	 * @throws NullPointerException if the given {@code source} is null.
	 * @since 0.3.0 ~2021.07.01
	 */
	public PairValue(@NotNull String source) {
		Objects.requireNonNull(source, "source");
		Entry<Value, Value> pair = PairValue.parse(source);
		this.pipe = (m, v) -> pair;
	}

	/**
	 * Construct a new pair value that evaluates to the given raw {@code key} and {@code
	 * value} pair.
	 *
	 * @param key   the raw key of the pair of the constructed pair value.
	 * @param value the raw value of the pair of the constructed pair value.
	 * @throws NullPointerException if the given {@code pair} is null.
	 * @since 0.3.0 ~2021.07.01
	 */
	public PairValue(@NotNull Object key, @NotNull Object value) {
		Objects.requireNonNull(key, "key");
		Objects.requireNonNull(value, "value");
		Entry<Value, Value> entry = PairValue.transformPair(key, value);
		this.pipe = (m, v) -> entry;
	}

	/**
	 * Construct a new pair value that evaluates to the given raw {@code pair}.
	 *
	 * @param pair the raw pair of the constructed pair value.
	 * @throws NullPointerException if the given {@code pair} is null.
	 * @since 0.3.0 ~2021.07.01
	 */
	public PairValue(@NotNull Entry<?, ?> pair) {
		Objects.requireNonNull(pair, "pair");
		Entry<Value, Value> entry = PairValue.transformPair(pair);
		this.pipe = (m, v) -> entry;
	}

	/**
	 * Construct a new pair value that evaluate to the result of parsing the evaluation of
	 * the given {@code value}.
	 *
	 * @param value the value of the constructed pair value.
	 * @throws NullPointerException if the given {@code value} is null.
	 * @since 0.3.0 ~2021.06.29
	 */
	public PairValue(@NotNull Value<?> value) {
		Objects.requireNonNull(value, "value");
		this.pipe = (m, v) -> PairValue.parse(value.evaluate(m));
	}

	/**
	 * An internal constructor to construct a new pair value with the given {@code
	 * function}.
	 *
	 * @param pipe the function that evaluates to the pair of the constructed pair value.
	 * @throws NullPointerException if the given {@code function} is null.
	 * @since 0.3.0 ~2021.07.01
	 */
	private PairValue(@NotNull Pipe<Entry<Value, Value>> pipe) {
		Objects.requireNonNull(pipe, "pipe");
		this.pipe = pipe;
	}

	/**
	 * Cast the given {@code object} into a pair.
	 *
	 * @param object the object to be cast.
	 * @return a pair evaluating to a pair interpretation of the given {@code object}.
	 * @since 0.3.0 ~2021.07.01
	 */
	@NotNull
	@Contract(pure = true)
	public static PairValue cast(@Nullable Object object) {
		//it
		if (object instanceof PairValue)
			return (PairValue) object;
		//wrap
		if (object instanceof Value)
			return new PairValue((Value) object);
		//raw
		if (object instanceof Entry)
			return new PairValue((Entry) object);

		//parse
		return new PairValue(Tokenizer.toString(object));
	}

	/**
	 * Parse the given {@code source} into a pair.
	 *
	 * @param source the source to be parsed.
	 * @return a pair from parsing the given {@code source}.
	 * @throws NullPointerException if the given {@code source} is null.
	 * @since 0.3.0 ~2021.07.01
	 */
	@NotNull
	@Unmodifiable
	@Contract(pure = true)
	public static Entry<Value, Value> parse(@NotNull String source) {
		Objects.requireNonNull(source, "source");
		//attempt to parse as singleton object
		try {
			JSONObject object = new JSONObject("{" + source + "}");

			return object.toMap()
						 .entrySet()
						 .stream()
						 .map(e -> new AbstractMap.SimpleImmutableEntry<>(
								 Tokenizer.cast(e.getKey()),
								 Tokenizer.cast(e.getValue())
						 ))
						 .findFirst()
						 .orElse(new AbstractMap.SimpleImmutableEntry<>(
								 Value.NULL,
								 Value.NULL
						 ));
		} catch (JSONException ignored) {
		}
		//attempt to parse as marker
		return new AbstractMap.SimpleImmutableEntry<>(
				m -> JSONObject.quote(source),
				m -> "\"\""
		);
	}

	/**
	 * Transform the given {@code key} and {@code value} pair into a pair values entry.
	 *
	 * @param key   the key.
	 * @param value the value.
	 * @return an immutable values entry interpretation of the given {@code key} and
	 *        {@code value} pair.
	 * @throws NullPointerException if the given {@code pair} is null.
	 * @since 0.3.0 ~2021.07.01
	 */
	@NotNull
	@Unmodifiable
	@Contract(pure = true)
	public static Entry<Value, Value> transformPair(@NotNull Object key, @NotNull Object value) {
		Objects.requireNonNull(key, "key");
		Objects.requireNonNull(value, "value");
		return new AbstractMap.SimpleImmutableEntry<>(
				Tokenizer.cast(key),
				Tokenizer.cast(value)
		);
	}

	/**
	 * Transform the given {@code pair} entry into a pair values entry.
	 *
	 * @param pair the entry to be transformed.
	 * @return an immutable values entry interpretation of the given {@code pair} entry.
	 * @throws NullPointerException if the given {@code pair} is null.
	 * @since 0.3.0 ~2021.07.01
	 */
	@NotNull
	@Unmodifiable
	@Contract(pure = true)
	public static Entry<Value, Value> transformPair(@NotNull Entry<?, ?> pair) {
		Objects.requireNonNull(pair, "pair");
		return new AbstractMap.SimpleImmutableEntry<>(
				Tokenizer.cast(pair.getKey()),
				Tokenizer.cast(pair.getValue())
		);
	}

	@NotNull
	@Override
	public PairValue apply(@NotNull Pipe<Entry<Value, Value>> pipe) {
		Objects.requireNonNull(pipe, "pipe");
		return new PairValue(
				this.pipe.apply(pipe)
		);
	}

	@NotNull
	@Override
	public String evaluate(@NotNull Memory memory) {
		Objects.requireNonNull(memory, "memory");
		Entry<Value, Value> pair = this.pipe.eval(memory);
		return pair.getKey().evaluate(memory) +
			   ":" +
			   pair.getValue().evaluate(memory);
	}

	@NotNull
	@Override
	public Pipe<Entry<Value, Value>> getPipe() {
		return this.pipe;
	}

	@NotNull
	@Override
	public String toString() {
		return "Pair:" + Integer.toHexString(this.hashCode());
	}

	//--------

	/**
	 * Return a value that evaluates to the key of this pair.
	 *
	 * @return a value of the key of this.
	 * @since 0.3.0 ~2021.07.01
	 */
	@NotNull
	@Contract(pure = true)
	public Value<?> getKey() {
		return m -> this.pipe
				.eval(m)
				.getKey()
				.evaluate(m);
	}

	/**
	 * Return a value that evaluates to the value of this pair.
	 *
	 * @return a value of the value of this.
	 * @since 0.3.0 ~2021.07.01
	 */
	@NotNull
	@Contract(pure = true)
	public Value<?> getValue() {
		return m -> this.pipe
				.eval(m)
				.getValue()
				.evaluate(m);
	}
}
