 import groovy.util.ConfigSlurper

println "###### > Starting DNS Master Service"

builder = new AntBuilder();
//builder.sequential { 
//		exec(executable: '/etc/init.d/named' ) {
//						 arg value:"restart"
//						}
builder.sequential { 
		exec(executable: '/usr/sbin/named' ) {
						 arg value:"-g"
						}
}
//sleep(Long.MAX_VALUE)