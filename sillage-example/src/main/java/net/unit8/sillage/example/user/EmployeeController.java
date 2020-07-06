package net.unit8.sillage.example.user;

import net.unit8.sillage.ResourceEngine;
import net.unit8.sillage.data.ClassResource;
import net.unit8.sillage.data.ClassResourceFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class EmployeeController {
	private final ResourceEngine resourceEngine;
	private final ClassResourceFactory resourceFactory;

	public EmployeeController(ResourceEngine resourceEngine,
							  ClassResourceFactory resourceFactory) {
		this.resourceEngine = resourceEngine;
		this.resourceFactory = resourceFactory;
	}

	@RequestMapping("/employees")
	public Object employees(HttpServletRequest request,
						   HttpServletResponse response) {
		ClassResource resource = resourceFactory.create(EmployeesResource.class);
		return resourceEngine.run(resource, request, response);
	}

	@RequestMapping("/employee/{id}")
	public Object employee(HttpServletRequest request,
					   HttpServletResponse response) {
		ClassResource resource = resourceFactory.create(EmployeeResource.class);
		return resourceEngine.run(resource, request, response);
	}

}
