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
package org.jamplate.model.document;

import org.jamplate.model.Name;

import java.io.*;
import java.util.Comparator;
import java.util.stream.IntStream;

/**
 * An interface that abstracts the functionality required to deal with source-code files.
 * <br>
 * The document should serialize its {@link #name()}. It is not encouraged to serialized
 * additional data.
 * <br>
 * If a document is a deserialized document then any method that attempts to read the
 * content or its length will throw an {@link IllegalStateException}.
 *
 * @author LSafer
 * @version 0.2.0
 * @since 0.2.0 ~2021.01.13
 */
public interface Document extends Serializable {
	/**
	 * The standard document comparator. This comparator is sorting documents by their
	 * qualified name. (natural ordering)
	 *
	 * @since 0.2.0 ~2021.01.13
	 */
	Comparator<Document> COMPARATOR = Comparator.comparing(Document::name);

	/**
	 * Determines if the given {@code object} equals this document or not. An object
	 * equals a document when that object is a document and has the same {@link #name()}
	 * as this document. (regardless of its content, assuming the user is honest and does
	 * not provide two documents with same qualified name but from different origins or
	 * have different content)
	 * <pre>
	 *     equals = object instanceof Document &&
	 *     			this.name.equals(object.name)
	 * </pre>
	 *
	 * @param object the object to be matched.
	 * @return true, if the given {@code object} is a document and equals this document.
	 * @since 0.2.0 ~2021.01.13
	 */
	@Override
	boolean equals(Object object);

	/**
	 * Calculates the hashCode of this document. The hash code of a document is the hash
	 * code of the {@link #name()} of it.
	 * <pre>
	 *     hashCode = &lt;NameHashCode&gt;
	 * </pre>
	 *
	 * @return the hash code of this document.
	 * @since 0.2.0 ~2021.01.13
	 */
	@Override
	int hashCode();

	/**
	 * Returns a string representation of this document. The string representation of a
	 * document is the qualified form of its {@link #name()}.
	 * <pre>
	 *     toString = &lt;NameToQualifiedString&gt;
	 * </pre>
	 *
	 * @return a string representation of this document.
	 * @since 0.2.0 ~2021.01.13
	 */
	@Override
	String toString();

	/**
	 * Returns the length of this document. This method must always return the same value
	 * on the same instance.
	 *
	 * @return the length of this document.
	 * @throws IllegalStateException if this document is a deserialized document.
	 * @throws IOError               if any I/O exception occurs.
	 * @since 0.2.0 ~2021.01.17
	 */
	int length();

	/**
	 * Returns an ordered stream of the positions of the lines in this document.
	 *
	 * @return a stream of the lines in this document.
	 * @throws IllegalStateException if this document is a deserialized document.
	 * @throws IOError               if any I/O exception occurs.
	 * @since 0.2.0 ~2021.01.27
	 */
	IntStream lines();

	/**
	 * Return the name of this document.
	 *
	 * @return the name of this document.
	 * @since 0.2.0 ~2021.01.13
	 */
	Name name();

	/**
	 * Open a new input-stream that reads the content of this document.
	 *
	 * @return a new input-stream that reads the content of this document.
	 * @throws IOException           if any I/O exception occurs.
	 * @throws IllegalStateException if this document is a deserialized document.
	 * @since 0.2.0 ~2021.01.13
	 */
	InputStream openInputStream() throws IOException;

	/**
	 * Open a new reader that reads the content of this document.
	 *
	 * @return a new reader that reads the content of this document.
	 * @throws IOException           if any I/O exception occurs. (optional)
	 * @throws IllegalStateException if this document is a deserialized document.
	 * @since 0.2.0 ~2021.01.13
	 */
	Reader openReader() throws IOException;

	/**
	 * Read the content of this document. Once the content read, it should be cached. So,
	 * invoking this method multiple times must be easy to perform.
	 *
	 * @return the content of this document. (unmodifiable view)
	 * @throws IOError               if any I/O exception occurs. (optional)
	 * @throws IllegalStateException if this document is a deserialized document.
	 * @since 0.2.0 ~2021.01.13
	 */
	CharSequence readContent();
}
