package io.fineo.lambda.handle;

import com.amazonaws.log.InternalLogFactory;
import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.fineo.lambda.configure.PropertiesModule;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestLambdaBaseWrapper {

  /**
   * Necessary for local e2e tests that need to shut down the dynamo connectors and we need to
   * get the same guice Injector so we get the same singleton instance.
   *
   * @throws Exception on failure
   */
  @Test
  public void testGetInjectorIsSameForModule() throws Exception {
    LambdaForTest lambda = new LambdaForTest(new ObjectModuleLoader());
    Object o = lambda.getInstance().getObject();
    assertTrue(o == lambda.getGuiceForTesting().getInstance(Object.class));
  }

  /**
   * @throws Exception
   */
  @Test
  public void testSlf4jFactoryForLambda() throws Exception {
    new LambdaForTest(new ObjectModuleLoader());
    assertEquals(Slf4jLogFactory.class, InternalLogFactory.getFactory().getClass());
  }

  @Test
  public void testDoNotFailLoggingEnableIfNullContext() throws Exception {
    LambdaBaseWrapper.log(null);
  }

  @Test
  public void testLoggingSetupPropertiesOnMissingProperty() throws Exception {
    Properties props = new Properties();
    props.put("testkey", "testvalue");
    PropertiesModule pm = new PropertiesModule(props);
    Module withNamed = new AbstractModule() {
      @Override
      protected void configure() {
      }

      @Inject
      @Provides
      @Singleton
      public SomeClass create(@Named("some.key") String key) {
        throw new RuntimeException("Should not be able to create the class because missing key!");
      }
    };

    LambdaForTest lambda = new LambdaForTest(pm, withNamed);
    try {
      LambdaBaseWrapper.LOG = Mockito.mock(Logger.class);
      lambda.getInstance();
      fail("Should have failed to create the instance!");
    } catch (CreationException e) {
      Mockito.verify(LambdaForTest.LOG).info("Initialized with properties: \n{}", props);
    } finally {
      // reset the LOG
      LambdaBaseWrapper.LOG = LoggerFactory.getLogger(LambdaBaseWrapper.class);
    }
  }

  public static class SomeClass {
  }

  private static class ObjectModuleLoader extends AbstractModule {

    @Override
    protected void configure() {
      bind(Object.class).toInstance(new Object());
    }
  }

  public static class LambdaClassForTest {
    private final Object object;

    @Inject
    public LambdaClassForTest(Object object) {
      this.object = object;
    }

    public Object getObject() {
      return object;
    }
  }

  public static class LambdaForTest extends LambdaBaseWrapper<LambdaClassForTest> {

    public LambdaForTest(Module... module) {
      super(LambdaClassForTest.class, Arrays.asList(module));
    }
  }
}
