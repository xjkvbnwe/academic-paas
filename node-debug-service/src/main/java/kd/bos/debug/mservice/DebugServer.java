package kd.bos.debug.mservice;

import java.net.InetAddress;
import kd.bos.config.client.util.ConfigUtils;
import kd.bos.db.DB;
import kd.bos.redis.JedisClient;
import kd.bos.redis.RedisFactory;
import kd.bos.service.webserver.JettyServer;

public class DebugServer {

	public static void main(String[] args) throws Exception {
		String hostName = InetAddress.getLocalHost().getHostName();
		System.setProperty(ConfigUtils.APP_NAME_KEY, hostName + "-cosmic");
		System.setProperty("mq.debug.queue.tag", hostName);

		// 设置集群环境名称和配置服务器地址
		String zk = System.getenv("zookeeper");
		String mc = System.getenv("mc.server.url");
		System.setProperty(ConfigUtils.CLUSTER_NAME_KEY, System.getenv("clusterName"));
		System.setProperty("domain.tenantCode", System.getenv("domain.tenantCode"));
		System.setProperty(ConfigUtils.CONFIG_URL_KEY, zk);
		System.setProperty("Schedule.zk.server", zk);
		System.setProperty("dubbo.registry.address", zk);
		System.setProperty("configAppName", "mservice,web");
		System.setProperty("webmserviceinone", "true");

		System.setProperty("file.encoding", "utf-8");
		System.setProperty("xdb.enable", "false");

		System.setProperty("mq.consumer.register", "true");
		System.setProperty("MONITOR_HTTP_PORT", "9998");
		System.setProperty("JMX_HTTP_PORT", "9091");
		System.setProperty("dubbo.protocol.port", "28888");
		System.setProperty("dubbo.consumer.url", "dubbo://localhost:28888");
		System.setProperty("dubbo.consumer.url.qing", "dubbo://localhost:30880");
		System.setProperty("dubbo.registry.register", "true");
		System.setProperty("dubbo.service.lookup.local", "false");
		System.setProperty("appSplit", "false");
		System.setProperty("dubbo.registry.register", "true");
		System.setProperty("tempfile.cachetype", "redis");

		System.setProperty("mq.consumer.register", "true");
		// 定义一个唯一的标识别和其他人重复
		System.setProperty("mq.debug.queue.tag", "dream");
		System.setProperty("lightweightdeploy", "false");
		// 队列发送方、消费方均需要该配置项，包括具体的配置文件，生产环境可配在服务节点的
		// 启动参数中
		System.setProperty("mqConfigFiles.config", "erkai.consummqconfig.xml");

		// 按系统配置，不需要调整
		System.setProperty("JETTY_WEBAPP_PATH", "/home/kduser/cosmic/mservice-cosmic/webapp");
		System.setProperty("JETTY_WEBRES_PATH", "/home/kduser/cosmic/static-file-service/webapp");
		System.setProperty("JETTY_WEB_PORT", "8080");
		System.setProperty("JETTY_CONTEXT", "ierp");

		System.setProperty("tenant.code.type", "config");
		System.setProperty("mc.server.url", mc);
		System.setProperty("env.type", "dev");
		System.setProperty("trace.reporter.type", "sword");
		//钉钉
		System.setProperty("authtication.apps", "kd.bos.login.thirdauth.app.dingding.DDAuthtication");
		JettyServer.main(null);
	}
}
