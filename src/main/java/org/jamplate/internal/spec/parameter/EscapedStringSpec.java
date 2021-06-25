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
import org.jamplate.internal.spec.standard.AnchorSpec;
import org.jamplate.internal.spec.syntax.enclosure.QuotesSpec;
import org.jamplate.internal.util.Functions;
import org.jamplate.internal.util.compiler.branch.FlattenCompiler;
import org.jamplate.internal.util.compiler.concrete.ToIdleCompiler;
import org.jamplate.internal.util.compiler.concrete.ToPushConstCompiler;
import org.jamplate.internal.util.compiler.group.FirstCompileCompiler;
import org.jamplate.internal.util.compiler.wrapper.FilterByKindCompiler;
import org.jetbrains.annotations.NotNull;

/**
 * Parameter escaped string specification.
 *
 * @author LSafer
 * @version 0.3.0
 * @since 0.3.0 ~2021.06.20
 */
public class EscapedStringSpec implements Spec {
	/**
	 * An instance of this spec.
	 *
	 * @since 0.3.0 ~2021.06.20
	 */
	@NotNull
	public static final EscapedStringSpec INSTANCE = new EscapedStringSpec();

	/**
	 * The qualified name of this spec.
	 *
	 * @since 0.3.0 ~2021.06.20
	 */
	@NotNull
	public static final String NAME = EscapedStringSpec.class.getSimpleName();

	@NotNull
	@Override
	public Compiler getCompiler() {
		return Functions.compiler(
				//target quotes
				c -> new FilterByKindCompiler(QuotesSpec.KIND, c),
				//flatten non-parsed trees, compile non-parsed trees to PushConst
				c -> new FlattenCompiler(c, ToPushConstCompiler.INSTANCE),
				//compile parsed trees
				c -> new FirstCompileCompiler(
						//compile opening anchors to Idle
						new FilterByKindCompiler(AnchorSpec.KIND_OPEN, ToIdleCompiler.INSTANCE),
						//compile closing anchors to Idle
						new FilterByKindCompiler(AnchorSpec.KIND_CLOSE, ToIdleCompiler.INSTANCE)
				)
		);
	}

	@NotNull
	@Override
	public String getQualifiedName() {
		return EscapedStringSpec.NAME;
	}
}
//		return new FilterByHierarchyKindCompiler(
//				ParameterSpec.KIND,
//				new FilterByKindCompiler(
//						QuotesSpec.KIND,
//						new FlattenCompiler(
//								new FirstCompileCompiler(
//										new FilterByKindCompiler(AnchorSpec.KIND_OPEN, ToIdleCompiler.INSTANCE),
//										new FilterByKindCompiler(AnchorSpec.KIND_CLOSE, ToIdleCompiler.INSTANCE)
//								),
//								ToPushConstCompiler.INSTANCE
//						)
//				)
//		);
