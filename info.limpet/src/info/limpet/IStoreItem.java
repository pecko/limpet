package info.limpet;

import java.util.UUID;

public interface IStoreItem
{

	/** if this object has children
	 * 
	 * @return
	 */
	boolean hasChildren();
	
	/** find the layer that contains this collection (or null if applicable)
	 * 
	 * @return parent collection, or null
	 */
	IStoreGroup getParent();
	
	/** set the parent object for this collection
	 * 
	 * @param parent
	 */
	void setParent(IStoreGroup parent);
	
	String getName();

	void addChangeListener(IChangeListener listener);

	void removeChangeListener(IChangeListener listener);

	/**
	 * indicate that the collection has changed Note: both registeered listeners
	 * and dependents are informed of the change
	 */
	void fireDataChanged();

	UUID getUUID();
}
