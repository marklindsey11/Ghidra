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
package agent.dbgmodel.impl.dbgmodel.concept;

import java.util.Map;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid.REFIID;

import agent.dbgmodel.dbgmodel.concept.DynamicConceptProviderConcept;
import agent.dbgmodel.impl.dbgmodel.DbgModelUtil;
import agent.dbgmodel.impl.dbgmodel.DbgModelUtil.InterfaceSupplier;
import agent.dbgmodel.jna.dbgmodel.concept.IDynamicConceptProviderConcept;
import agent.dbgmodel.jna.dbgmodel.concept.WrapIDynamicConceptProviderConcept;
import ghidra.util.datastruct.WeakValueHashMap;

public interface DynamicConceptProviderConceptInternal extends DynamicConceptProviderConcept {
	Map<Pointer, DynamicConceptProviderConceptInternal> CACHE = new WeakValueHashMap<>();

	static DynamicConceptProviderConceptInternal instanceFor(
			WrapIDynamicConceptProviderConcept data) {
		return DbgModelUtil.lazyWeakCache(CACHE, data, DynamicConceptProviderConceptImpl::new);
	}

	Map<REFIID, Class<? extends WrapIDynamicConceptProviderConcept>> PREFERRED_DATA_SPACES_IIDS =
		Map.ofEntries(
			Map.entry(
				new REFIID(
					IDynamicConceptProviderConcept.IID_IDYNAMIC_CONCEPT_PROVIDER_CONCEPT),
				WrapIDynamicConceptProviderConcept.class));

	static DynamicConceptProviderConceptInternal tryPreferredInterfaces(
			InterfaceSupplier supplier) {
		return DbgModelUtil.tryPreferredInterfaces(DynamicConceptProviderConceptInternal.class,
			PREFERRED_DATA_SPACES_IIDS, supplier);
	}
}
