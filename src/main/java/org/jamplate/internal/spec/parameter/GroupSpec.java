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
import org.jamplate.instruction.memory.frame.DumpFrame;
import org.jamplate.instruction.memory.frame.JoinFrame;
import org.jamplate.instruction.memory.frame.PushFrame;
import org.jamplate.internal.function.compiler.branch.FlattenCompiler;
import org.jamplate.internal.function.compiler.concrete.ToIdleCompiler;
import org.jamplate.internal.function.compiler.group.FirstCompileCompiler;
import org.jamplate.internal.function.compiler.router.FallbackCompiler;
import org.jamplate.internal.function.compiler.wrapper.FilterByKindCompiler;
import org.jamplate.internal.spec.standard.AnchorSpec;
import org.jamplate.internal.spec.syntax.enclosure.ParenthesesSpec;
import org.jamplate.internal.util.Functions;
import org.jetbrains.annotations.NotNull;

/**
 * Logical group specification.
 *
 * @author LSafer
 * @version 0.3.0
 * @since 0.3.0 ~2021.06.20
 */
public class GroupSpec implements Spec {
	/**
	 * An instance of this spec.
	 *
	 * @since 0.3.0 ~2021.06.20
	 */
	@NotNull
	public static final GroupSpec INSTANCE = new GroupSpec();

	/**
	 * The qualified name of this spec.
	 *
	 * @since 0.3.0 ~2021.06.20
	 */
	@NotNull
	public static final String NAME = GroupSpec.class.getSimpleName();

	@NotNull
	@Override
	public Compiler getCompiler() {
		return Functions.compiler(
				//target parentheses
				c -> new FilterByKindCompiler(ParenthesesSpec.KIND, c),
				//compile the whole context
				c -> (compiler, compilation, tree) ->
						new Block(
								tree,
								//push a frame to encapsulate the content of the group
								new PushFrame(tree),
								//execute inner parts
								c.compile(compiler, compilation, tree),
								//join the execution results
								JoinFrame.INSTANCE,
								//dump the frame
								DumpFrame.INSTANCE
						),
				//flatten parts
				FlattenCompiler::new,
				//compile anchors and body
				c -> new FirstCompileCompiler(
						//compile opening anchors to Idle
						new FilterByKindCompiler(AnchorSpec.KIND_OPEN, ToIdleCompiler.INSTANCE),
						//compile closing anchors to Idle
						new FilterByKindCompiler(AnchorSpec.KIND_CLOSE, ToIdleCompiler.INSTANCE),
						//compile body
						Functions.compiler(
								//target body
								cc -> new FilterByKindCompiler(AnchorSpec.KIND_BODY, cc),
								//flatten body parts
								FlattenCompiler::new,
								//compile each part using the fallback compiler
								cc -> FallbackCompiler.INSTANCE
						)
				)
		);
	}

	@NotNull
	@Override
	public String getQualifiedName() {
		return GroupSpec.NAME;
	}
}
//		return new FilterByHierarchyKindCompiler(
//				ParameterSpec.KIND,
//				new FilterByKindCompiler(
//						ParenthesesSpec.KIND,
//						new FlattenCompiler(
//								FallbackCompiler.INSTANCE,
//								new MandatoryCompiler(new FirstCompileCompiler(
//										new FilterByKindCompiler(AnchorSpec.KIND_OPEN, ToIdleCompiler.INSTANCE),
//										new FilterByKindCompiler(AnchorSpec.KIND_CLOSE, ToIdleCompiler.INSTANCE),
//										new FilterWhitespaceCompiler(ToIdleCompiler.INSTANCE)
//								))
//						)
//				)
//		);
