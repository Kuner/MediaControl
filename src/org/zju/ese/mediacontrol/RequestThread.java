package org.zju.ese.mediacontrol;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class RequestThread extends Thread {
	private String ServerIP = "192.168.1.115";
	private int ServerPort = 10000;
	Object object;
	
	public RequestThread(String address,int port, Object obj)
	{
		ServerIP = address;
		ServerPort = port;
		object = obj;
	}
	@Override
	public void run() {
		SocketConnector connector;
		connector = new NioSocketConnector();
        connector.getSessionConfig().setUseReadOperation(true);
        connector.getFilterChain().addLast("codec", 
                new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        ConnectFuture connectFuture = connector.connect(
                new InetSocketAddress(ServerIP, ServerPort));
        
        if(connectFuture.awaitUninterruptibly(3000) 
                && connectFuture.isConnected()) {
        	IoSession session;
        	session = connectFuture.getSession();
        	
        	session.write(object);
        }
	}
}
