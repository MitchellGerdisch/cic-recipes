import groovy.util.ConfigSlurper
import java.util.concurrent.TimeUnit


println "###### > Stoping Ulteo Session Application"
config = new ConfigSlurper().parse(new File("ulteoApplication.properties").toURL())

builder = new AntBuilder();
builder.sequential { 
		exec(executable: 'service' ) {
						 arg value:"ulteo-ovd-subsystem"
						 arg value:"stop"
						}
}
