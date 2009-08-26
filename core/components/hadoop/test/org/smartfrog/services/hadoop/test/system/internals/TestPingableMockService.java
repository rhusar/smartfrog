/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/
package org.smartfrog.services.hadoop.test.system.internals;

import junit.framework.TestCase;
import org.apache.hadoop.util.MockService;
import org.apache.hadoop.util.Service;
import org.apache.hadoop.PingableMockService;
import org.smartfrog.services.hadoop.core.ServicePingStatus;

import java.io.IOException;
import java.util.List;

/**
 *
 * Created 26-Aug-2009 17:16:24
 *
 */

public final class TestPingableMockService extends TestCase {
  private PingableMockService service;

  /**
   * Constructs a test case with the given name.
   */
  public TestPingableMockService(String name) {
    super(name);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    service = new PingableMockService();
  }


  @Override
  protected void tearDown() throws Exception {
    Service.close(service);
    super.tearDown();
  }

  private void start() throws IOException {
    service.start();
  }

  private void close() throws IOException {
    service.close();
    assertInTerminatedState();
  }


  private ServicePingStatus ping() throws IOException {
    return service.ping();
  }
  
  private void assertInState(Service.ServiceState state)
          throws Service.ServiceStateException {
    service.verifyServiceState(state);
  }

  private void assertInLiveState() throws Service.ServiceStateException {
    assertInState(Service.ServiceState.LIVE);
  }

  private void assertInCreatedState() throws Service.ServiceStateException {
    assertInState(Service.ServiceState.CREATED);
  }

  private void assertInFailedState() throws Service.ServiceStateException {
    assertInState(Service.ServiceState.FAILED);
  }

  private void assertInTerminatedState() throws Service.ServiceStateException {
    assertInState(Service.ServiceState.CLOSED);
  }

  private void assertRunning() {
    assertTrue("Service is not running: " + service, service.isRunning());
  }

  private void assertNotRunning() {
    assertFalse("Service is running: " + service, service.isRunning());
  }

  private void enterState(Service.ServiceState state)
          throws Service.ServiceStateException {
    service.changeState(state);
    assertInState(state);
  }


  private void enterFailedState() throws Service.ServiceStateException {
    enterState(Service.ServiceState.FAILED);
  }

  private void enterTerminatedState() throws Service.ServiceStateException {
    enterState(Service.ServiceState.CLOSED);
  }

  private void assertStateChangeCount(int expected) {
    assertEquals("Wrong state change count for " + service,
            expected,
            service.getStateChangeCount());
  }

  private void assertPingCount(int expected) {
    assertEquals("Wrong pingchange count for " + service,
            expected,
            service.getPingCount());
  }


  private void failShouldNotGetHere() {
    fail("expected failure, but service is in " + service.getServiceState());
  }

  /**
   * Test that the ping operation returns a mock exception
   * @return the service status
   * @throws IOException IO problems
   */
  private ServicePingStatus assertPingContainsMockException()
          throws IOException {
    ServicePingStatus serviceStatus = service.ping();
    List<Throwable> thrown = serviceStatus.getThrowables();
    assertFalse("No nested exceptions in service status", thrown.isEmpty());
    Throwable throwable = thrown.get(0);
    assertTrue(
            "Nested exception is not a MockServiceException : " + throwable,
            throwable instanceof MockService.MockServiceException);
    return serviceStatus;
  }

  /**
   * Walk through the lifecycle and check it changes visible state
   */
  public void testBasicLifecycle() throws Throwable {
    assertInCreatedState();
    assertNotRunning();
    assertNotRunning();
    start();
    assertInLiveState();
    assertRunning();
    ping();
    ping();
    assertPingCount(2);
    close();
    assertStateChangeCount(3);
    assertNotRunning();
  }


  public void testPingInFailedReturnsException() throws Throwable {
    service.setFailOnStart(true);
    try {
      start();
      failShouldNotGetHere();
    } catch (MockService.MockServiceException e) {
      assertInFailedState();
      //and test that the ping works out
      ServicePingStatus serviceStatus = assertPingContainsMockException();
      assertEquals(Service.ServiceState.FAILED, serviceStatus.getState());
    }
  }


  public void testFailInPing() throws Throwable {
    service.setFailOnPing(true);
    start();
    ServicePingStatus serviceStatus = service.ping();
    assertEquals(Service.ServiceState.FAILED, serviceStatus.getState());
    assertPingCount(1);
    List<Throwable> thrown = serviceStatus.getThrowables();
    assertEquals(1, thrown.size());
    Throwable throwable = thrown.get(0);
    assertTrue(throwable instanceof MockService.MockServiceException);
  }

  public void testPingInCreated() throws Throwable {
    service.setFailOnPing(true);
    ping();
    assertPingCount(0);
  }


  /**
   * Test that when in a failed state, you can't ping the service
   *
   * @throws Throwable if needed
   */
  public void testPingInFailedStateIsNoop() throws Throwable {
    enterFailedState();
    assertInFailedState();
    ServicePingStatus serviceStatus = service.ping();
    assertEquals(Service.ServiceState.FAILED, serviceStatus.getState());
    assertPingCount(0);
  }

  /**
   * Test that when in a terminated state, you can't ping the service
   *
   * @throws Throwable if needed
   */
  public void testPingInTerminatedStateIsNoop() throws Throwable {
    enterTerminatedState();
    assertInTerminatedState();
    ServicePingStatus serviceStatus = service.ping();
    assertEquals(Service.ServiceState.CLOSED, serviceStatus.getState());
    assertPingCount(0);
  }

}
