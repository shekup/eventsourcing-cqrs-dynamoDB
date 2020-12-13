package com.max.prospect;

import com.max.prospect.application.commands.CreateProspectCommand;
import com.max.prospect.domain.ProspectAggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    ProspectAggregate prospectAggregate;

    @RequestMapping("/")
    public String index() {
        System.out.println("Hello Users");
        return "Hello Users";
    }

    @RequestMapping(value="/prospects/")
    public String createProspect(){
        CreateProspectCommand createProspectCommand = new CreateProspectCommand();
        return prospectAggregate.createProspectHandler(createProspectCommand);
    }
}
