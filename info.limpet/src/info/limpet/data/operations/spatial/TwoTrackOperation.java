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
package info.limpet.data.operations.spatial;

import info.limpet.IBaseTemporalCollection;
import info.limpet.ICollection;
import info.limpet.IContext;
import info.limpet.IObjectCollection;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.ITemporalQuantityCollection.InterpMethod;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.samples.StockTypes.ILocations;
import info.limpet.data.impl.samples.StockTypes.NonTemporal;
import info.limpet.data.impl.samples.StockTypes.NonTemporal.Location;
import info.limpet.data.impl.samples.TemporalLocation;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.data.operations.CollectionComplianceTests.TimePeriod;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class TwoTrackOperation implements IOperation<IStoreItem>
{

  public abstract static class DistanceOperation extends
      AbstractCommand<IStoreItem>
  {
    private final IBaseTemporalCollection _timeProvider;
    private final CollectionComplianceTests aTests = new CollectionComplianceTests();

    public DistanceOperation(List<IStoreItem> selection, IStore store,
        String title, String description, IBaseTemporalCollection timeProvider,
        IContext context)
    {
      super(title, description, store, false, false, selection, context);
      _timeProvider = timeProvider;
    }

    @Override
    public void execute()
    {
      // get the unit
      List<IStoreItem> outputs = new ArrayList<IStoreItem>();

      // put the names into a string
      String title = getOutputName();

      // ok, generate the new series
      IQuantityCollection<?> target = getOutputCollection(title,
          _timeProvider != null);

      outputs.add(target);

      // store the output
      super.addOutput(target);

      // start adding values.
      performCalc();

      // tell each series that we're a dependent
      Iterator<IStoreItem> iter = getInputs().iterator();
      while (iter.hasNext())
      {
        ICollection iCollection = (ICollection) iter.next();
        iCollection.addDependent(this);
      }

      // ok, done
      List<IStoreItem> res = new ArrayList<IStoreItem>();
      res.add(target);
      getStore().addAll(res);
    }

    protected abstract IQuantityCollection<?> getOutputCollection(
        String trackList, boolean isTemporal);

    @Override
    protected void recalculate(IStoreItem subject)
    {
      // clear out the lists, first
      Iterator<IStoreItem> iter = getOutputs().iterator();
      while (iter.hasNext())
      {
        IQuantityCollection<?> qC = (IQuantityCollection<?>) iter.next();
        qC.getValues().clear();
      }

      // update the results
      performCalc();
    }

    /**
     * wrap the actual operation. We're doing this since we need to separate it
     * from the core "execute" operation in order to support dynamic updates
     * 
     * @param unit
     */
    @SuppressWarnings("unchecked")
    private void performCalc()
    {
      ICollection track1 = (ICollection) getInputs().get(0);
      ICollection track2 = (ICollection) getInputs().get(1);

      // get a calculator to use
      final IGeoCalculator calc = GeoSupport.getCalculator();

      if (_timeProvider != null)
      {

        // and the bounding period
        Collection<ICollection> selection = new ArrayList<ICollection>();
        selection.add(track1);
        selection.add(track2);

        TimePeriod period = aTests.getBoundingTime(selection);

        // check it's valid
        if (period.invalid())
        {
          System.err.println("Insufficient coverage for datasets");
          return;
        }

        // ok, let's start by finding our time sync
        IBaseTemporalCollection times = aTests.getOptimalTimes(period,
            selection);

        // check we were able to find some times
        if (times == null)
        {
          System.err.println("Unable to find time source dataset");
          return;
        }

        // and now we can start looping through
        Iterator<Long> tIter = times.getTimes().iterator();
        while (tIter.hasNext())
        {
          long thisTime = (long) tIter.next();

          if (thisTime >= period.getStartTime()
              && thisTime <= period.getEndTime())
          {

            Point2D locA = locationFor(track1, thisTime);
            Point2D locB = locationFor(track2, thisTime);

            if (locA != null && locB != null)
            {
              calcAndStore(calc, locA, locB, thisTime);
            }
            else
            {
              System.err.println("Not calculating location at time:" + thisTime
                  + " - insufficient coverage");
            }
          }
        }
      }
      else
      {
        // ok, we're doing an indexed version
        // find one wiht more than one item
        final IObjectCollection<Point2D> primary;
        final IObjectCollection<Point2D> secondary;
        if (track1.getValuesCount() > 1)
        {
          primary = (IObjectCollection<Point2D>) track1;
          secondary = (IObjectCollection<Point2D>) track2;
        }
        else
        {
          primary = (IObjectCollection<Point2D>) track2;
          secondary = (IObjectCollection<Point2D>) track1;
        }

        for (int j = 0; j < primary.getValuesCount(); j++)
        {
          final Point2D locA, locB;

          locA = primary.getValues().get(j);

          if (secondary.getValuesCount() > 1)
          {
            locB = secondary.getValues().get(j);
          }
          else
          {
            locB = secondary.getValues().get(0);
          }

          calcAndStore(calc, locA, locB, null);
        }
      }

      // ok, share the updates
      Iterator<IStoreItem> oIter = getOutputs().iterator();
      while (oIter.hasNext())
      {
        IStoreItem iStoreItem = (IStoreItem) oIter.next();
        iStoreItem.fireDataChanged();
      }
    }

    private Point2D locationFor(ICollection track, final Long thisTime)
    {
      final Point2D locOne;
      if (track instanceof IBaseTemporalCollection)
      {
        TemporalLocation tl = (TemporalLocation) track;
        locOne = tl.interpolateValue(thisTime, InterpMethod.Linear);
      }
      else
      {
        NonTemporal.Location tl = (Location) track;
        locOne = tl.getValues().iterator().next();
      }
      return locOne;
    }

    protected abstract void calcAndStore(final IGeoCalculator calc,
        final Point2D locA, final Point2D locB, Long time);
  }

  private final CollectionComplianceTests aTests = new CollectionComplianceTests();

  protected boolean appliesTo(List<IStoreItem> selection)
  {
    boolean nonEmpty = getATests().nonEmpty(selection);
    boolean equalLength = getATests().allEqualLengthOrSingleton(selection);
    boolean canInterpolate = getATests()
        .suitableForTimeInterpolation(selection);
    boolean onlyTwo = getATests().exactNumber(selection, 2);
    boolean hasContents = getATests().allHaveData(selection);
    boolean equalOrInterp = equalLength || canInterpolate;
    boolean allLocation = getATests().allLocation(selection);

    return nonEmpty && equalOrInterp && onlyTwo && allLocation && hasContents;
  }

  public CollectionComplianceTests getATests()
  {
    return aTests;
  }

  /**
   * utility operation to extract the location datasets from the selection
   * (walking down into groups as necessary)
   * 
   * @param selection
   * @return
   */
  protected List<IStoreItem> getLocationDatasets(List<IStoreItem> selection)
  {
    List<IStoreItem> collatedTracks = new ArrayList<IStoreItem>();

    // hmm, they may be composite tracks - extract the location data
    Iterator<IStoreItem> sIter = selection.iterator();
    while (sIter.hasNext())
    {
      IStoreItem iStoreItem = (IStoreItem) sIter.next();
      if (iStoreItem instanceof IStoreGroup)
      {
        IStoreGroup group = (IStoreGroup) iStoreItem;
        Iterator<IStoreItem> kids = group.iterator();
        while (kids.hasNext())
        {
          IStoreItem thisItem = (IStoreItem) kids.next();
          if (thisItem instanceof ILocations)
          {
            IStoreItem thisI = (IStoreItem) thisItem;
            collatedTracks.add(thisI);
          }
        }
      }
      else if (iStoreItem instanceof ILocations)
      {
        collatedTracks.add(iStoreItem);
      }
    }
    return collatedTracks;
  }

}
