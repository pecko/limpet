package info.limpet.rcp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

@Suite.SuiteClasses(
{ 
	TestReflectivePropertySource.class,
})

@RunWith(Suite.class)
public class AllRcpTests extends TestSuite
{

}
