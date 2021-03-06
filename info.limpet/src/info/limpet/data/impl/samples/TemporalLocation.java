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
package info.limpet.data.impl.samples;

import info.limpet.ITemporalQuantityCollection.InterpMethod;
import info.limpet.data.impl.TemporalObjectCollection;
import info.limpet.data.operations.spatial.GeoSupport;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class TemporalLocation extends TemporalObjectCollection<Point2D>
    implements StockTypes.ILocations
{
  public TemporalLocation(String name)
  {
    super(name);
  }

  @Override
  public List<Point2D> getLocations()
  {
    return super.getValues();
  }

  public TemporalLocation()
  {
    this(null);
  }

  private Point2D linearInterp(Collection<Long> times, List<Point2D> values,
      long time)
  {
    final Point2D res;

    // ok, find the values either side
    Point2D beforeVal, afterVal;
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

        double latY0 = beforeVal.getY();
        double latY1 = afterVal.getY();

        double longY0 = beforeVal.getX();
        double longY1 = afterVal.getX();

        double x0 = beforeTime;
        double x1 = afterTime;
        double x = time;

        double newResLat = latY0 + (latY1 - latY0) * (x - x0) / (x1 - x0);
        double newResLong = longY0 + (longY1 - longY0) * (x - x0) / (x1 - x0);

        // ok, we can do the calc
        res = GeoSupport.getCalculator().createPoint(newResLong, newResLat);
      }
    }
    else
    {
      res = null;
    }

    return res;
  }

  public Point2D interpolateValue(long time, InterpMethod interpMethod)
  {
    final Point2D res;
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

}
