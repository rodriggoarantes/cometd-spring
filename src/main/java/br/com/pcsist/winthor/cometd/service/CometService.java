package br.com.pcsist.winthor.cometd.service;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.cometd.annotation.Configure;
import org.cometd.annotation.Listener;
import org.cometd.annotation.RemoteCall;
import org.cometd.annotation.Session;
import org.cometd.annotation.Subscription;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.LocalSession;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.authorizer.GrantAuthorizer;

@org.cometd.annotation.Service("cometService")
public class CometService {

  @Inject
  private BayeuxServer bayeuxServer;
  @Session
  private ServerSession serverSession;
  @Session
  private LocalSession localSession;

  @Configure("/echo")
  protected void configureMensagem(ConfigurableServerChannel channel) {
    System.out.println("@Configure : doEcho");
    channel.addAuthorizer(GrantAuthorizer.GRANT_ALL);
  }

  @RemoteCall("echo")
  public void doEcho(RemoteCall.Caller caller, Map<String, Object> data) {
    System.out.println("@RemoteCall : doEcho");
    
    final Map<String, Object> r = new HashMap<String, Object>(0);
    r.put("msg", "Mensagem: " + data.get("msg"));
    
    // send mensagem for channel
    publishMessage(r);
    
    // return result
    caller.result(r);
  }

  @Listener("/echo")
  public void echo(ServerSession remote, ServerMessage.Mutable message) {
    System.out.println("@Listener: Listener Echo");
    
    String channel = message.getChannel();
    Object data = message.getData();
    remote.deliver(serverSession, channel, data);
  }

  @Subscription("/echo")
  public void echo(Message message) {
    System.out.println("@Subscription: Echo service published " + message);
  }

  private void publishMessage(Object msg) {
    final ServerChannel channel = bayeuxServer.getChannel("/echo");
    channel.publish(serverSession, msg);
  }
}
