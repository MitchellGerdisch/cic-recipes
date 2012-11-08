 import groovy.util.ConfigSlurper
 import org.hyperic.sigar.OperatingSystem
 import org.cloudifysource.usm.USMUtils
 import org.cloudifysource.dsl.context.ServiceContextFactory
 
println "###### > ulteoManager-poststart.groovy"

builder = new AntBuilder()
builder.sequential {
	echo(message:"Configuring Database with initial set of users and stuff")
	exec(executable:"poststart.sh", osfamily:"unix",failonerror: "true")
}
