import org.hyperic.sigar.OperatingSystem
import org.cloudifysource.usm.USMUtils
import org.cloudifysource.dsl.context.ServiceContextFactory

println "###### > Installing Ulteo Session Manager"

config = new ConfigSlurper().parse(new File("ulteoManager.properties").toURL())
context = ServiceContextFactory.getServiceContext()
osConfig = USMUtils.isWindows() ? config.win32 : config.linux

use_preconfigured=config.using_preconfigured_managerVMtemplate

if ( use_preconfigured ) {

	// restart mysqld 
	"service mysqld start".execute()
	
} else {
	// build from scratch
	builder = new AntBuilder()
	os = OperatingSystem.getInstance()
	currVendor = os.getVendor()
	
	println "the Directory is >>  ${context.serviceDirectory}"
	
	switch (currVendor) {
		case ["Ubuntu", "Debian", "Mint"]:
			builder.sequential {
				chmod(dir:"${context.serviceDirectory}", perm:"+x", includes:"*.sh")
				echo(message:"Running ${context.serviceDirectory}/installOnUbuntu.sh os is ${currVendor}...")
				exec(executable: "${context.serviceDirectory}/installOnUbuntu.sh",osfamily:"unix", failonerror: "true")
			}
			break
	
		case ["Red Hat", "CentOS", "Fedora", "Amazon",""]:
			builder.sequential {
				chmod(dir:"${context.serviceDirectory}", perm:"+x", includes:"*.sh")
				echo(message:"Running ${context.serviceDirectory}/installOnLinux.sh os is ${currVendor}...")
				exec(executable: "${context.serviceDirectory}/installOnLinux.sh", osfamily:"unix", failonerror: "true")
			}
	
			break
		default: throw new Exception("Support for ${currVendor} is not implemented")
	}
}

// or just use the template as-is