import groovy.util.ConfigSlurper
import java.util.concurrent.TimeUnit


println "###### > Stoping DNS Slave service"
config = new ConfigSlurper().parse(new File("dnsSlave.properties").toURL())


//	println "postStop: removeSlave ..."
//def dnsMasterService = context.waitForService("dnsMasterService", 60, TimeUnit.SECONDS)
//dnsMasterService.invoke("removeSlave", InetAddress.getLocalHost().getHostAddress())


builder = new AntBuilder();
builder.sequential {
		exec(executable: '/etc/init.d/named' ) {
						 arg value:"stop"
		}
		delete(file:"/etc/${config.namedFile}")
		delete(file:"/var/named/${config.rootHintsFile}")
		delete(file:"/var/named/${config.localZoneFile}")
		delete(file:"/var/named/${config.revFile}" )
		delete(file:"/var/named/${config.namedZone}")
		delete(file:"/var/named/slaves/${config.revFile}" )
		delete(file:"/var/named/slaves/${config.namedZone}")
}
