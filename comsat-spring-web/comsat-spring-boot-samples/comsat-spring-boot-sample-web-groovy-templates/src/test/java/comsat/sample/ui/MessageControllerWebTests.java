/*
 * COMSAT
 * Copyright (c) 2013-2014, Parallel Universe Software Co. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 3.0
 * as published by the Free Software Foundation.
 */
/*
 * Based on the corresponding class in Spring Boot Samples.
 * Copyright the original authors Biju Kunjummen and Doo-Hwan, Kwak.
 * Released under the ASF 2.0 license.
 */
package comsat.sample.ui;

import java.util.regex.Pattern;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A Basic Spring MVC Test for the Sample Controller"
 *
 * @author Biju Kunjummen
 * @author Doo-Hwan, Kwak
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = SampleGroovyTemplateApplication.class)
@DirtiesContext
public class MessageControllerWebTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testHome() throws Exception {
        this.mockMvc.perform(get("/")).andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>Messages")));
    }

    @Test
    public void testCreate() throws Exception {
        this.mockMvc.perform(post("/").param("text", "FOO text").param("summary", "FOO"))
                .andExpect(status().isMovedTemporarily())
                .andExpect(header().string("location", RegexMatcher.matches("/[0-9]+")));
    }

    @Test
    public void testCreateValidation() throws Exception {
        this.mockMvc.perform(post("/").param("text", "").param("summary", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("is required")));
    }

    private static class RegexMatcher extends TypeSafeMatcher<String> {

        private final String regex;

        public RegexMatcher(String regex) {
            this.regex = regex;
        }

        public static org.hamcrest.Matcher<java.lang.String> matches(String regex) {
            return new RegexMatcher(regex);
        }

        @Override
        public boolean matchesSafely(String item) {
            return Pattern.compile(this.regex).matcher(item).find();
        }

        @Override
        public void describeMismatchSafely(String item, Description mismatchDescription) {
            mismatchDescription.appendText("was \"").appendText(item).appendText("\"");
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a string that matches regex: ")
                    .appendText(this.regex);
        }
    }
}
