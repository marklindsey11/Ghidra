/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ghidra.pcode.exec.trace;

import ghidra.pcode.exec.trace.data.PcodeTraceDataAccess;

/**
 * A state composing a single {@link RequireIsKnownTraceCachedWriteBytesPcodeExecutorState}
 */
public class RequireIsKnownTraceCachedWriteBytesPcodeExecutorState
		extends DefaultTracePcodeExecutorState<byte[]> {

	/**
	 * Create the state
	 * 
	 * @param data the trace-data access shim
	 */
	public RequireIsKnownTraceCachedWriteBytesPcodeExecutorState(PcodeTraceDataAccess data) {
		super(new RequireIsKnownTraceCachedWriteBytesPcodeExecutorStatePiece(data));
	}

	protected RequireIsKnownTraceCachedWriteBytesPcodeExecutorState(
			TracePcodeExecutorStatePiece<byte[], byte[]> piece) {
		super(piece);
	}

	@Override
	public RequireIsKnownTraceCachedWriteBytesPcodeExecutorState fork() {
		return new RequireIsKnownTraceCachedWriteBytesPcodeExecutorState(piece.fork());
	}
}
