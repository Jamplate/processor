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
package org.jamplate.glucose.instruction.operator.struct;

import org.jamplate.glucose.value.ArrayValue;
import org.jamplate.glucose.value.NumberValue;
import org.jamplate.glucose.value.ObjectValue;
import org.jamplate.glucose.value.QuoteValue;
import org.jamplate.memory.Memory;
import org.jamplate.memory.Value;
import org.jamplate.model.Environment;
import org.jamplate.model.ExecutionException;
import org.jamplate.model.Instruction;
import org.jamplate.model.Tree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static org.jamplate.glucose.internal.util.Structs.touch;
import static org.jamplate.glucose.internal.util.Values.*;

/**
 * An instruction that pops the top three values in the stack and put the first popped
 * value in the third popped value at the key resultant from evaluating the second popped
 * value.
 * <br>
 * If the second popped value is not {@link ArrayValue array}, an {@link
 * ExecutionException} will be thrown.
 * <br><br>
 * Memory Visualization:
 * <pre>
 *     [..., param:value*|array*|object*, key:array*, value:value*]
 *     [..., result:array*|object*]
 * </pre>
 *
 * @author LSafer
 * @version 0.3.0
 * @since 0.3.0 ~2021.06.15
 */
public class Touch implements Instruction {
	/**
	 * An instance of this instruction.
	 *
	 * @since 0.3.0 ~2021.06.15
	 */
	@NotNull
	public static final Touch INSTANCE = new Touch();

	@SuppressWarnings("JavaDoc")
	private static final long serialVersionUID = -4102904984181017768L;

	/**
	 * A reference of this instruction in the source code.
	 *
	 * @since 0.3.0 ~2021.06.15
	 */
	@Nullable
	protected final Tree tree;

	/**
	 * Construct a new instruction that pops the last two values in the stack and
	 * allocates the first popped value to the heap at the result of evaluating the second
	 * popped value.
	 *
	 * @since 0.3.0 ~2021.06.15
	 */
	public Touch() {
		this.tree = null;
	}

	/**
	 * Construct a new instruction that pops the last two values in the stack and
	 * allocates the first popped value to the heap at the result of evaluating the second
	 * popped value.
	 *
	 * @param tree a reference for the constructed instruction in the source code.
	 * @throws NullPointerException if the given {@code tree} is null.
	 * @since 0.3.0 ~2021.06.15
	 */
	public Touch(@NotNull Tree tree) {
		Objects.requireNonNull(tree, "tree");
		this.tree = tree;
	}

	@Override
	public void exec(@NotNull Environment environment, @NotNull Memory memory) {
		Objects.requireNonNull(environment, "environment");
		Objects.requireNonNull(memory, "memory");

		//value
		Value value0 = memory.pop();
		//key
		Value value1 = memory.pop();
		//struct
		Value value2 = memory.pop();

		if (!(value1 instanceof ArrayValue))
			throw new ExecutionException(
					"TOUCH expected array but got: " + value1.evaluate(memory),
					this.tree
			);

		//value
		QuoteValue quote0 = quote(value0);
		//key
		ArrayValue array1 = (ArrayValue) value1;
		List<Value> list1 = array1.getPipe().eval(memory);
		//first key
		Value value3 = list1.get(0);

		if (!(value2 instanceof ObjectValue) && value3 instanceof NumberValue) {
			//struct
			ArrayValue array2 = array(value2);

			//result
			ArrayValue array4 = touch(
					array2,
					list1,
					value0
			);

			memory.push(array4);
		} else {
			//struct
			ObjectValue object2 = object(value2);

			//result
			ObjectValue object4 = touch(
					object2,
					list1,
					value0
			);

			memory.push(object4);
		}
	}

	@Nullable
	@Override
	public Tree getTree() {
		return this.tree;
	}

	@NotNull
	@Override
	public Instruction optimize(int mode) {
		return mode < 0 ? Touch.INSTANCE : new Touch(new Tree(this.tree));
	}
}
