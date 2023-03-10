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
package ghidra.trace.database.listing;

import java.util.Iterator;
import java.util.stream.StreamSupport;

import ghidra.program.model.address.*;
import ghidra.trace.model.TraceAddressSnapRange;
import ghidra.trace.model.listing.TraceBaseCodeUnitsView;

/**
 * An abstract implementation of {@link TraceBaseCodeUnitsView} for composing views of many address
 * spaces, where the views include undefined units
 * 
 * @param <T> the implementation type of the units contained in the view. Must be a super-type of
 *            {@link UndefinedDBTraceData}.
 * @param <M> the implementation type of the views being composed
 */
public abstract class AbstractWithUndefinedDBTraceCodeUnitsMemoryView<T extends DBTraceCodeUnitAdapter, M extends AbstractBaseDBTraceCodeUnitsView<T>>
		extends AbstractBaseDBTraceCodeUnitsMemoryView<T, M> {

	/**
	 * Construct a composite view
	 * 
	 * @param manager the code manager, from which individual views are retrieved
	 */
	public AbstractWithUndefinedDBTraceCodeUnitsMemoryView(DBTraceCodeManager manager) {
		super(manager);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected T nullOrUndefined(long snap, Address address) {
		return (T) manager.doCreateUndefinedUnit(snap, address, null, 0);
	}

	@Override
	protected AddressSetView emptyOrFullAddressSetUndefined(AddressRange within) {
		return new AddressSet(within);
	}

	@Override
	protected boolean falseOrTrueUndefined() {
		return true;
	}

	@Override
	public Iterable<? extends T> emptyOrFullIterableUndefined(long snap, AddressRange range,
			boolean forward) {
		return () -> new Iterator<>() {
			Address address = forward ? range.getMinAddress() : range.getMaxAddress();

			@Override
			public boolean hasNext() {
				return address != null && (forward ? address.compareTo(range.getMaxAddress()) <= 0
						: address.compareTo(range.getMinAddress()) >= 0);
			}

			@Override
			public T next() {
				@SuppressWarnings("unchecked")
				T result = (T) manager.doCreateUndefinedUnit(snap, address, null, 0);
				address = forward ? address.next() : address.previous();
				return result;
			}
		};
	}

	@Override
	public Iterable<? extends T> emptyOrFullIterableUndefined(TraceAddressSnapRange tasr) {
		return () -> StreamSupport.stream(tasr.getLifespan().spliterator(), false)
				.flatMap(snap -> StreamSupport
						.stream(emptyOrFullIterableUndefined(snap, tasr.getRange(), true)
								.spliterator(),
							false)
						.map(t -> (T) t))
				.iterator();
	}
}
