package limpet.prototype.ian_generics;

import java.util.Collection;

public interface IObjectCollection<T extends Object> extends ICollection {
	public void add(T object);
	
	public Collection<T> getValues();
}
