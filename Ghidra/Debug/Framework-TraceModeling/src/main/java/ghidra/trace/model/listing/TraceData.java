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
package ghidra.trace.model.listing;

import ghidra.program.model.listing.Data;
import ghidra.trace.model.Trace;
import ghidra.trace.model.symbol.TraceReference;

/**
 * A data unit in a {@link Trace}
 */
public interface TraceData extends TraceCodeUnit, Data {
	@Override
	TraceData getComponent(int index);

	@Override
	TraceData getComponentAt(int offset);

	@Override
	TraceData getComponentContaining(int offset);

	@Override
	TraceData getComponent(int[] componentPath);

	@Override
	TraceData getPrimitiveAt(int offset);

	@Override
	TraceReference[] getValueReferences();
}
