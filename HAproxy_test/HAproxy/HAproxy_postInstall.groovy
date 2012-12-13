
import org.hyperic.sigar.OperatingSystem
import org.cloudifysource.dsl.utils.ServiceUtils;
import org.cloudifysource.dsl.context.ServiceContextFactory

context = ServiceContextFactory.getServiceContext()
config = new ConfigSlurper().parse(new File("HAproxy-service.properties").toURL())

haproxy_cfg_file="${config.haproxy_cfg_file}"
haproxy_cfg_dir="${config.haproxy_cfg_dir}"

httpchk="${config.httpchk}"
frontend_name="${config.frontend_name}"
acl_name="${config.acl_name}"
acl_path="${config.acl_path}"
backend_name="${config.backend_name}"

// Install HAproxy using the install script

builder = new AntBuilder()
builder.sequential {
	// Now substitute in the various bits based on this specific installation
	replaceregexp(file:"${context.serviceDirectory}/${haproxy_cfg_file}",
		match:"HTTPCHK",
		replace:"${httpchk}",
		flags: "g")
	replaceregexp(file:"${context.serviceDirectory}/${haproxy_cfg_file}",
		match:"FRONTEND_NAME",
		replace:"${frontend_name}",
		flags: "g")
	replaceregexp(file:"${context.serviceDirectory}/${haproxy_cfg_file}",
		match:"ACL_NAME",
		replace:"${acl_name}",
		flags: "g")
	replaceregexp(file:"${context.serviceDirectory}/${haproxy_cfg_file}",
		match:"ACL_PATH",
		replace:"${acl_path}",
		flags: "g")
	replaceregexp(file:"${context.serviceDirectory}/${haproxy_cfg_file}",
		match:"BACKEND_NAME",
		replace:"${backend_name}",
		flags: "g")
	
	// Put our special version of the haproxy cfg file in place.
	copy(file:"${context.serviceDirectory}/${haproxy_cfg_file}", tofile: "${haproxy_cfg_dir}/${haproxy_cfg_file}")
}