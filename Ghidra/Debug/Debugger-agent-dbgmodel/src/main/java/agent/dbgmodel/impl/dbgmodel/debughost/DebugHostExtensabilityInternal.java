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
package agent.dbgmodel.impl.dbgmodel.debughost;

import java.util.Map;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid.REFIID;

import agent.dbgmodel.dbgmodel.debughost.DebugHostExtensability;
import agent.dbgmodel.impl.dbgmodel.DbgModelUtil;
import agent.dbgmodel.impl.dbgmodel.DbgModelUtil.InterfaceSupplier;
import agent.dbgmodel.jna.dbgmodel.debughost.IDebugHostExtensability;
import agent.dbgmodel.jna.dbgmodel.debughost.WrapIDebugHostExtensability;
import ghidra.util.datastruct.WeakValueHashMap;

public interface DebugHostExtensabilityInternal extends DebugHostExtensability {
	Map<Pointer, DebugHostExtensabilityInternal> CACHE = new WeakValueHashMap<>();

	static DebugHostExtensabilityInternal instanceFor(WrapIDebugHostExtensability data) {
		return DbgModelUtil.lazyWeakCache(CACHE, data, DebugHostExtensabilityImpl::new);
	}

	Map<REFIID, Class<? extends WrapIDebugHostExtensability>> PREFERRED_DATA_SPACES_IIDS =
		Map.ofEntries(
			Map.entry(new REFIID(IDebugHostExtensability.IID_IDEBUG_HOST_EXTENSABILITY),
				WrapIDebugHostExtensability.class));

	static DebugHostExtensabilityInternal tryPreferredInterfaces(InterfaceSupplier supplier) {
		return DbgModelUtil.tryPreferredInterfaces(DebugHostExtensabilityInternal.class,
			PREFERRED_DATA_SPACES_IIDS, supplier);
	}
}
