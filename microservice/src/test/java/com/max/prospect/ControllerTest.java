package com.max.prospect;

import com.max.prospect.application.commands.CreateProspectCommand;
import com.max.prospect.domain.ProspectAggregate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;

@WebMvcTest(Controller.class)
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * We use @MockBean to create and inject a mock for the ProspectAggregate
     * (if you do not do so, the application context cannot start),
     * and we set its expectations using Mockito
     */
    @MockBean
    private ProspectAggregate prospectAggregate;

    private CreateProspectCommand createProspectCommand;

    @Test
    public void testCreateProspect() throws Exception {

        createProspectCommand = new CreateProspectCommand();

       // when(prospectAggregate.createProspectHandler(createProspectCommand)).thenReturn("1234567");

        this.mockMvc.perform(get("/prospects/")).andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("123456789")));
    }

}
