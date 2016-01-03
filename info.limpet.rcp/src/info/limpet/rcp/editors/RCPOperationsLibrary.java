package info.limpet.rcp.editors;

import info.limpet.IOperation;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.rcp.analysis_view.AnalysisView;
import info.limpet.rcp.data_frequency.DataFrequencyView;
import info.limpet.rcp.operations.ShowInNamedView;
import info.limpet.rcp.range_slider.RangeSliderView;
import info.limpet.rcp.time_frequency.TimeFrequencyView;
import info.limpet.rcp.xy_plot.XyPlotView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RCPOperationsLibrary
{
	final static CollectionComplianceTests aTests = new CollectionComplianceTests();
	
	public static HashMap<String, List<IOperation<?>>> getOperations()
	{
		HashMap<String, List<IOperation<?>>> res = new HashMap<String, List<IOperation<?>>>();


		res.put("Analysis", getAnalysis());

		return res;

	}

	private static List<IOperation<?>> getAnalysis()
	{
		List<IOperation<?>> analysis = new ArrayList<IOperation<?>>();
		analysis.add(new ShowInNamedView("Show in XY Plot View", XyPlotView.ID)
		{
			protected boolean appliesTo(List<IStoreItem> selection)
			{
				return getTests().nonEmpty(selection)
						&& (getTests().allQuantity(selection) || getTests().allLocation(selection));
			}
		});
		analysis.add(new ShowInNamedView("Show in Time Frequency View",
				TimeFrequencyView.ID));
		analysis.add(new ShowInNamedView("Show in Data Frequency View",
				DataFrequencyView.ID));
		analysis.add(new ShowInNamedView("Show in Analysis View", AnalysisView.ID));
		analysis.add(new ShowInNamedView("Show in Range Slider", RangeSliderView.ID));
		return analysis;
	}
}