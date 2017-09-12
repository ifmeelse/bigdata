package com.flume;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.api.RpcClient;
import java.nio.charset.Charset;
import java.util.Properties;

public class flumeloadbalance {
  public static void main(String[] args) {
    MySecureRpcClientFacade client = new MySecureRpcClientFacade();
    // Initialize client with the remote Flume agent's host, port
    Properties props = new Properties();
    props.put("client.type", "default_loadbalance");

    // List of hosts (space-separated list of user-chosen host aliases)
    props.put("hosts", "h1 h2 h3");

    // host/port pair for each host alias
    String host1 = "10.90.60.206:8888";
    String host2 = "10.90.60.205:8888";
    String host3 = "10.90.60.204:8888";
    props.put("hosts.h1", host1);
    props.put("hosts.h2", host2);
    props.put("hosts.h3", host3);

//    props.put("host-selector", "random"); // For random host selection
     props.put("host-selector", "round_robin"); // For round-robin host
//                                                // selection
    props.put("backoff", "true"); // Disabled by default.

//    props.put("maxBackoff", "10000"); // Defaults 0, which effectively
                                      // becomes 30000 ms
    props.put("request-timeout", "2000");//Must be >=1000 (default: 20000)
    props.put("request-timeout", "2000");//Must be >=1000 (default: 20000)
    props.put("batch-size", "100");// Must be >=1 (default: 100)
    client.init(props);
    long time=System.currentTimeMillis();
    // Send 10 events to the remote Flume agent. That agent should be
    // configured to listen with an AvroSource.
    String sampleData = "Hello Flume!";
    for (int i = 0; i < 3000; i++) {
      client.sendDataToFlume(i+"="+sampleData);
      System.out.println(i);
    }
    System.out.println(System.currentTimeMillis()-time);
    client.cleanUp();
  }
}

class MySecureRpcClientFacade {
  private RpcClient client;
  private Properties properties;

  public void init(Properties properties) {
    // Setup the RPC connection
    this.properties = properties;
    // Create the ThriftSecureRpcClient instance by using SecureRpcClientFactory
//    this.client = SecureRpcClientFactory.getThriftInstance(properties);
    this.client = RpcClientFactory.getInstance(properties);
  }

  public void sendDataToFlume(String data) {
    // Create a Flume Event object that encapsulates the sample data
    Event event = EventBuilder.withBody(data, Charset.forName("UTF-8"));

    // Send the event
    try {
      client.append(event);
    } catch (EventDeliveryException e) {
      // clean up and recreate the client
      client.close();
      client = null;
//      client = SecureRpcClientFactory.getThriftInstance(properties);
      client = RpcClientFactory.getInstance(properties);
    }
  }

  public void cleanUp() {
    // Close the RPC connection
    client.close();
  }
}