import org.hyperic.sigar.OperatingSystem
import org.cloudifysource.usm.USMUtils
import org.cloudifysource.dsl.context.ServiceContextFactory
import java.util.concurrent.TimeUnit


config = new ConfigSlurper().parse(new File("RunDeck-service.properties").toURL())
project_dir="${config.rundeck_config_dir}/${config.rundeck_project_name}"
project_properties_file="${project_dir}/etc/${config.rundeck_project_properties}"
resources_file="${project_dir}/etc/${config.rundeck_resources_xml}"
priv_ssh_key_file="${config.rundeck_server_ssh_dir}/${config.rundeck_private_ssh_key}"

context = ServiceContextFactory.getServiceContext()

// Set up and place the project.properties file used by RunDeck
println "RunDeck_preStart.groovy: Building and placing project.properties file under ${config.rundeck_config_dir}/${config.rundeck_project_name}"

Builder = new AntBuilder()
Builder.sequential {
	mkdir(dir:"${project_dir}");
	chown(file:"${project_dir}", owner:"rundeck:rundeck", type:"both");
	chown(file:"${project_dir}/etc", owner:"rundeck:rundeck", type:"both");
	echo(message:"#Project configuration, generated by RunDeckService\n", file:"${project_properties_file}", append:"false");
	echo(message:"project.name=${config.rundeck_project_name}\n", file:"${project_properties_file}", append:"true");
	echo(message:"resources.source.1.config.requireFileExists=false\n", file:"${project_properties_file}", append:"true");
	echo(message:"service.NodeExecutor.default.provider=jsch-ssh\n", file:"${project_properties_file}", append:"true");
	echo(message:"resources.source.1.config.includeServerNode=false\n", file:"${project_properties_file}", append:"true");
	echo(message:"project.resources.file=${resources_file}\n", file:"${project_properties_file}", append:"true");
	echo(message:"project.ssh-keypath=${config.rundeck_server_ssh_dir}/${config.rundeck_private_ssh_key}\n", file:"${project_properties_file}", append:"true");
	echo(message:"service.FileCopier.default.provider=jsch-scp\n", file:"${project_properties_file}", append:"true");
	echo(message:"resources.source.1.type=file\n", file:"${project_properties_file}", append:"true");
	chown(file:"${project_properties_file}", owner:"rundeck:rundeck", type:"both");
	copy(file:"${context.serviceDirectory}/${config.rundeck_private_ssh_key}", tofile:"${priv_ssh_key_file}")
	chmod(file:"${priv_ssh_key_file}", perm:'400')
	chown(file:"${priv_ssh_key_file}", owner:"rundeck:rundeck", type:"both")
}

// Set up and place the resources.xml file used by RunDeck
// Loop through remoteNodeHostInstances and grab remoteNodeHostInstances[X].hostAddress and build resources.xml file
Builder = new AntBuilder()
Builder.sequential {
	echo(message:"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n", file:"${resources_file}", append:"false");
	echo(message:"<project>\n", file:"${resources_file}", append:"true");
	//echo(message:"<node name=\"localhost\" description=\"Rundeck server node\" tags=\"\" hostname=\"localhost\" osArch=\"amd64\" osFamily=\"unix\" osName=\"Linux\" osVersion=\"2.6.32-279.2.1.el6.x86_64\" username=\"root\"/>\n", file:"${resources_file}", append:"true");
}

def remoteNodesService = context.waitForService("RunDeckRemoteNodes", 300, TimeUnit.SECONDS)
remoteNodesHostInstances = remoteNodesService.waitForInstances(remoteNodesService.numberOfPlannedInstances, 300, TimeUnit.SECONDS)
def remotenode_num = 1

for ( remotenodeinstance in remoteNodeHostInstances ) {
	remotenode_ip="${remotenodeinstance.hostAddress}"
	println "RundDeck_prestart.groovy: remotenode-${remotenode_num} has IP ${remotenode_ip}"
	Builder.sequential {
		echo(message:"<node name=\"remotenode-${remotenode_num}\" description=\"remotenode-${remotenode_num}\" tags=\"\" hostname=\"${remotenode_ip}\" osArch=\"amd64\" osFamily=\"unix\" osName=\"Linux\" osVersion=\"2.6.32-279.2.1.el6.x86_64\" username=\"root\"/>\n", file:"${resources_file}", append:"true");
	}
	remotenode_num++
}

Builder.sequential {
	echo(message:"</project>\n", file:"${resources_file}", append:"true");
	chown(file:"${resources_file}", owner:"rundeck:rundeck", type:"both");
}
