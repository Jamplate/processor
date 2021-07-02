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
package org.jamplate.spec.parameter.operator;

import org.jamplate.api.Spec;
import org.jamplate.function.Analyzer;
import org.jamplate.function.Compiler;
import org.jamplate.instruction.flow.Block;
import org.jamplate.instruction.operator.math.Product;
import org.jamplate.spec.element.ParameterSpec;
import org.jamplate.spec.standard.OperatorSpec;
import org.jamplate.spec.syntax.symbol.AsteriskSpec;
import org.jamplate.internal.util.Functions;
import org.jamplate.internal.util.IO;
import org.jamplate.internal.function.analyzer.alter.BinaryOperatorAnalyzer;
import org.jamplate.internal.function.analyzer.filter.FilterByKindAnalyzer;
import org.jamplate.internal.function.analyzer.filter.FilterByNotParentKindAnalyzer;
import org.jamplate.internal.function.analyzer.router.HierarchyAnalyzer;
import org.jamplate.internal.function.compiler.filter.FilterByKindCompiler;
import org.jamplate.model.CompileException;
import org.jamplate.model.Instruction;
import org.jamplate.model.Sketch;
import org.jamplate.model.Tree;
import org.jetbrains.annotations.NotNull;

/**
 * Multiplier operator specifications.
 *
 * @author LSafer
 * @version 0.3.0
 * @since 0.3.0 ~2021.06.22
 */
public class MultiplierSpec implements Spec {
	/**
	 * An instance of this spec.
	 *
	 * @since 0.3.0 ~2021.06.22
	 */
	@NotNull
	public static final MultiplierSpec INSTANCE = new MultiplierSpec();

	/**
	 * The kind of a multiplier operator context.
	 *
	 * @since 0.3.0 ~2021.06.22
	 */
	@NotNull
	public static final String KIND = "operator:multiplier";

	/**
	 * The qualified name of this spec.
	 *
	 * @since 0.3.0 ~2021.06.22
	 */
	@NotNull
	public static final String NAME = MultiplierSpec.class.getSimpleName();

	@NotNull
	@Override
	public Analyzer getAnalyzer() {
		return Functions.analyzer(
				//analyze the whole hierarchy
				HierarchyAnalyzer::new,
				//filter only if not already wrapped
				a -> new FilterByNotParentKindAnalyzer(MultiplierSpec.KIND, a),
				//target asterisks
				a -> new FilterByKindAnalyzer(AsteriskSpec.KIND, a),
				//wrap
				a -> new BinaryOperatorAnalyzer(
						//context wrapper constructor
						(d, r) -> new Tree(
								d,
								r,
								new Sketch(MultiplierSpec.KIND),
								OperatorSpec.WEIGHT
						),
						//operator constructor
						(w, t) -> w.getSketch().set(
								OperatorSpec.KEY_SIGN,
								t.getSketch()
						),
						//left-side wrapper constructor
						(w, r) -> w.offer(new Tree(
								w.getDocument(),
								r,
								w.getSketch()
								 .get(OperatorSpec.KEY_LEFT)
								 .setKind(ParameterSpec.KIND),
								ParameterSpec.WEIGHT
						)),
						//right-side wrapper constructor
						(w, r) -> w.offer(new Tree(
								w.getDocument(),
								r,
								w.getSketch()
								 .get(OperatorSpec.KEY_RIGHT)
								 .setKind(ParameterSpec.KIND),
								ParameterSpec.WEIGHT
						))
				)
		);
	}

	@NotNull
	@Override
	public Compiler getCompiler() {
		return Functions.compiler(
				//target multiplier operator
				c -> new FilterByKindCompiler(MultiplierSpec.KIND, c),
				//compile
				c -> (compiler, compilation, tree) -> {
					Tree leftT = tree.getSketch().get(OperatorSpec.KEY_LEFT).getTree();
					Tree rightT = tree.getSketch().get(OperatorSpec.KEY_RIGHT).getTree();

					if (leftT == null || rightT == null)
						throw new CompileException(
								"Operator MULTIPLIER (*) is missing some components",
								tree
						);

					Instruction leftI = compiler.compile(
							compiler,
							compilation,
							leftT
					);
					Instruction rightI = compiler.compile(
							compiler,
							compilation,
							rightT
					);

					if (leftI == null || rightI == null)
						throw new CompileException(
								"The operator MULTIPLIER (*) cannot be applied to <" +
								IO.read(leftT) +
								"> and <" +
								IO.read(rightT) +
								">",
								tree
						);

					return new Block(
							tree,
							leftI,
							rightI,
							new Product(tree)
					);
				}
		);
	}

	@NotNull
	@Override
	public String getQualifiedName() {
		return MultiplierSpec.NAME;
	}
}
