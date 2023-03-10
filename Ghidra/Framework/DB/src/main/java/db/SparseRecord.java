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
package db;

import java.io.IOException;
import java.util.ArrayList;

public class SparseRecord extends DBRecord {

	SparseRecord(Schema schema, Field key) {
		super(schema, key);
	}

	@Override
	int computeLength() {
		int len = 1; // sparse field count always written as byte after non-sparse fields
		Field[] fields = getFields();
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			if (schema.isSparseColumn(i)) {
				if (!f.isNull()) {
					// sparse field if present will be prefixed by a byte
					// indicating the column index
					len += f.length() + 1;
				}
			}
			else {
				len += f.length();
			}
		}
		return len;
	}

	@Override
	public void write(Buffer buf, int offset) throws IndexOutOfBoundsException, IOException {
		ArrayList<Integer> sparseFieldIndexes = new ArrayList<>();
		Field[] fields = getFields();
		for (int i = 0; i < fields.length; i++) {
			if (schema.isSparseColumn(i)) {
				if (!fields[i].isNull()) {
					sparseFieldIndexes.add(i);
				}
			}
			else {
				offset = fields[i].write(buf, offset);
			}
		}
		// write sparse field count
		buf.putByte(offset++, (byte) sparseFieldIndexes.size());
		// write each non-null sparse field
		for (int i : sparseFieldIndexes) {
			Field f = fields[i];
			if (!f.isNull()) {
				// sparse field if present will be prefixed by a byte
				// indicating the column index
				buf.putByte(offset++, (byte) i); // sparse field index
				offset = f.write(buf, offset); // sparse field data
			}
		}
		dirty = false;
	}

	@Override
	public void read(Buffer buf, int offset) throws IndexOutOfBoundsException, IOException {
		Field[] fields = getFields();
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			if (schema.isSparseColumn(i)) {
				f.setNull();
			}
			else {
				offset = f.read(buf, offset);
			}
		}
		int sparseFieldCount = buf.getByte(offset++);
		for (int i = 0; i < sparseFieldCount; i++) {
			int index = buf.getByte(offset++); // sparse field index
			offset = fields[index].read(buf, offset); // sparse field data
		}
		dirty = false;
	}

	/**
	 * Check for a change in a sparse column's storage size when setting a non-null primitive value.
	 * All primitive value storage is fixed-length and only varies for a sparse column when
	 * transitioning between a null and non-null state.
	 * @param colIndex field column index within this record.
	 * @return true for a sparse column which is transitioning between a null and non-null state,
	 * else false.
	 */
	private boolean changeInSparsePrimitiveStorage(int colIndex) {
		if (!schema.isSparseColumn(colIndex)) {
			return false;
		}
		return getField(colIndex).isNull();
	}

	/**
	 * Check for a change in a sparse column's storage size when setting a binary value.
	 * This method only checks for a sparse column's transition between a null and non-null state.
	 * While this is the only length change consideration needed for a fixed-length field (e.g. 
	 * {@link FixedField}, {@link PrimitiveField}), record length invalidation due to a change
	 * in variable-length {@link Field} data must be handled separately.
	 * @param colIndex field column index within this record.
	 * @return true for a sparse column which is transitioning between a null and non-null state,
	 * else false.
	 */
	private boolean changeInSparseStorage(int colIndex, byte[] newValue) {
		if (!schema.isSparseColumn(colIndex)) {
			return false;
		}
		boolean oldIsNull = getField(colIndex).isNull();
		boolean newIsNull = newValue == null;
		return oldIsNull != newIsNull;
	}

	/**
	 * Check for a change in a sparse column's storage size when setting a column to the null state.
	 * @param colIndex field column index within this record.
	 * @return true for a sparse column which is transitioning between a null and non-null state,
	 * else false.
	 */
	private boolean changeInSparseNullStorage(int colIndex) {
		if (!schema.isSparseColumn(colIndex)) {
			return false;
		}
		return !getField(colIndex).isNull();
	}

	@Override
	public void setField(int colIndex, Field value) {
		if (value == null) {
			if (!schema.isSparseColumn(colIndex)) {
				throw new IllegalArgumentException("null value supported for sparse column only");
			}
			value = getField(colIndex).newField();
			value.setNull();
		}
		super.setField(colIndex, value);
	}

	@Override
	public void setLongValue(int colIndex, long value) {
		if (changeInSparsePrimitiveStorage(colIndex)) {
			invalidateLength();
		}
		super.setLongValue(colIndex, value);
	}

	@Override
	public void setIntValue(int colIndex, int value) {
		if (changeInSparsePrimitiveStorage(colIndex)) {
			invalidateLength();
		}
		super.setIntValue(colIndex, value);
	}

	@Override
	public void setShortValue(int colIndex, short value) {
		if (changeInSparsePrimitiveStorage(colIndex)) {
			invalidateLength();
		}
		super.setShortValue(colIndex, value);
	}

	@Override
	public void setByteValue(int colIndex, byte value) {
		if (changeInSparsePrimitiveStorage(colIndex)) {
			invalidateLength();
		}
		super.setByteValue(colIndex, value);
	}

	@Override
	public void setBooleanValue(int colIndex, boolean value) {
		if (changeInSparsePrimitiveStorage(colIndex)) {
			invalidateLength();
		}
		super.setBooleanValue(colIndex, value);
	}

	@Override
	public void setBinaryData(int colIndex, byte[] bytes) {
		if (changeInSparseStorage(colIndex, bytes)) {
			invalidateLength();
		}
		super.setBinaryData(colIndex, bytes);
	}

	@Override
	public void setNull(int colIndex) {
		if (changeInSparseNullStorage(colIndex)) {
			invalidateLength();
		}
		super.setNull(colIndex);
	}
}
