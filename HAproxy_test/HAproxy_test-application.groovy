application {
	
	name="HAproxy_test"
	
	service {
		name = "HAproxy"
	}
	
	service {
		name = "RunDeckRemoteNodes"
			dependsOn = ["HAproxy"]
	}

}
