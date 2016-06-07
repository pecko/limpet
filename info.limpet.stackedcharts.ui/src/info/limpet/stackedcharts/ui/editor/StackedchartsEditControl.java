package info.limpet.stackedcharts.ui.editor;

import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.ui.editor.drop.DatasetToAxisDropTargetListener;
import info.limpet.stackedcharts.ui.editor.drop.DatasetToAxisLandingDropTargetListener;
import info.limpet.stackedcharts.ui.editor.drop.ProxyDropTargetListener;
import info.limpet.stackedcharts.ui.editor.parts.StackedChartsEditPartFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.view.ExtendedPropertySheetPage;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.gef.ui.actions.RedoAction;
import org.eclipse.gef.ui.actions.UndoAction;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;

public class StackedchartsEditControl extends Composite
{

  private final EditDomain editDomain;
  private final GraphicalViewer viewer;

  protected AdapterFactoryEditingDomain emfEditingDomain;
  private ComposedAdapterFactory adapterFactory;
  private ExtendedPropertySheetPage propertySheetPage;

  public StackedchartsEditControl(Composite parent)
  {
    super(parent, SWT.NONE);

    setLayout(new FillLayout());

    editDomain = new EditDomain();

    viewer = new ScrollingGraphicalViewer();

    // connect external Drop support
    // add Dataset to Axis

    viewer.addDropTargetListener(new ProxyDropTargetListener(
        new DatasetToAxisDropTargetListener(viewer),
        new DatasetToAxisLandingDropTargetListener(viewer)));

    viewer.createControl(this);
    editDomain.addViewer(viewer);

    viewer.getControl().setBackground(ColorConstants.listBackground);

    viewer.setEditPartFactory(new StackedChartsEditPartFactory());

    // emf edit domain
    adapterFactory =
        new ComposedAdapterFactory(
            ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
    BasicCommandStack commandStack = new BasicCommandStack();
    adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
    adapterFactory.addAdapterFactory(new EcoreItemProviderAdapterFactory());
    adapterFactory
        .addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
    emfEditingDomain =
        new AdapterFactoryEditingDomain(adapterFactory, commandStack);

  }

  /**
   * This accesses a cached version of the property sheet.
   */
  public IPropertySheetPage getPropertySheetPage()
  {
    if (propertySheetPage == null)
    {
      propertySheetPage = new ExtendedPropertySheetPage(emfEditingDomain)
      {
        @Override
        public void setSelectionToViewer(List<?> selection)
        {
          StackedchartsEditControl.this.setSelectionToViewer(selection);
        }
      };
      propertySheetPage
          .setPropertySourceProvider(new AdapterFactoryContentProvider(
              adapterFactory));
    }

    return propertySheetPage;
  }

  /**
   * This method should return a {@link org.eclipse.jface.viewers.StructuredSelection} containing
   * one or more of the viewer's EditParts underline EMF objects . If no editparts are selected,
   * root EMF object will return
   * 
   */
  public ISelection getSelection()
  {
    StructuredSelection selection = (StructuredSelection) viewer.getSelection();
    if (!selection.isEmpty())
    {
      List<Object> emfObj = new ArrayList<Object>();
      Object[] array = selection.toArray();
      for (Object object : array)
      {
        if (object instanceof AbstractEditPart)
        {
          emfObj.add(((AbstractEditPart) object).getModel());
        }
      }
      return new StructuredSelection(emfObj);
    }

    return viewer.getSelection();
  }

  private void setSelectionToViewer(Collection<?> collection)
  {
    final Collection<?> theSelection = collection;

    if (theSelection != null && !theSelection.isEmpty())
    {
      Runnable runnable = new Runnable()
      {
        public void run()
        {

          if (viewer != null)
          {
            viewer
                .setSelection(new StructuredSelection(theSelection.toArray()));
          }
        }
      };
      Display.getCurrent().asyncExec(runnable);
    }
  }

  public GraphicalViewer getViewer()
  {
    return viewer;
  }

  public void setModel(ChartSet model)
  {
    viewer.setContents(model);

  }

  public void init(IViewPart view)
  {
    IActionBars actionBars = view.getViewSite().getActionBars();
    IToolBarManager toolBarManager = actionBars.getToolBarManager();

    final UndoAction undoAction = new UndoAction(view);
    toolBarManager.add(undoAction);
    final RedoAction redoAction = new RedoAction(view);
    toolBarManager.add(redoAction);

    viewer.getEditDomain().getCommandStack().addCommandStackListener(
        new CommandStackListener()
        {

          @Override
          public void commandStackChanged(EventObject event)
          {
            undoAction.setEnabled(undoAction.isEnabled());
            redoAction.setEnabled(redoAction.isEnabled());
          }
        });

  }
}
