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
package info.limpet.data.impl;

import info.limpet.ICommand;
import info.limpet.IQuantityCollection;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.QuantityRange;
import info.limpet.UIProperty;
import info.limpet.data.impl.helpers.QuantityHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Dimension;
import javax.measure.unit.Unit;

public class TemporalQuantityCollection<T extends Quantity> extends
    TemporalObjectCollection<Measurable<T>> implements
    ITemporalQuantityCollection<T>, IQuantityCollection<T>
{
  private transient QuantityHelper<T> _qHelper;

  private QuantityRange<T> _range;

  private Unit<T> units;

  public TemporalQuantityCollection(String name, ICommand<?> precedent,
      Unit<T> units)
  {
    super(name, precedent);
    this.units = units;
    _qHelper =
        new QuantityHelper<T>((ArrayList<Measurable<T>>) getValues(), units);
  }

  @Override
  public void add(long time, Measurable<T> object)
  {
    if (object instanceof Measure)
    {
      Measure<?, ?> oM = (Measure<?, ?>) object;
      Unit<?> hisUnits = oM.getUnit();
      if (getUnits() != null && !getUnits().equals(hisUnits))
      {
        throw new RuntimeException("Measurement is in wrong units");
      }
    }
    // double-check the units
    super.add(time, object);
  }

  @Override
  public void add(long time, Number value)
  {
    super.add(time, Measure.valueOf(value.doubleValue(), getUnits()));
  }

  @Override
  public void add(Number value)
  {
    throw new UnsupportedOperationException(
        "Please use add(time, value) for time series datasets");
  }

  @Override
  public Measurable<T> min()
  {
    initQHelper();
    return _qHelper.min();
  }

  @Override
  public Measurable<T> max()
  {
    initQHelper();
    return _qHelper.max();
  }

  @Override
  public Measurable<T> mean()
  {
    initQHelper();
    return _qHelper.mean();
  }

  @Override
  public Measurable<T> variance()
  {
    initQHelper();
    return _qHelper.variance();
  }

  @Override
  public Measurable<T> sd()
  {
    initQHelper();
    return _qHelper.sd();
  }

  @Override
  public boolean isQuantity()
  {
    return true;
  }

  @Override
  public boolean isTemporal()
  {
    return true;
  }

  protected void initQHelper()
  {
    if (_qHelper == null)
    {
      _qHelper =
          new QuantityHelper<T>((ArrayList<Measurable<T>>) getValues(), units);
    }
  }

  @Override
  public Dimension getDimension()
  {
    initQHelper();
    return _qHelper.getDimension();
  }

  @UIProperty(name = "Units", category = UIProperty.CATEGORY_VALUE)
  @Override
  public Unit<T> getUnits()
  {
    initQHelper();
    return _qHelper.getUnits();
  }

  @Override
  public void replaceSingleton(Number newValue)
  {
    initQHelper();
    _qHelper.replace(newValue);
  }

  @Override
  public void setRange(QuantityRange<T> range)
  {
    initQHelper();
    _range = range;
    _qHelper.setRange(range);

    // tell anyone that wants to know
    super.fireMetadataChanged();

  }

  @UIProperty(name = "Range", category = UIProperty.CATEGORY_METADATA,
      visibleWhen = "valuesCount == 1")
  @Override
  public QuantityRange<T> getRange()
  {
    return _range;
  }

  @Override
  public Measurable<T> interpolateValue(long time, InterpMethod interpMethod)
  {
    final Measurable<T> res;
    switch (interpMethod)
    {
    case Linear:
      res = linearInterp(this.getTimes(), this.getValues(), time);
      break;
    default:
      res = null;
    }
    return res;
  }

  private Measurable<T> linearInterp(Collection<Long> times,
      List<Measurable<T>> values, long time)
  {
    final Measurable<T> res;

    // ok, find the values either side
    Measurable<T> beforeVal, afterVal;
    int beforeIndex = -1, afterIndex = -1;
    long beforeTime = 0, afterTime = 0;

    Iterator<Long> tIter = times.iterator();
    int ctr = 0;
    while (tIter.hasNext())
    {
      Long thisT = (Long) tIter.next();
      if (thisT <= time)
      {
        beforeIndex = ctr;
        beforeTime = thisT;
      }
      if (thisT >= time)
      {
        afterIndex = ctr;
        afterTime = thisT;
        break;
      }

      ctr++;
    }

    if (beforeIndex >= 0 && afterIndex == 0)
    {
      res = values.get(beforeIndex);
    }
    else if (beforeIndex >= 0 && afterIndex >= 0)
    {
      if (beforeIndex == afterIndex)
      {
        // special case - it falls on one of our values
        res = values.get(beforeIndex);
      }
      else
      {

        beforeVal = values.get(beforeIndex);
        afterVal = values.get(afterIndex);

        double y0 = beforeVal.doubleValue(getUnits());
        double y1 = afterVal.doubleValue(getUnits());
        double x0 = beforeTime;
        double x1 = afterTime;
        double x = time;

        double newRes = y0 + (y1 - y0) * (x - x0) / (x1 - x0);

        // ok, we can do the calc
        res = Measure.valueOf(newRes, getUnits());
      }
    }
    else
    {
      res = null;
    }

    return res;
  }

  @UIProperty(name = "Value", category = UIProperty.CATEGORY_VALUE,
      visibleWhen = "valuesCount == 1")
  public Number getSingletonValue()
  {
    initQHelper();
    return _qHelper.getValue();
  }

  public void setSingletonValue(Number newValue)
  {
    replaceSingleton(newValue);
    fireDataChanged();
  }

}
