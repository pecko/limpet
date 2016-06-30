package info.limpet.stackedcharts.ui.editor.drop;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.ui.editor.commands.AddScatterSetsToChartCommand;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.dnd.DropTargetEvent;

public class ScatterSetToChartDropTargetListener extends ScatterSetDropTargetListener
{
  public ScatterSetToChartDropTargetListener(GraphicalViewer viewer)
  {
    super(viewer);
  }

  @Override
  public boolean appliesTo(DropTargetEvent event)
  {
    EditPart findObjectAt = findPart(event);
    return findObjectAt instanceof ChartEditPart;
  }

  protected Command createCommand(AbstractGraphicalEditPart chart,
      List<ScatterSet> scatterSets)
  {
    AddScatterSetsToChartCommand addDatasetsToAxisCommand =
        new AddScatterSetsToChartCommand((Chart) chart.getModel(),
            scatterSets.toArray(new ScatterSet[scatterSets.size()]));
    return addDatasetsToAxisCommand;
  }

}