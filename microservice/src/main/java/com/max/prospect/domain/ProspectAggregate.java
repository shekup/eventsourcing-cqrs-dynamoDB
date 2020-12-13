package com.max.prospect.domain;

import com.max.prospect.application.commands.CreateProspectCommand;
import com.max.prospect.application.commands.PropsectPersonalDetailsCommand;
import com.max.prospect.domain.repository.ProspectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ProspectAggregate {

	@Autowired
	ProspectRepository prospectRepository;
	
	public String createProspectHandler(CreateProspectCommand createProspectCommand) {
		return prospectRepository.createProspect();
	}

	public void addPersonalDetails(PropsectPersonalDetailsCommand personalDetailsCommand) throws Exception{
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("FIRST NAME", personalDetailsCommand.getFirstName());
		values.put("MIDDLE NAME", personalDetailsCommand.getMiddleName());
		values.put("LAST NAME", personalDetailsCommand.getLastName());
		values.put("DATE OF BIRTH", personalDetailsCommand.getDOB().toString());
		prospectRepository.addPersonalDetails(personalDetailsCommand.getProspectId(),values);
	}

}
