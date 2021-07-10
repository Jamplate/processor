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
package org.jamplate.glucose.spec.parameter.operator;

import org.jamplate.unit.Spec;
import org.jamplate.function.Analyzer;
import org.jamplate.function.Compiler;
import org.jamplate.glucose.instruction.memory.frame.IDumpFrame;
import org.jamplate.glucose.instruction.memory.frame.IGlueFrame;
import org.jamplate.glucose.instruction.memory.frame.IPushFrame;
import org.jamplate.glucose.instruction.operator.cast.IBuildPair;
import org.jamplate.glucose.instruction.operator.cast.ICastGlue;
import org.jamplate.glucose.spec.element.ParameterSpec;
import org.jamplate.glucose.spec.standard.OperatorSpec;
import org.jamplate.glucose.spec.syntax.symbol.ColonSpec;
import org.jamplate.impl.instruction.Block;
import org.jamplate.model.CompileException;
import org.jamplate.model.Instruction;
import org.jamplate.model.Sketch;
import org.jamplate.model.Tree;
import org.jetbrains.annotations.NotNull;

import static org.jamplate.impl.analyzer.FilterAnalyzer.filter;
import static org.jamplate.impl.analyzer.HierarchyAnalyzer.hierarchy;
import static org.jamplate.impl.compiler.FilterCompiler.filter;
import static org.jamplate.glucose.internal.analyzer.BinaryOperatorAnalyzer.operator;
import static org.jamplate.util.Functions.analyzer;
import static org.jamplate.util.Functions.compiler;
import static org.jamplate.util.Query.*;
import static org.jamplate.util.Source.read;

/**
 * Pair operator specifications.
 *
 * @author LSafer
 * @version 0.3.0
 * @since 0.3.0 ~2021.06.27
 */
@SuppressWarnings("OverlyCoupledMethod")
public class PairSpec implements Spec {
	/**
	 * An instance of this spec.
	 *
	 * @since 0.3.0 ~2021.06.27
	 */
	@NotNull
	public static final PairSpec INSTANCE = new PairSpec();

	/**
	 * The kind of a pair operator context.
	 *
	 * @since 0.3.0 ~2021.06.27
	 */
	@NotNull
	public static final String KIND = "operator:pair";

	/**
	 * The qualified name of this spec.
	 *
	 * @since 0.3.0 ~2021.06.25
	 */
	@NotNull
	public static final String NAME = PairSpec.class.getSimpleName();

	@NotNull
	@Override
	public Analyzer getAnalyzer() {
		return analyzer(
				//analyze the whole hierarchy
				a -> hierarchy(a),
				//target valid non processed trees
				a -> filter(a, and(
						//target colon symbols
						is(ColonSpec.KIND),
						//skip if already wrapped
						parent(not(PairSpec.KIND))
				)),
				//analyze
				a -> operator(
						//context wrapper constructor
						(d, r) -> new Tree(
								d,
								r,
								new Sketch(PairSpec.KIND),
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
		return compiler(
				//target pair operator
				c -> filter(c, is(PairSpec.KIND)),
				//compile
				c -> (compiler, compilation, tree) -> {
					Tree leftT = tree.getSketch().get(OperatorSpec.KEY_LEFT).getTree();
					Tree rightT = tree.getSketch().get(OperatorSpec.KEY_RIGHT).getTree();

					if (leftT == null || rightT == null)
						throw new CompileException(
								"Operator PAIR (:) is missing some components",
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
								"The operator PAIR (:) cannot be applied to <" +
								read(leftT) +
								"> and <" +
								read(rightT) +
								">",
								tree
						);

					return new Block(
							tree,
							//key sandbox
							new Block(
									tree,
									//push a frame to encapsulate key values
									new IPushFrame(tree),
									//run the key
									leftI,
									//glue the key parts
									new IGlueFrame(tree),
									//if single value
									new ICastGlue(tree),
									//dump the frame
									new IDumpFrame(tree)
							),
							//value sandbox
							new Block(
									tree,
									//push a frame to encapsulate value values
									new IPushFrame(tree),
									//run the value
									rightI,
									//glue the value parts
									new IGlueFrame(tree),
									//if single value
									new ICastGlue(tree),
									//dump the frame
									new IDumpFrame(tree)
							),
							//build a pair from the glued values
							new IBuildPair(tree)
					);
				}
		);
	}

	@NotNull
	@Override
	public String getQualifiedName() {
		return PairSpec.NAME;
	}
}
