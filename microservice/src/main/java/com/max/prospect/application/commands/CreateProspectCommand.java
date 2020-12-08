package com.max.prospect.application.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProspectCommand {
	
	private String firstName;
	
	private String middleName;
	
	private String lastName;

}
