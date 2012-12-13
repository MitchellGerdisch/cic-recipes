import org.hyperic.sigar.OperatingSystem
import org.cloudifysource.usm.USMUtils
import org.cloudifysource.dsl.context.ServiceContextFactory
import java.util.concurrent.TimeUnit


config = new ConfigSlurper().parse(new File("RunDeckRemoteNodes-service.properties").toURL())
remotenode_ssh_dir="${config.remotenode_ssh_dir}"
pub_ssh_key_file="${remotenode_ssh_dir}/${config.rundeck_public_ssh_key}"
remotenode_authorized_keys_file="${config.remotenode_authorized_keys_file}"
bin_dir="${config.bin_dir}"
test_script="${config.test_script}"
dd_script="${config.dd_script}"
num_wanted_file="${config.num_wanted_file}"
check_num_wanted_script="${config.check_num_wanted_script}"
set_num_wanted_script="${config.set_num_wanted_script}"

currVendor="linux"

context = ServiceContextFactory.getServiceContext()
def remoteNodesService = context.waitForService("RunDeckRemoteNodes", 300, TimeUnit.SECONDS)
num_planned_instances=remoteNodesService.numberOfPlannedInstances

// Set up and place the project.properties file used by RunDeck
println "RemoteNode_preStart.groovy: Placing public SSH key file under ${remotenode_ssh_dir}"

Builder = new AntBuilder()
Builder.sequential {
	mkdir(dir:"${remotenode_ssh_dir}");
	chmod(file:"${remotenode_ssh_dir}", perm:'700')
	copy(file:"${context.serviceDirectory}/${config.rundeck_public_ssh_key}", tofile:"${pub_ssh_key_file}")
	chmod(file:"${pub_ssh_key_file}", perm:'400')
	concat(destfile:"${remotenode_authorized_keys_file}", append:"true") {
		filelist(dir:"${remotenode_ssh_dir}", files:"${pub_ssh_key_file}")
	}
	chmod(file:"${remotenode_authorized_keys_file}", perm:'400')
}

// Drop in a scripts for testing things out
Builder = new AntBuilder()
Builder.sequential {
	mkdir(dir:"${bin_dir}")
	copy(file:"${context.serviceDirectory}/${test_script}", tofile:"${bin_dir}/${test_script}")
	copy(file:"${context.serviceDirectory}/${dd_script}", tofile:"${bin_dir}/${dd_script}")
	copy(file:"${context.serviceDirectory}/deploy_apache.sh", tofile:"${bin_dir}/deploy_apache.sh")
	
	chmod(dir:"${bin_dir}", perm:"+x", includes:"*.sh")
}

// A little hack to add an http web service
installScript="deploy_apache.sh"
builder = new AntBuilder()
builder.sequential {

	echo(message:"RemoteNode_install.groovy: Running ${bin_dir}/${installScript} ...")
	exec(executable: "${bin_dir}/${installScript}",failonerror: "true")
}

/* No longer used
// Place the remotenodes scaling scripts and initialize the num_remotenodes_wanted.txt file
Builder = new AntBuilder()
Builder.sequential {
	echo(message:"${num_planned_instances}", file:"/root/bin/num_remotenodes_wanted.txt", append:"false");
	copy(file:"${context.serviceDirectory}/${check_num_wanted_script}", tofile:"${bin_dir}/${check_num_wanted_script}")
	copy(file:"${context.serviceDirectory}/${set_num_wanted_script}", tofile:"${bin_dir}/${set_num_wanted_script}")
	chmod(dir:"${bin_dir}", perm:"+x", includes:"*.sh")
}
*/
