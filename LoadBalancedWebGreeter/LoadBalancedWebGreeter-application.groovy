application {
	
	name="HAproxy"

	service {
		name = "HAproxy"
	}

	service {
		name = "webService"
			dependsOn = ["HAproxy" ]
	}
	
}
