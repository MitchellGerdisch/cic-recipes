import org.hyperic.sigar.OperatingSystem
import org.cloudifysource.usm.USMUtils
import org.cloudifysource.dsl.context.ServiceContextFactory

println "###### > Installing Ulteo Session Manager"

config = new ConfigSlurper().parse(new File("ulteoManager.properties").toURL())
context = ServiceContextFactory.getServiceContext()
osConfig = USMUtils.isWindows() ? config.win32 : config.linux

use_preconfigured=config.using_preconfigured_managerVMtemplate

// Need to set scripts as executable regardless of what happens below
builder = new AntBuilder()
builder.sequential {
	chmod(dir:"${context.serviceDirectory}", perm:"+x", includes:"*.sh")
}

// Now figure out if using the preconfigured mode or not
if ( use_preconfigured ) {
	
	// need to restart mysqld on preconfigured
	"service mysqld start".execute()
	
} else {
	// build from scratch 
	// the install scripts will overwrite the preconfigured stuff (I think - probably should test it out ...)
	builder = new AntBuilder()
	os = OperatingSystem.getInstance()
	currVendor = os.getVendor()
	
	println "the Directory is >>  ${context.serviceDirectory}"
	
	switch (currVendor) {
		case ["Ubuntu", "Debian", "Mint"]:
			builder.sequential {
				echo(message:"Running ${context.serviceDirectory}/installOnUbuntu.sh os is ${currVendor}...")
				exec(executable: "${context.serviceDirectory}/installOnUbuntu.sh",osfamily:"unix", failonerror: "true")
			}
			break
	
		case ["Red Hat", "CentOS", "Fedora", "Amazon",""]:
			builder.sequential {
				echo(message:"Running ${context.serviceDirectory}/installOnLinux.sh os is ${currVendor}...")
				exec(executable: "${context.serviceDirectory}/installOnLinux.sh", osfamily:"unix", failonerror: "true")
			}
	
			break
		default: throw new Exception("Support for ${currVendor} is not implemented")
	}
}

// or just use the template as-is