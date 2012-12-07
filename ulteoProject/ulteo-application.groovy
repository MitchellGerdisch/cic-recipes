application {
	
	name="ulteoProject"

	service {
		name = "ulteoManagerService"
	}

	service {
		name = "ulteoApplicationService"
			dependsOn = ["ulteoManagerService"]
	}
}
