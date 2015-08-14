package xyz.jacklify.server;

import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.net.ssl.SSLException;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import xyz.jacklify.netutils.DisconnectCodes;
import xyz.jacklify.netutils.EasyProperties;
import xyz.jacklify.netutils.PacketFactory;
import xyz.jacklify.server.debug.StubbedCallbackManager;
import xyz.jacklify.server.internal.Packet0Login;
import xyz.jacklify.server.internal.Packet1Kick;
import xyz.jacklify.server.netio.Client;
import xyz.jacklify.server.netio.Connection;
import xyz.jacklify.server.netio.ConnectionChannelManager;
import xyz.jacklify.server.netio.PeerBase;
import xyz.jacklify.server.netio.ServerPeer;
import xyz.jacklify.server.tasks.UnauthenticatedFloodHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InternalServerManager {

	private EasyProperties config = null;
	private NetworkCallbackManager callbacks = null;
	private ArrayList<String> acceptedProtocols = new ArrayList<String>();
	private String gameProtocol = null;
	private Logger log = LogManager.getLogger("Game-Server");
	
	// Netty-related components
	private EventLoopGroup bGroup = null;
	private EventLoopGroup wGroup = null;
	private ServerBootstrap serverBootstap = null;
	
	// Backend communication support (optional)
	private boolean allowEndtoEnd = false;
	private ArrayList<String> acceptedServerIPs = new ArrayList<String>();
	private String endToEndPass = null;
	
	private ArrayList<Client> connectedClients = new ArrayList<Client>();
	private ArrayList<ServerPeer> connectedServers = new ArrayList<ServerPeer>();
	
	private CopyOnWriteArrayList<Connection> unauthenticatedPeers = new CopyOnWriteArrayList<Connection>();
	private Timer taskManager = new Timer();
	
	public InternalServerManager(EasyProperties props, NetworkCallbackManager impl) {
		initLogging();
		if (props == null) {
			props = new EasyProperties();
			log.error("Core: Running with default configuration settings! These are not suitable for production environments and may be insecure!");
		}
		
		this.config = props;
		
		if (impl == null) {
			impl = new StubbedCallbackManager();
			log.error("Core: Running with StubbedCallbackManager. Please specify your own NetworkCallbackManager implementation.");
		}
		
		this.callbacks = impl;
		this.gameProtocol = props.getProperty("impl.gameversion", "dev-unspecified");
		this.allowEndtoEnd = props.getBoolean("net.endtoend", false);
		
		if (this.allowEndtoEnd) {
			this.endToEndPass = props.getProperty("net.passphrase", null);
			if (this.endToEndPass == null || this.endToEndPass.isEmpty()) {
				this.log.error("Core: ** NO END-TO-END PASSPHRASE SPECIFIED **");
				this.log.error("Core: ** WITHOUT AN END TO END PASSPHRASE, ANY REMOTE SERVER COULD CONNECT TO YOUR SERVICE AND LEAK DATA. **");
				this.log.error("Core: ** THIS IS A MAJOR SECURITY RISK. END TO END COMMUNICATION WILL BE DISABLED **");
				this.log.error("Core: ** Developers: Please specify net.passphrase in your code to enabled end-to-end communication **");
				this.allowEndtoEnd = false;
			}
		}
		// Register internal protocol packets
		PacketFactory.registerPacket(0, Packet0Login.class);
		PacketFactory.registerPacket(1, Packet1Kick.class);
		taskManager.scheduleAtFixedRate(new UnauthenticatedFloodHandler(this, 10000), 10000, 10000);
		this.acceptedProtocols.add("libjackdare-alpha-test");
	}
	
	public void startServer() {
		initNetwork();
	}
	
	public Client[] getConnectedClients() {
		synchronized (this.connectedClients) {
			Client[] clients = new Client[this.connectedClients.size()];
			return this.connectedClients.toArray(clients);
		}
	}
	
	public ServerPeer[] getConnectedServers() {
		synchronized (this.connectedServers) {
			ServerPeer[] server = new ServerPeer[this.connectedServers.size()];
			return this.connectedServers.toArray(server);
		}
	}
	
	public boolean handleConnect(PeerBase base, Packet0Login login) {
		if (!this.acceptedProtocols.contains(login.getProtocolVersion())) {
			login.setResponseCode(DisconnectCodes.PROTOCOL_VERSION_MISMATCH.getId()); 
			return false;
		}
		
		if (!this.gameProtocol.equals(login.getProtocolVersion())) {
			login.setResponseCode(DisconnectCodes.IMPLEMENTATION_VERSION_MISMATCH.getId());
			return false;
		}
		
		if (base instanceof ServerPeer) {
			// Check if server peer is allowed.
			if (this.allowEndtoEnd && this.acceptedServerIPs.contains(base.getIP()) && this.endToEndPass.equals(login.getPassphrase())) {
				this.log.info("Core: (handleConnect): Accepting end-to-end connection from: " + base.getIP() + ":" + base.getPort());
				
				if (this.callbacks.onPeerConnect((ServerPeer)base)) {
					synchronized (this.connectedServers) {
						this.connectedServers.add((ServerPeer)base);
					}
					return true;
				} else {
					return false;
				}
			} else {
				login.setResponseCode(DisconnectCodes.UNAUTHORIZED_ACTION.getId());
				this.log.warn("Core: (handleConnect): Unauthroized server peer connection from: " + base.getIP() + ":" + base.getPort());
				this.log.warn("Core: (handleConnect): Disconnecting rogue server..");
				return false;
			}
		} else {
			// Client
			return this.callbacks.onClientConnect((Client)base, login);
		}
	}
	
	private void initNetwork() {
		this.bGroup = createEventGroup();
		this.wGroup = createEventGroup();
		
		Class<? extends ServerChannel> clazz = null;
		
		if (bGroup instanceof EpollEventLoopGroup) {
			clazz = EpollServerSocketChannel.class;
		} else {
			clazz = NioServerSocketChannel.class;
		}
		
		ChannelHandler handler = null;
		
		if (config.getBoolean("net.usessl", false)) {
			this.log.error("Core: Using SSL");
			// We need to load the SSL certificates
			try {
				SslContext ssl = SslContext.newServerContext(null, null);
				handler = new ConnectionChannelManager(ssl, this);
			} catch (SSLException e) {
				this.log.error("Core: Failed to create SslContext, aborting.", e);
				return;
			}
		} else {
			// No SSL, warn developer
			this.log.warn("Core: net.usessl is DISABLED! Connections will be marked insecure.");
			handler = new ConnectionChannelManager(this);
		}
		
		int port = config.getInteger("net.port", 3124);
		log.info("Core: Starting Netty server on port: " + port);
		this.serverBootstap = new ServerBootstrap();
		this.serverBootstap
		.group(this.bGroup, this.wGroup)
		.channel(clazz)
		.childOption(ChannelOption.TCP_NODELAY, config.getBoolean("net.tcpdelay", true))
		.childHandler(handler)
		.bind(port);
		log.info("Core: Started Netty server sucessfully! Awaiting connections...");
		this.callbacks.onServerStart();
	}

	private EventLoopGroup createEventGroup() {
		// Check if using native transport
		EventLoopGroup group = null;
		if (config.getBoolean("net.use-netty-epoll", false)) {
			if (System.getProperties().getProperty("os.name").toLowerCase().contains("linux")) {
				log.info("Core: Using EPoll (native transport) for socket connections");
				group = new EpollEventLoopGroup(config.getInteger("net.threads-per-core", 4));
			} else {
				log.error("Core: Couldn't use Epoll! You must be running a Linux-based OS!");
				log.info("Core: Using Nio for socket connections");
				group = new NioEventLoopGroup(config.getInteger("net.threads-per-core", 4));
			}
		} else {
			log.info("Core: Using Nio for socket connections");
			group = new NioEventLoopGroup(config.getInteger("net.threads-per-core", 4));
		}
		return group;
	}
	private void initLogging() {
	}

	public CopyOnWriteArrayList<Connection> getUnauthenticatedPeers() {
		return this.unauthenticatedPeers;
	}

	public void addUnauthenticatedPeer(Connection connection) {
		this.unauthenticatedPeers.add(connection);
	}

	public void removeUnauthenticatedPeer(Connection connection) {
		this.unauthenticatedPeers.remove(connection);
	}

	public void setGameProtocol(String string) {
		this.gameProtocol = string;
	}
}
