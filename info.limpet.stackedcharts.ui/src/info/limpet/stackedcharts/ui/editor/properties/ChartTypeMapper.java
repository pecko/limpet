package info.limpet.stackedcharts.ui.editor.properties;

import org.eclipse.gef.EditPart;
import org.eclipse.ui.views.properties.tabbed.AbstractTypeMapper;

public class ChartTypeMapper extends AbstractTypeMapper
{

  @SuppressWarnings("rawtypes")
  @Override
  public Class mapType(Object object)
  {
    if (object instanceof EditPart)
    {
      return ((EditPart) object).getModel().getClass();
    }
    return super.mapType(object);
  }

}
