/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet;


import java.util.List;


/** encapsulation of some change to data
 * 
 * @author ian
 *
 */
public interface ICommand<T extends IStoreItem> extends IChangeListener, IStoreItem
{
	String getDescription();
	void execute();
	void undo();
	void redo();
	boolean canUndo();
	boolean canRedo();
	List<T> getOutputs();
	List<T> getInputs();
	boolean getDynamic();
	void setDynamic(boolean dynamic);
	
	String NEW_DATASET_MESSAGE = "Provide name for new dataset"; 
	
}
