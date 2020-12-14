package com.max.prospect;

import com.max.prospect.application.commands.CreateProspectCommand;
import com.max.prospect.application.commands.PropsectPersonalDetailsCommand;
import com.max.prospect.domain.ProspectAggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class Controller {

    @Autowired
    ProspectAggregate prospectAggregate;

    @RequestMapping("/")
    public String index() {
        System.out.println("Hello, World");
        return "Hello, World";
    }

    /**
     * This should be POST method but for convenience GET is written
     * @return
     */
    @RequestMapping(value="/prospects/")
    public @ResponseBody String createProspect(){
        CreateProspectCommand createProspectCommand = new CreateProspectCommand();
        String prospectId = prospectAggregate.createProspectHandler(createProspectCommand);
        return prospectId;
    }

    /**
     * This should be POST method but for convenience GET is written
     * @return
     */
    @RequestMapping(value="/prospects/{prospectId}/addPersonal")
    public HttpStatus addPersonalDetails(@PathVariable("prospectId") String prospectId){
        System.out.println("Adding personal details for prospectId: " + prospectId);
        PropsectPersonalDetailsCommand personalProspectCommand = new PropsectPersonalDetailsCommand();
        personalProspectCommand.setFirstName("Abhishek");
        personalProspectCommand.setMiddleName("");
        personalProspectCommand.setLastName("Upadhyay");
        personalProspectCommand.setDOB(new Date());
        personalProspectCommand.setProspectId(prospectId);
        try{
            prospectAggregate.addPersonalDetails(personalProspectCommand);
            return HttpStatus.OK;
        }catch(Exception e){
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }
}
