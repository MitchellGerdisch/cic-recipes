import groovy.util.ConfigSlurper
import java.util.concurrent.TimeUnit


println "###### > Stoping Ulteo Session Manager"
config = new ConfigSlurper().parse(new File("ulteoManager.properties").toURL())

builder = new AntBuilder();
builder.sequential { 
		exec(executable: 'service' ) {
						 arg value:"httpd"
						 arg value:"stop"
						}
}
