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
package org.jamplate.impl;

import org.jamplate.impl.compiler.*;
import org.jamplate.impl.instruction.*;
import org.jamplate.impl.util.Trees;
import org.jamplate.model.CompileException;
import org.jamplate.model.Instruction;
import org.jamplate.model.Tree;
import org.jamplate.model.function.Compiler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A class containing the default jamplate implementation compilers.
 *
 * @author LSafer
 * @version 0.2.0
 * @since 0.2.0 ~2021.05.21
 */
@SuppressWarnings("OverlyCoupledClass")
public final class Compilers {
	//CX CMD

	/**
	 * A compiler that compiles console commands.
	 *
	 * @since 0.2.0 ~2021.05.21
	 */
	@NotNull
	public static final Compiler CX_CMD_CONSOLE =
			new KindCompiler(Kind.CX_CMD_CONSOLE, (compiler, compilation, tree) -> {
				Tree paramT = tree.getSketch().get(Component.PARAMETER).getTree();
				Instruction instruction = compiler.compile(compiler, compilation, paramT);

				return new ConsoleExecInstr(
						tree,
						instruction == null ?
						Instruction.empty(paramT) :
						instruction
				);
			});

	/**
	 * A compiler that compiles declare commands.
	 *
	 * @since 0.2.0 ~2021.05.23
	 */
	@NotNull
	public static final Compiler CX_CMD_DECLARE =
			new KindCompiler(Kind.CX_CMD_DECLARE, (compiler, compilation, tree) -> {
				Tree keyT = tree.getSketch().get(Component.KEY).getTree();
				Tree paramT = tree.getSketch().get(Component.PARAMETER).getTree();

				String address = Trees.read(keyT).toString();
				Instruction instruction = compiler.compile(compiler, compilation, paramT);

				if (instruction == null)
					throw new CompileException(
							"Unrecognized parameter",
							paramT
					);

				return new RepllocAddrExecInstr(
						tree,
						address,
						instruction
				);
			});

	/**
	 * A compiler that compiles define commands.
	 *
	 * @since 0.2.0 ~2021.05.23
	 */
	@NotNull
	public static final Compiler CX_CMD_DEFINE =
			new KindCompiler(Kind.CX_CMD_DEFINE, (compiler, compilation, tree) -> {
				Tree keyT = tree.getSketch().get(Component.KEY).getTree();
				Tree paramT = tree.getSketch().get(Component.PARAMETER).getTree();

				String address = Trees.read(keyT).toString();
				Instruction instruction = compiler.compile(compiler, compilation, paramT);

				if (instruction == null)
					throw new CompileException(
							"Unrecognized parameter",
							paramT
					);

				return new RepllocAddrExecInstr(
						tree,
						address,
						instruction
				);
			});

	/**
	 * A compiler that compiles include commands.
	 *
	 * @since 0.2.0 ~2021.05.21
	 */
	@NotNull
	public static final Compiler CX_CMD_INCLUDE =
			new KindCompiler(Kind.CX_CMD_INCLUDE, (compiler, compilation, tree) -> {
				Tree paramT = tree.getSketch().get(Component.PARAMETER).getTree();

				Instruction instruction = compiler.compile(compiler, compilation, paramT);

				if (instruction == null)
					throw new CompileException(
							"Unrecognized parameter",
							paramT
					);

				return new ExecImportExecInstr(
						tree,
						instruction
				);
			});

	/**
	 * A compiler that compiles undec commands.
	 *
	 * @since 0.2.0 ~2021.05.31
	 */
	@NotNull
	public static final Compiler CX_CMD_UNDEC =
			new KindCompiler(Kind.CX_CMD_UNDEC, (compiler, compilation, tree) -> {
				Tree keyT = tree.getSketch().get(Component.KEY).getTree();

				String address = Trees.read(keyT).toString();

				return new FreeAddr(
						tree,
						address
				);
			});

	/**
	 * A compiler that compiles undef commands.
	 *
	 * @since 0.2.0 ~2021.05.31
	 */
	@NotNull
	public static final Compiler CX_CMD_UNDEF =
			new KindCompiler(Kind.CX_CMD_UNDEF, (compiler, compilation, tree) -> {
				Tree keyT = tree.getSketch().get(Component.KEY).getTree();

				String address = Trees.read(keyT).toString();

				return new RepreeAddr(
						tree,
						address
				);
			});

	//CX FLW

