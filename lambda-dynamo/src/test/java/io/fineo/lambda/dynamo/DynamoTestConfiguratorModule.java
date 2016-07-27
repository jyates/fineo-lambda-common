package io.fineo.lambda.dynamo;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import io.fineo.lambda.configure.dynamo.AwsDynamoConfigurator;

public class DynamoTestConfiguratorModule extends AbstractModule {
  public static final String DYNAMO_URL_FOR_TESTING = "fineo.dynamo.testing.url";

  @Override
  protected void configure() {
  }

  @Provides
  @Inject
  public AwsDynamoConfigurator getTestConfigurator(
    @Named(DYNAMO_URL_FOR_TESTING) String url) {
    return client -> client.setEndpoint(url);
  }
}
