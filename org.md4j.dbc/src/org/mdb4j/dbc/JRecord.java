package org.mdb4j.dbc;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class JRecord implements Iterable<Object> {
	private final long index;
	private final List<Object> values;
	
	JRecord(final long index, final Object ...values) {
		this.index = index;
		this.values = Arrays.asList(values);
	}
		
	public long getIndex() {
		return index;
	}
	
	public Object getValue(final int index) {
		return values.get(index);
	}

	@Override
	public Iterator<Object> iterator() {
		return values.iterator();
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(index);
		sb.append(" [");
		int i = 0;
		for(final Object obj: values) {
			sb.append(obj); i++;
			if(i < values.size()) sb.append(", ");
		}
		sb.append("]");
		return sb.toString();
	}
}