	/**
	 * A compiler that compiles for-contexts. (including the for and endfor in it)
	 *
	 * @since 0.2.0 ~2021.05.31
	 */
	@SuppressWarnings("OverlyLongLambda")
	@NotNull
	public static final Compiler CX_FLW_FOR =
			new KindCompiler(Kind.CX_FLW_FOR, (compiler, compilation, tree) -> {
				String address = null;
				Instruction instruction = null;
				List<Instruction> instructions = new ArrayList<>();

				//assert FOR is the first and ENDFOR is the last
				for (Tree t : Trees.flatChildren(tree))
					switch (t.getSketch().getKind()) {
						case Kind.CX_CMD_FOR: {
							Tree keyT = t.getSketch().get(Component.KEY).getTree();
							Tree paramT = t.getSketch().get(Component.PARAMETER).getTree();

							address = Trees.read(keyT).toString();
							instruction = compiler.compile(compiler, compilation, paramT);
							break;
						}
						case Kind.CX_CMD_ENDFOR: {
							//done
							break;
						}
						default: {
							//instruction
							instructions.add(compiler.compile(compiler, compilation, t));
							break;
						}
					}

				if (address == null || instruction == null)
					throw new CompileException(
							"For context is missing some components",
							tree
					);

				return new FpedAddrExecInstrXinstr(
						address,
						instruction,
						instructions
				);
			});

	/**
	 * A compiler that compiles if-contexts. (including the if, elif-s, else and endif in
	 * it)
	 *
	 * @since 0.2.0 ~2021.05.24
	 */
	@SuppressWarnings("OverlyLongLambda")
	@NotNull
	public static final Compiler CX_FLW_IF =
			new KindCompiler(Kind.CX_FLW_IF, (compiler, compilation, tree) -> {
				Map<Instruction, List<Instruction>> instructions = new LinkedHashMap<>();
				List<Instruction> current = null;

				//assert IF/IFDEF/IFNDEF is the first and ENDIF is the last
				for (Tree t : Trees.flatChildren(tree))
					switch (t.getSketch().getKind()) {
						case Kind.CX_CMD_IF:
						case Kind.CX_CMD_ELIF: {
							Tree paramT = t.getSketch().get(Component.PARAMETER).getTree();
							Instruction instruction = compiler.compile(compiler, compilation, paramT);

							instructions.put(instruction, current = new ArrayList<>());
							break;
						}
						case Kind.CX_CMD_IFDEF:
						case Kind.CX_CMD_ELIFDEF: {
							Tree keyT = t.getSketch().get(Component.KEY).getTree();
							String address = Trees.read(keyT).toString();

							instructions.put(new DefAddr(keyT, address), current = new ArrayList<>());
							break;
						}
						case Kind.CX_CMD_IFNDEF:
						case Kind.CX_CMD_ELIFNDEF: {
							Tree keyT = t.getSketch().get(Component.KEY).getTree();
							String address = Trees.read(keyT).toString();

							instructions.put(new NdefAddr(keyT, address), current = new ArrayList<>());
							break;
						}
						case Kind.CX_CMD_ELSE: {
							instructions.put(null, current = new ArrayList<>());
							break;
						}
						case Kind.CX_CMD_ENDIF: {
							//done
							current = null;
							break;
						}
						default: {
							//intermediate instruction
							Instruction instruction = compiler.compile(compiler, compilation, t);

							current.add(instruction);
							break;
						}
					}

				List<Instruction> conditions = new ArrayList<>(instructions.keySet());
				Collections.reverse(conditions);

				Instruction instruction = null;
				for (Instruction condition : conditions)
					if (condition == null)
						instruction = new IpedXinstr(
								tree,
								instructions.get(null)
						);
					else if (instruction == null)
						instruction = new BranchExecInstr0Instr1Instr2(
								tree,
								condition,
								new IpedXinstr(instructions.get(condition))
						);
					else
						instruction = new BranchExecInstr0Instr1Instr2(
								tree,
								condition,
								new IpedXinstr(instructions.get(condition)),
								instruction
						);

				return instruction;
			});

	//CX INJ

	/**
	 * A compiler that compiles injections.
	 *
	 * @since 0.2.0 ~2021.05.25
	 */
	@NotNull
	public static final Compiler CX_INJ =
			new KindCompiler(Kind.CX_INJ, (compiler, compilation, tree) -> {
				Tree parameterT = tree.getSketch().get(Component.PARAMETER).getTree();

				Instruction instruction = compiler.compile(compiler, compilation, parameterT);

				if (instruction == null)
					throw new CompileException(
							"Unrecognized parameter",
							parameterT
					);

				return new PrintExecInstr(
						tree,
						instruction
				);
			});

	//CX TXT

	/**
	 * A compiler that compiles the non-command text.
	 *
	 * @since 0.2.0 ~2021.05.21
	 */
	@NotNull
	public static final Compiler CX_TXT =
			new FlattenCompiler(
					FallbackCompiler.INSTANCE,
					ReprntConstCompiler.INSTANCE
			);

	//SX

