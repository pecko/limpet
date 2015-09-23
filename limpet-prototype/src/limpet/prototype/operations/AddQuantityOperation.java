package limpet.prototype.operations;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Speed;

import limpet.prototype.commands.AbstractCommand;
import limpet.prototype.commands.ICommand;
import limpet.prototype.generics.dinko.impl.QuantityCollection;
import limpet.prototype.generics.dinko.interfaces.ICollection;
import limpet.prototype.generics.dinko.interfaces.IQuantityCollection;
import limpet.prototype.store.IStore;
import tec.units.ri.quantity.DefaultQuantityFactory;
import tec.units.ri.unit.MetricPrefix;
import tec.units.ri.unit.Units;

public class AddQuantityOperation extends BaseOperation
{
	@Override
	public ICommand[] actionsFor(ICollection[] selection, IStore destination)
	{
		ICommand[] res = null;
		if (appliesTo(selection))
		{
			AddQuantityValues newC = new AddQuantityValues(selection, destination);
			res = new ICommand[]
			{ newC };
		}

		return res;
	}

	protected boolean appliesTo(ICollection[] selection)
	{
		// are the all temporal?
		boolean allValid = true;
		int size = -1;

		for (int i = 0; i < selection.length; i++)
		{
			ICollection thisC = selection[i];
			if (thisC.isQuantity())
			{
				// great, valid, check the size
				if (size == -1)
				{
					size = thisC.size();
				}
				else
				{
					if (size != thisC.size())
					{
						// oops, no
						allValid = false;
						break;
					}
				}
			}
			else
			{
				// oops, no
				allValid = false;
				break;
			}

		}

		return allValid;
	}

	public static class AddQuantityValues extends AbstractCommand
	{

		private ICollection[] _series;

		public AddQuantityValues(ICollection[] series, IStore store)
		{
			super("Add series", "Add numeric values in provided series", store,
					false, false);
			_series = series;
		}

		@Override
		public void execute()
		{
			// ok, generate the new series
			Unit<Speed> kmh = MetricPrefix.KILO(Units.METRE).divide(Units.HOUR)
					.asType(Speed.class);
			IQuantityCollection<Speed> target = new QuantityCollection<Speed>(
					"Speed Total", kmh);

			// start adding values.
			for (int j = 0; j < _series[0].size(); j++)
			{
				double runningTotal = 0;
				for (int i = 0; i < _series.length; i++)
				{
					@SuppressWarnings("unchecked")
					IQuantityCollection<Speed> thisC = (IQuantityCollection<Speed>) _series[i];
					Speed thisQ = (Speed) thisC.getValues().get(j);
					runningTotal += thisQ.getValue().doubleValue();
				}
				
				Quantity<Speed> value = (Quantity<Speed>) DefaultQuantityFactory.getInstance(Speed.class)
						.create(runningTotal, (Unit<Speed>) kmh);

				target.add(value);
			}
			
			// ok, done
			getStore().add(new ICollection[]{target});

		}
	}

}