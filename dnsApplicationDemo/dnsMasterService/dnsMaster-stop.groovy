import groovy.util.ConfigSlurper
import java.util.concurrent.TimeUnit


println "###### > Stoping DNS Master service"
config = new ConfigSlurper().parse(new File("dnsMaster.properties").toURL())

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
}
