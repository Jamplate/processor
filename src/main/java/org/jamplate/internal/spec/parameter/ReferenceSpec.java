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
package org.jamplate.internal.spec.parameter;

import org.jamplate.api.Spec;
import org.jamplate.function.Compiler;
import org.jamplate.instruction.flow.Block;
import org.jamplate.instruction.flow.Branch;
import org.jamplate.instruction.flow.Idle;
import org.jamplate.instruction.memory.heap.Access;
import org.jamplate.instruction.memory.resource.PushConst;
import org.jamplate.instruction.memory.stack.Dup;
import org.jamplate.instruction.memory.stack.Pop;
import org.jamplate.instruction.operator.logic.Defined;
import org.jamplate.instruction.operator.logic.Negate;
import org.jamplate.internal.spec.syntax.term.WordSpec;
import org.jamplate.internal.util.Functions;
import org.jamplate.internal.util.IO;
import org.jamplate.internal.util.compiler.wrapper.FilterByKindCompiler;
import org.jetbrains.annotations.NotNull;

/**
 * Parameter reference specification.
 *
 * @author LSafer
 * @version 0.3.0
 * @since 0.3.0 ~2021.06.20
 */
public class ReferenceSpec implements Spec {
	/**
	 * An instance of this spec.
	 *
	 * @since 0.3.0 ~2021.06.20
	 */
	@NotNull
	public static final ReferenceSpec INSTANCE = new ReferenceSpec();

	/**
	 * The qualified name of this spec.
	 *
	 * @since 0.3.0 ~2021.06.20
	 */
	@NotNull
	public static final String NAME = ReferenceSpec.class.getSimpleName();

	@NotNull
	@Override
	public Compiler getCompiler() {
		return Functions.compiler(
				//target words
				c -> new FilterByKindCompiler(WordSpec.KIND, c),
				//compile
				c -> (compiler, compilation, tree) -> {
					//read the tree
					String text = IO.read(tree).toString();

					//compile to Access
					return new Block(
							tree,
							//push the address
							new PushConst(tree, m -> text),
							//access
							Access.INSTANCE,
							//duplicate the value to be null checked first
							Dup.INSTANCE,
							//null check
							Defined.INSTANCE,
							//negate null check result
							Negate.INSTANCE,
							//branch if null
							new Branch(
									new Block(
											//pop the duplicate value (it is null)
											Pop.INSTANCE,
											//push the name of the reference
											new PushConst(tree, m -> text)
									),
									//otherwise, do nothing
									Idle.INSTANCE
							)
					);
				}
		);
	}

	@NotNull
	@Override
	public String getQualifiedName() {
		return ReferenceSpec.NAME;
	}
}
//		return new FilterByHierarchyKindCompiler(
//				ParameterSpec.KIND,
//				new FilterByKindCompiler(
//						WordSpec.KIND,
//						(compiler, compilation, tree) -> {
//							String text = Trees.read(tree).toString();
//
//							return new Block(
//									new PushConst(m -> text),
//									Access.INSTANCE
//							);
//						}
//				)
//		);
