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
package agent.dbgmodel.impl.dbgmodel.datamodel.script;

import java.util.Map;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid.REFIID;

import agent.dbgmodel.dbgmodel.datamodel.script.DataModelScriptProviderEnumerator;
import agent.dbgmodel.impl.dbgmodel.DbgModelUtil;
import agent.dbgmodel.impl.dbgmodel.DbgModelUtil.InterfaceSupplier;
import agent.dbgmodel.jna.dbgmodel.datamodel.script.IDataModelScriptProviderEnumerator;
import agent.dbgmodel.jna.dbgmodel.datamodel.script.WrapIDataModelScriptProviderEnumerator;
import ghidra.util.datastruct.WeakValueHashMap;

public interface DataModelScriptProviderEnumeratorInternal
		extends DataModelScriptProviderEnumerator {
	Map<Pointer, DataModelScriptProviderEnumeratorInternal> CACHE = new WeakValueHashMap<>();

	static DataModelScriptProviderEnumeratorInternal instanceFor(
			WrapIDataModelScriptProviderEnumerator data) {
		return DbgModelUtil.lazyWeakCache(CACHE, data, DataModelScriptProviderEnumeratorImpl::new);
	}

	Map<REFIID, Class<? extends WrapIDataModelScriptProviderEnumerator>> PREFERRED_DATA_SPACES_IIDS =
		Map.ofEntries(
			Map.entry(
				new REFIID(
					IDataModelScriptProviderEnumerator.IID_IDATA_MODEL_SCRIPT_PROVIDER_ENUMERATOR),
				WrapIDataModelScriptProviderEnumerator.class));

	static DataModelScriptProviderEnumeratorInternal tryPreferredInterfaces(
			InterfaceSupplier supplier) {
		return DbgModelUtil.tryPreferredInterfaces(DataModelScriptProviderEnumeratorInternal.class,
			PREFERRED_DATA_SPACES_IIDS, supplier);
	}
}
