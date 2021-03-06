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
package info.limpet.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import info.limpet.ui.data_provider.data.DataModel;

/**
 * 
 * Temporary editor input for Limpet Data Provider editor
 * @author snpe
 *
 */
public class DataProviderEditorInput implements IEditorInput
{

	private DataModel model;

	public DataProviderEditorInput(DataModel model)
	{
		this.model = model;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter)
	{
		return null;
	}

	@Override
	public boolean exists()
	{
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return "Data Provider";
	}

	@Override
	public IPersistableElement getPersistable()
	{
		return null;
	}

	@Override
	public String getToolTipText()
	{
		return "Data Provider";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		DataProviderEditorInput other = (DataProviderEditorInput) obj;
		if (model == null)
		{
			if (other.model != null)
			{
				return false;
			}
		}
		else if (!model.equals(other.model))
		{
			return false;
		}
		return true;
	}

	public DataModel getModel()
	{
		return model;
	}

}
