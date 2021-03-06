package info.limpet.data.operations.arithmetic;

import info.limpet.ICollection;

import java.util.List;

import javax.measure.unit.SI;

public abstract class UnitaryAngleOperation extends UnitaryMathOperation
{
  public UnitaryAngleOperation(String opName)
  {
    super(opName);
  }

  @Override
  protected boolean appliesTo(List<ICollection> selection)
  {
    return super.appliesTo(selection)
        && getATests().allHaveDimension(selection, SI.RADIAN.getDimension());
  }
}
