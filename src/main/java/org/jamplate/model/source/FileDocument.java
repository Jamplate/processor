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

import java.io.*;
import java.util.Objects;

/**
 * A document that delegates to a {@link File}.
 *
 * @author LSafer
 * @version 0.0.2
 * @since 0.0.2 ~2021.01.13
 */
public class FileDocument implements Document {
	/**
	 * The file of this file document.
	 *
	 * @since 0.0.2 ~2021.01.13
	 */
	protected final File file;

	/**
	 * Construct a new document for the given {@code file}.
	 *
	 * @param file the file for the constructed document.
	 * @throws NullPointerException if the given {@code file} is null.
	 * @since 0.0.2 ~2021.01.13
	 */
	public FileDocument(File file) {
		Objects.requireNonNull(file, "file");
		this.file = file;
	}

	@Override
	public String name() {
		return this.file.getName();
	}

	@Override
	public InputStream openInputStream() throws FileNotFoundException {
		return new FileInputStream(this.file);
	}

	@Override
	public Reader openReader() throws FileNotFoundException {
		return new FileReader(this.file);
	}

	@Override
	public String qualifiedName() {
		return this.file.toString();
	}

	@Override
	public String readContent() throws IOException {
		try (Reader reader = new FileReader(this.file)) {
			StringBuilder builder = new StringBuilder();
			char[] buffer = new char[1024];

			while (true) {
				int l = reader.read(buffer);

				if (l < 0)
					return builder.toString();
				if (l > 0)
					builder.append(buffer, 0, l);
			}
		}
	}

	@Override
	public String simpleName() {
		//noinspection DynamicRegexReplaceableByCompiledPattern
		return this.file.getName().replaceAll("[.][^.]*$", "");
	}

	@Override
	public String toString() {
		return this.file.toString();
	}

	/**
	 * Get the file this document is delegating to.
	 *
	 * @return the file of this.
	 * @since 0.0.2 ~2021.01.13
	 */
	public File file() {
		return this.file;
	}
}
