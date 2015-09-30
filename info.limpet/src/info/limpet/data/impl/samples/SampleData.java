package info.limpet.data.impl.samples;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IObjectCollection;
import info.limpet.IQuantityCollection;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.samples.StockTypes.Temporal.ElapsedTime_Sec;
import info.limpet.data.operations.AddQuantityOperation;
import info.limpet.data.operations.MultiplyQuantityOperation;
import info.limpet.data.store.InMemoryStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Speed;

import tec.units.ri.quantity.Quantities;
import tec.units.ri.quantity.QuantityRange;
import tec.units.ri.unit.Units;

public class SampleData
{
	public static final String TIME_INTERVALS = "Time intervals";
	public static final String STRING_TWO = "String two";
	public static final String STRING_ONE = "String one";
	public static final String LENGTH_SINGLETON = "Length Singleton";
	public static final String LENGTH_TWO = "Length Two non-Time";
	public static final String LENGTH_ONE = "Length One non-Time";
	public static final String SPEED_ONE = "Speed One Time";
	public static final String SPEED_TWO = "Speed Two Time";
	public static final String RANGED_SPEED_SINGLETON = "Ranged Speed Singleton";
	public static final String FLOATING_POINT_FACTOR = "Floating point factor";

	public InMemoryStore getData()
	{
		InMemoryStore res = new InMemoryStore();


		// // collate our data series
		StockTypes.Temporal.Speed_MSec speedSeries1 = new StockTypes.Temporal.Speed_MSec(
				SPEED_ONE);
		StockTypes.Temporal.Speed_MSec speedSeries2 = new StockTypes.Temporal.Speed_MSec(
				SPEED_TWO);
		StockTypes.Temporal.Speed_MSec speedSeries3 = new StockTypes.Temporal.Speed_MSec(
				"Speed Three (longer)");
		StockTypes.NonTemporal.Length_M length1 = new StockTypes.NonTemporal.Length_M(
				LENGTH_ONE);
		StockTypes.NonTemporal.Length_M length2 = new StockTypes.NonTemporal.Length_M(
				LENGTH_TWO);
		IObjectCollection<String> string1 = new ObjectCollection<String>(
				STRING_ONE);
		IObjectCollection<String> string2 = new ObjectCollection<String>(
				STRING_TWO);
		IQuantityCollection<Dimensionless> singleton1 = new QuantityCollection<Dimensionless>(
				FLOATING_POINT_FACTOR, Units.ONE.asType(Dimensionless.class));
		StockTypes.NonTemporal.Speed_MSec singletonRange1 = new StockTypes.NonTemporal.Speed_MSec(
				RANGED_SPEED_SINGLETON);
		StockTypes.NonTemporal.Length_M singletonLength = new StockTypes.NonTemporal.Length_M(
				LENGTH_SINGLETON);
		ElapsedTime_Sec timeIntervals = new StockTypes.Temporal.ElapsedTime_Sec(TIME_INTERVALS);
		
		
		long thisTime = 0; 
		
		for (int i = 1; i <= 10; i++)
		{
			thisTime = new Date().getTime() + i * 500 * 60;

			speedSeries1.add(thisTime, 1 / Math.sin(i));
			speedSeries2.add(thisTime, Math.sin(i));
			speedSeries3.add(thisTime, 3d * Math.cos(i));
			length1.add((double)i % 3);
			length2.add((double)i % 5);
			string1.add("item " + i);
			string2.add("item " + (i % 3));
			timeIntervals.add(thisTime, 3 + Math.sin(i) * Math.random() * 4);
		}

		// add an extra item to speedSeries3
		speedSeries3.add(thisTime + 12 * 500 * 60, 12);
		
		// give the singleton a value		
		singleton1.add(4d);
		singletonRange1.add(998);
		@SuppressWarnings("unchecked")
		QuantityRange<Speed> speedRange = QuantityRange.of( Quantities.getQuantity(940d, singletonRange1.getUnits()),  Quantities.getQuantity(1050d, singletonRange1.getUnits()), null);
		singletonRange1.setRange(speedRange);

		singletonLength.add(12d);

		List<ICollection> list = new ArrayList<ICollection>();

		list.add(speedSeries1);
		list.add(speedSeries2);
		list.add(speedSeries3);
		list.add(length1);
		list.add(length2);
		list.add(string1);
		list.add(string2);
		list.add(singleton1);
		list.add(singletonRange1);
		list.add(singletonLength);
		list.add(timeIntervals);

		res.add(list);

		// perform an operation, so we have some audit trail
		List<ICollection> selection = new ArrayList<ICollection>();
		selection.add(speedSeries1);
		selection.add(speedSeries2);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Collection<ICommand<?>> actions = new AddQuantityOperation().actionsFor(selection, res);
		ICommand<?> addAction = actions.iterator().next();
		addAction.execute();	
		
		// and an operation using our speed factor
		selection.clear();
		selection.add(speedSeries1);
		selection.add(singleton1);
		Collection<ICommand<ICollection>> actions2 = new MultiplyQuantityOperation().actionsFor(selection, res);
		addAction = actions2.iterator().next();
		addAction.execute();

		// calculate the distance travelled
		selection.clear();
		selection.add(timeIntervals);
		selection.add(singletonRange1);
		Collection<ICommand<ICollection>> actions3 = new MultiplyQuantityOperation("Calculated distance").actionsFor(selection, res);
		addAction = actions3.iterator().next();
		addAction.execute();

		
		return res;
	}
}
