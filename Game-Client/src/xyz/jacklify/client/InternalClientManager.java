package xyz.jacklify.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import xyz.jacklify.client.netio.ConnectionManager;
import xyz.jacklify.client.netio.NetChannelHandler;
import xyz.jacklify.client.netio.Packet0Login;
import xyz.jacklify.client.netio.Packet1Kick;
import xyz.jacklify.netutils.EasyProperties;
import xyz.jacklify.netutils.PacketBase;
import xyz.jacklify.netutils.PacketFactory;

public class InternalClientManager {
	
	private final String protocolVersion = "libjackdare-alpha-test";
	private String gameVersion = null;
	
	public NetworkCallbackManager mgr = null;
	
	private Logger logger = LogManager.getLogger("Game-Client");
	private EasyProperties props = null;
	
	private Bootstrap bootstrap = null;
	private ConnectionManager connection;
	
	public InternalClientManager(String gameVersion, NetworkCallbackManager mgr, EasyProperties props) {
		this.gameVersion = gameVersion;
		this.mgr = mgr;
		this.props = props;
		
		if (this.mgr == null) {
			this.logger.error("Core: No NetworkCallbackManager implementation specified. Using internal StubbedCallbackManager");
			this.mgr = new StubbedCallbackManager();
		}
		PacketFactory.registerPacket(0, Packet0Login.class);
		PacketFactory.registerPacket(1, Packet1Kick.class);
	}
	
	public void connect(String ip, int port, boolean allowServerSwitch, String[] extraData) {
		this.logger.info("Client: Connecting to [" + ip + "/" + port + "]");
		this.logger.info("Client: " + ((allowServerSwitch) ? "Direct connection to peer servers enabled." : "Using server as a proxy for backend servers."));
		EventLoopGroup group = this.createGroup();
		this.bootstrap = new Bootstrap();
		ChannelFuture future = this.bootstrap
		.group(this.createGroup())
		.channel((group instanceof NioEventLoopGroup) ? NioSocketChannel.class : EpollSocketChannel.class)
		.handler(new NetChannelHandler(this, this.protocolVersion, props.getProperty("impl.version", "version-not-defined."), extraData))
		.connect(ip, port);
		
		if (future.cause() != null) {
			this.logger.error("Client: Lost connection to the server. Code 8: An unexpected error has occurred.", future.cause());
		}
		this.logger.info("Client: Cleaning up connection");
		//future.channel().close();
	}
	
	public void connect(String ip, int port, boolean allowServerSwitch) {
		this.connect(ip, port, allowServerSwitch, null);
	}
	
	public void connect(String ip, int port) {
		this.connect(ip, port, false);
	}
	
	public void connect(String ip, int port, String[] extra) {
		this.connect(ip, port, false, extra);
	}
	
	private EventLoopGroup createGroup() {
		if (props.getBoolean("net.use-netty-epoll", false)) {
			if (System.getProperties().getProperty("os.name").contains("linux")) {
				this.logger.info("Client: Using epoll for network handling");
				return new EpollEventLoopGroup(props.getInteger("net.threads-per-core", 4));
			} else {
				this.logger.warn("Client: Cannot use epoll for network transfer, requires a linux-based OS");
			}
		}
		
		this.logger.info("Client: Using NIO for network handling");
		return new NioEventLoopGroup(props.getInteger("net.threads-per-core", 4));
	}
	
	public void sendPacket(PacketBase base) {
		this.connection.send(base);
	}
	
	public void setManager(ConnectionManager mgr) {
		this.connection = mgr;
	}
}
