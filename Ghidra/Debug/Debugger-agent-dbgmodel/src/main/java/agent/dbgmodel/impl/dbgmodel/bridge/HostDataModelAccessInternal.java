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
package agent.dbgmodel.impl.dbgmodel.bridge;

import java.util.Map;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid.REFIID;

import agent.dbgmodel.dbgmodel.bridge.HostDataModelAccess;
import agent.dbgmodel.impl.dbgmodel.DbgModelUtil;
import agent.dbgmodel.impl.dbgmodel.DbgModelUtil.InterfaceSupplier;
import agent.dbgmodel.jna.dbgmodel.bridge.IHostDataModelAccess;
import agent.dbgmodel.jna.dbgmodel.bridge.WrapIHostDataModelAccess;
import ghidra.util.datastruct.WeakValueHashMap;

public interface HostDataModelAccessInternal extends HostDataModelAccess {
	Map<Pointer, HostDataModelAccessInternal> CACHE = new WeakValueHashMap<>();

	static HostDataModelAccessInternal instanceFor(WrapIHostDataModelAccess data) {
		return DbgModelUtil.lazyWeakCache(CACHE, data, HostDataModelAccessImpl::new);
	}

	Map<REFIID, Class<? extends WrapIHostDataModelAccess>> PREFERRED_DATA_SPACES_IIDS =
		Map.ofEntries(
			Map.entry(new REFIID(IHostDataModelAccess.IID_IHOST_DATA_MODEL_ACCESS),
				WrapIHostDataModelAccess.class));

	static HostDataModelAccessInternal tryPreferredInterfaces(InterfaceSupplier supplier) {
		return DbgModelUtil.tryPreferredInterfaces(HostDataModelAccessInternal.class,
			PREFERRED_DATA_SPACES_IIDS, supplier);
	}
}
