package com.hive;

import java.util.Hashtable;
import javax.security.sasl.AuthenticationException;
import org.apache.hive.service.auth.PasswdAuthenticationProvider;

/*
 javac -cp $HIVE_HOME/lib/hive-service-0.12.0-cdh5.0.0-beta-2.jar SampleAuthenticator.java -d .
 jar cf sampleauth.jar hive
 cp sampleauth.jar $HIVE_HOME/lib/.
*/


public class SampleAuthenticator implements PasswdAuthenticationProvider {

  Hashtable<String, String> store = null;

  public SampleAuthenticator () {
    store = new Hashtable<String, String>();
    store.put("root", "hx2carroot");
    store.put("hive", "hx2carhive");
  }

  @Override
  public void Authenticate(String user, String  password)
      throws AuthenticationException {

    String storedPasswd = store.get(user);

    if (storedPasswd != null && storedPasswd.equals(password))
      return;

    throw new AuthenticationException("SampleAuthenticator: Error validating user");
  }

}