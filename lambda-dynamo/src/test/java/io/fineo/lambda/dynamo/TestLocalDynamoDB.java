package io.fineo.lambda.dynamo;

import io.fineo.lambda.dynamo.rule.BaseDynamoTableTest;
import org.junit.Test;

/**
 * Simple test to run dynamodb locally.
 * Important things you need to ensure you can use dynamodb local:
 * <ol>
 * <li>-Dsqlite4java.library.path=${project.build.directory}/dependencies in you surefire
 * argline settings</li>
 * <li>maven-dependency-plugin to copy all the test dependencies (needed for sql lib path</li>
 * <li>dynamo s3 release repository</li>
 * </ol>
 * Otherwise, just use the same dependencies we have here.
 */
public class TestLocalDynamoDB extends BaseDynamoTableTest {

  @Test
  public void testAccess() throws Exception {
    BaseDynamoTableTest.dynamo.getClient().listTables();
    this.tables.getAsyncClient().listTables();
  }
}
