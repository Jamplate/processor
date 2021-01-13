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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Comparator;

/**
 * An interface that abstracts the functionality required to deal with source-code files.
 *
 * @author LSafer
 * @version 0.0.2
 * @since 0.0.2 ~2021.01.13
 */
public interface Document {
	/**
	 * The default comparator that compares documents.
	 *
	 * @since 0.0.2 ~2021.01.13
	 */
	Comparator<Document> COMPARATOR = Comparator.comparing(Document::qualifiedName);

	/**
	 * Return the name of this document.
	 *
	 * @return the name of this document.
	 * @since 0.0.2 ~2021.01.13
	 */
	String name();

	/**
	 * Open a new input-stream that reads the content of this document.
	 *
	 * @return a new input-stream that reads the content of this document.
	 * @throws IOException if any I/O exception occurs.
	 * @since 0.0.2 ~2021.01.13
	 */
	InputStream openInputStream() throws IOException;

	/**
	 * Open a new reader that reads the content of this document.
	 *
	 * @return a new reader that reads the content of this document.
	 * @throws IOException if any I/O exception occurs. (optional)
	 * @since 0.0.2 ~2021.01.13
	 */
	Reader openReader() throws IOException;

	/**
	 * Return the qualified name of this document. The qualified name usually refers to
	 * the full path where this document belong.
	 *
	 * @return the qualified name of this document.
	 * @since 0.0.2 ~2021.01.13
	 */
	String qualifiedName();

	/**
	 * Read the content of this document.
	 *
	 * @return the content of this document.
	 * @throws IOException if any I/O exception occurs. (optional)
	 * @since 0.0.2 ~2021.01.13
	 */
	String readContent() throws IOException;

	/**
	 * Return the simple name of this document. The simple name usually refers to the name
	 * but without any extensions (like {@code .class}, {@code .java} and {@code
	 * .jamplate}).
	 *
	 * @return the simple name of this document.
	 * @since 0.0.2 ~2021.01.13
	 */
	String simpleName();
}
