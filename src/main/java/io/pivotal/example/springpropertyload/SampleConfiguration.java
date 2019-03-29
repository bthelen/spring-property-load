package io.pivotal.example.springpropertyload;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "message.default")
@Validated // required to tell Spring to validate this Configuration on load
public class SampleConfiguration {
    // If SpringBoot cannot find message.default.sample SOMEWHERE (Environment Variables, Properties Files,
    // or Config Server), this application will refuse to start.
    @NotNull
    private String sample;

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }
}
