package com.max.prospect.domain;

import com.max.prospect.application.commands.CreateProspectCommand;
import com.max.prospect.domain.repository.ProspectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProspectAggregate {

	@Autowired
	ProspectRepository prospectRepository;
	
	public void createProspectHandler(CreateProspectCommand createProspectCommand) {
		prospectRepository.createProspect();
	}

}
