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

import agent.dbgmodel.dbgmodel.debughost.DebugHostTypeSignature;
import agent.dbgmodel.impl.dbgmodel.DbgModelUtil;
import agent.dbgmodel.impl.dbgmodel.DbgModelUtil.InterfaceSupplier;
import agent.dbgmodel.jna.dbgmodel.debughost.IDebugHostTypeSignature;
import agent.dbgmodel.jna.dbgmodel.debughost.WrapIDebugHostTypeSignature;
import ghidra.util.datastruct.WeakValueHashMap;

public interface DebugHostTypeSignatureInternal extends DebugHostTypeSignature {
	Map<Pointer, DebugHostTypeSignatureInternal> CACHE = new WeakValueHashMap<>();

	static DebugHostTypeSignatureInternal instanceFor(WrapIDebugHostTypeSignature data) {
		return DbgModelUtil.lazyWeakCache(CACHE, data, DebugHostTypeSignatureImpl::new);
	}

	Map<REFIID, Class<? extends WrapIDebugHostTypeSignature>> PREFERRED_DATA_SPACES_IIDS =
		Map.ofEntries(
			Map.entry(new REFIID(IDebugHostTypeSignature.IID_IDEBUG_HOST_TYPE_SIGNATURE),
				WrapIDebugHostTypeSignature.class));

	static DebugHostTypeSignatureInternal tryPreferredInterfaces(InterfaceSupplier supplier) {
		return DbgModelUtil.tryPreferredInterfaces(DebugHostTypeSignatureInternal.class,
			PREFERRED_DATA_SPACES_IIDS, supplier);
	}
}
