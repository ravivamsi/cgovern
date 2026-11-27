package com.cgovern.cgovern;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CgovernWebTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void publicPagesRender() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(content().string(containsString("CGovern")));
        mvc.perform(get("/pricing")).andExpect(status().isOk()).andExpect(content().string(containsString("Starter")));
        mvc.perform(get("/api-docs")).andExpect(status().isOk()).andExpect(content().string(containsString("/api/v2")));
        mvc.perform(get("/support")).andExpect(status().isOk());
        mvc.perform(get("/login")).andExpect(status().isOk());
        mvc.perform(get("/register")).andExpect(status().isOk());
        mvc.perform(get("/api/v2/health")).andExpect(status().isOk());
    }

    @Test
    void catalogRequiresAuth() throws Exception {
        mvc.perform(get("/features/ci/orgs")).andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin@cgovern.dev")
    void ciOrgCrudAndNestedPages() throws Exception {
        mvc.perform(get("/features/ci/orgs")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Platform Engineering")));
        mvc.perform(post("/features/ci/orgs").with(csrf())
                .param("name", "Payments Org")
                .param("description", "Card processing"))
                .andExpect(status().is3xxRedirection());
        mvc.perform(get("/features/ci/orgs")).andExpect(content().string(containsString("Payments Org")));
        mvc.perform(get("/servers")).andExpect(status().isOk()).andExpect(content().string(containsString("ci-worker-east")));
        mvc.perform(get("/templates")).andExpect(status().isOk());
        mvc.perform(get("/plugins")).andExpect(status().isOk());
        mvc.perform(get("/notifications")).andExpect(status().isOk());
        mvc.perform(get("/features/cd/orgs")).andExpect(status().isOk());
        mvc.perform(get("/features/ct/orgs")).andExpect(status().isOk());
        mvc.perform(get("/features/cm/orgs")).andExpect(status().isOk());
        mvc.perform(get("/features/agentic/orgs")).andExpect(status().isOk());
        mvc.perform(post("/features/ci/orgs/1/services/1/run").with(csrf()))
                .andExpect(status().is3xxRedirection());
        mvc.perform(get("/notifications")).andExpect(content().string(containsString("Job")));
    }

    @Test
    void registerCreatesAccount() throws Exception {
        mvc.perform(post("/register").with(csrf())
                .param("fullName", "Ada")
                .param("email", "ada@example.com")
                .param("password", "secret1"))
                .andExpect(redirectedUrlPattern("/login*"));
    }
}
