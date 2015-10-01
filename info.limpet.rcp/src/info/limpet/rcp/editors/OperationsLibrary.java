package info.limpet.rcp.editors;

import info.limpet.ICollection;
import info.limpet.IOperation;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.operations.AddQuantityOperation;
import info.limpet.data.operations.DeleteCollectionOperation;
import info.limpet.data.operations.DivideQuantityOperation;
import info.limpet.data.operations.GenerateDummyDataOperation;
import info.limpet.data.operations.MultiplyQuantityOperation;
import info.limpet.data.operations.SubtractQuantityOperation;
import info.limpet.data.operations.UnitConversionOperation;
import info.limpet.rcp.analysis_view.AnalysisView;
import info.limpet.rcp.data_frequency.DataFrequencyView;
import info.limpet.rcp.operations.ShowInNamedView;
import info.limpet.rcp.xy_plot.XyPlotView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tec.units.ri.unit.Units;

public class OperationsLibrary
{
	public static HashMap<String, List<IOperation<?>>> getOperations()
	{
		HashMap<String, List<IOperation<?>>> res = new HashMap<String, List<IOperation<?>>>();

		res.put("Arithmetic", getArithmetic());
		res.put("Conversions", getConversions());
		res.put("Administration", getAdmin());
		res.put("Analysis", getAnalysis());
		return res;

	}

	private static List<IOperation<?>> getAnalysis()
	{
		List<IOperation<?>> analysis = new ArrayList<IOperation<?>>();
		analysis.add(new ShowInNamedView("Show in XY Plot View", XyPlotView.ID)
		{
			protected boolean appliesTo(List<ICollection> selection)
			{
				return getTests().nonEmpty(selection) && getTests().allQuantity(selection);
			}
		});
		analysis.add(new ShowInNamedView("Show in Data Frequency View",
				DataFrequencyView.ID));
		analysis.add(new ShowInNamedView("Show in Analysis View", AnalysisView.ID));
		return analysis;
	}

	public static List<IOperation<?>> getTopLevel()
	{
		List<IOperation<?>> topLevel = new ArrayList<IOperation<?>>();
		topLevel.add(new DeleteCollectionOperation());
		return topLevel;
	}

	private static List<IOperation<?>> getAdmin()
	{
		List<IOperation<?>> admin = new ArrayList<IOperation<?>>();
		admin.add(new GenerateDummyDataOperation("small", 10));
		admin.add(new GenerateDummyDataOperation("large", 1000));
		admin.add(new GenerateDummyDataOperation("monster", 1000000));

		return admin;
	}

	private static List<IOperation<?>> getArithmetic()
	{
		List<IOperation<?>> arithmetic = new ArrayList<IOperation<?>>();
		arithmetic.add(new MultiplyQuantityOperation());
		arithmetic.add(new AddQuantityOperation<>());
		arithmetic.add(new SubtractQuantityOperation<>());
		arithmetic.add(new DivideQuantityOperation());
		return arithmetic;
	}

	private static List<IOperation<?>> getConversions()
	{
		List<IOperation<?>> conversions = new ArrayList<IOperation<?>>();

		// Length
		conversions.add(new UnitConversionOperation(Units.METRE));

		// Time
		conversions.add(new UnitConversionOperation(Units.SECOND));
		conversions.add(new UnitConversionOperation(Units.MINUTE));

		// Speed
		conversions.add(new UnitConversionOperation(Units.METRES_PER_SECOND));

		// Acceleration
		conversions
				.add(new UnitConversionOperation(Units.METRES_PER_SQUARE_SECOND));

		// Temperature
		conversions.add(new UnitConversionOperation(Units.CELSIUS));

		// Angle
		conversions.add(new UnitConversionOperation(Units.RADIAN));
		conversions.add(new UnitConversionOperation(StockTypes.DEGREE_ANGLE));
		return conversions;
	}
}