package io.fineo.lambda.configure;

import com.google.inject.AbstractModule;

import java.time.Clock;

/**
 * Bind a {@link Clock} to a UTC clock using the system time
 */
public class UtcClockModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(Clock.class).toInstance(Clock.systemUTC());
  }
}
