package io.fineo.lambda.configure.dynamo;

import com.amazonaws.regions.RegionUtils;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import java.io.Serializable;

/**
 * A module that loads a {@link AwsDynamoConfigurator} to io.fineo.lambda.configure.configure with a given region
 */
public class DynamoRegionConfigurator extends AbstractModule implements Serializable {

  public static final String DYNAMO_REGION = "fineo.dynamo.region";

  @Override
  protected void configure() {
  }

  @Provides
  @Inject
  public AwsDynamoConfigurator getRegionConfigurator(
    @Named(DYNAMO_REGION) String region) {
    return client -> client.setRegion(RegionUtils.getRegion(region));
  }
}
