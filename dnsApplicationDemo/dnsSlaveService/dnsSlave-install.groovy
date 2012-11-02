import org.hyperic.sigar.OperatingSystem
import org.cloudifysource.usm.USMUtils
import org.cloudifysource.dsl.context.ServiceContextFactory

println "###### > Installing DNS (BIND) Slave Server"

config = new ConfigSlurper().parse(new File("dnsSlave.properties").toURL())

builder = new AntBuilder()
builder.sequential {
//	exec(executable: 'yum', osfamily:"unix") {
//							 arg value:"update"
//							}
	exec(executable: 'yum', osfamily:"unix") {
							 arg value:"install"
							 arg value:"-y"
							 arg value:"${config.installPackage1}"
							 arg value:"${config.installPackage2}"
							 arg value:"${config.installPackage3}"
							}

}

println "###### > Installing DNS (BIND) Slave Server "

