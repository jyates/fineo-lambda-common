package io.fineo.lambda.handle;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class TestLambdaBaseWrapper {

  /**
   * Necessary for local e2e tests that need to shut down the dynamo connectors and we need to
   * get the same guice Injector so we get the same singleton instance.
   * @throws Exception on failure
   */
  @Test
  public void testGetInjectorIsSameForModule() throws Exception {
    LambdaForTest lambda = new LambdaForTest(new ObjectModuleLoader());
    Object o = lambda.getInstance().getObject();
    assertTrue(o == lambda.getGuiceForTesting().getInstance(Object.class));
  }

  private static class ObjectModuleLoader extends AbstractModule{

    @Override
    protected void configure() {
      bind(Object.class).toInstance(new Object());
    }
  }

  public static class LambdaClassForTest{
    private final Object object;

    @Inject
    public LambdaClassForTest(Object object) {
      this.object = object;
    }

    public Object getObject() {
      return object;
    }
  }

  public static class LambdaForTest extends LambdaBaseWrapper<LambdaClassForTest>{

    public LambdaForTest(Module module) {
      super(LambdaClassForTest.class, Arrays.asList(module));
    }
  }
}