	/**
	 * A compiler that compiles curly braces.
	 *
	 * @since 0.2.0 ~2021.05.31
	 */
	@NotNull
	public static final Compiler SX_CUR =
			new KindCompiler(Kind.SX_CUR, new FlattenCompiler(
					new OrderCompiler(
							new KindCompiler(Kind.SX_DQT, PushConstCompiler.INSTANCE),
							//new KindCompiler(Kind.SX_QTE, PushConstCompiler.INSTANCE),
							FallbackCompiler.INSTANCE
					),
					new MandatoryCompiler(new OrderCompiler(
							new KindCompiler(Kind.SX_CUR_OPEN, PushConstCompiler.INSTANCE),
							new KindCompiler(Kind.SX_CUR_CLOSE, PushConstCompiler.INSTANCE),
							new KindCompiler(Kind.SX_CMA, PushConstCompiler.INSTANCE),
							new KindCompiler(Kind.SX_CLN, PushConstCompiler.INSTANCE),
							WhitespaceCompiler.INSTANCE
					))
			));

	/**
	 * A compiler that compiles double quotes.
	 *
	 * @since 0.2.0 ~2021.05.31
	 */
	@NotNull
	public static final Compiler SX_DQT =
			new KindCompiler(Kind.SX_DQT, new FlattenCompiler(
					new OrderCompiler(
							new KindCompiler(Kind.SX_DQT_OPEN, EmptyCompiler.INSTANCE),
							new KindCompiler(Kind.SX_DQT_CLOSE, EmptyCompiler.INSTANCE)
					),
					PushConstCompiler.INSTANCE
			));

	/**
	 * A compiler that compiles suppressed line separators.
	 *
	 * @since 0.2.0 ~2021.05.23
	 */
	@NotNull
	public static final Compiler SX_EOL_SUPPRESSED =
			new KindCompiler(Kind.SX_EOL_SUPPRESSED, EmptyCompiler.INSTANCE);

	/**
	 * A compiler that compiles name instructions.
	 *
	 * @since 0.2.0 ~2021.05.31
	 */
	@NotNull
	public static final Compiler SX_NME =
			new KindCompiler(Kind.SX_NME, PushEvalAddrCompiler.INSTANCE);

	/**
	 * A compiler that compiles numbers.
	 *
	 * @since 0.2.0 ~2021.05.31
	 */
	@NotNull
	public static final Compiler SX_NUM =
			new KindCompiler(Kind.SX_NUM, PushConstCompiler.INSTANCE);

	/**
	 * A compiler that compiles quotes.
	 *
	 * @since 0.2.0 ~2021.05.31
	 */
	@NotNull
	public static final Compiler SX_QTE =
			new KindCompiler(Kind.SX_QTE, new FlattenCompiler(
					new OrderCompiler(
							new KindCompiler(Kind.SX_QTE_OPEN, EmptyCompiler.INSTANCE),
							new KindCompiler(Kind.SX_QTE_CLOSE, EmptyCompiler.INSTANCE)
					),
					PushConstCompiler.INSTANCE
			));

	/**
	 * A compiler that compiles parentheses.
	 *
	 * @since 0.2.0 ~2021.05.31
	 */
	@NotNull
	public static final Compiler SX_RND =
			new KindCompiler(Kind.SX_RND, new FlattenCompiler(
					new OrderCompiler(
							new KindCompiler(Kind.SX_DQT, PushConstCompiler.INSTANCE),
							//new KindCompiler(Kind.SX_QTE, PushConstCompiler.INSTANCE),
							FallbackCompiler.INSTANCE
					),
					new MandatoryCompiler(new OrderCompiler(
							new KindCompiler(Kind.SX_RND_OPEN, PushConstCompiler.INSTANCE),
							new KindCompiler(Kind.SX_RND_CLOSE, PushConstCompiler.INSTANCE),
							WhitespaceCompiler.INSTANCE
					))
			));

	/**
	 * A compiler that compiles square brackets.
	 *
	 * @since 0.2.0 ~2021.05.31
	 */
	@NotNull
	public static final Compiler SX_SQR =
			new KindCompiler(Kind.SX_SQR, new FlattenCompiler(
					new OrderCompiler(
							new KindCompiler(Kind.SX_DQT, PushConstCompiler.INSTANCE),
							//new KindCompiler(Kind.SX_QTE, PushConstCompiler.INSTANCE),
							FallbackCompiler.INSTANCE
					),
					new MandatoryCompiler(new OrderCompiler(
							new KindCompiler(Kind.SX_SQR_OPEN, PushConstCompiler.INSTANCE),
							new KindCompiler(Kind.SX_SQR_CLOSE, PushConstCompiler.INSTANCE),
							new KindCompiler(Kind.SX_CMA, PushConstCompiler.INSTANCE),
							WhitespaceCompiler.INSTANCE
					))
			));

	/**
	 * Utility classes must not be initialized.
	 *
	 * @throws AssertionError when called.
	 * @since 0.2.0 ~2021.05.21
	 */
	private Compilers() {
		throw new AssertionError("No instance for you");
	}
}
