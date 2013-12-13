package com.hueemulator.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PHGroupsEntry
{
    @JsonProperty("name")
    private String  name;
    
    @JsonProperty("action")
    private PHLightState   lightState;
    
    @JsonProperty("lights")
    private List<String>    lightIdentifiers;
    
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public PHLightState getLightState()
    {
        return lightState;
    }

    public void setLightState(PHLightState lightState)
    {
        this.lightState = lightState;
    }

    public List<String> getLightIdentifiers() {
        return lightIdentifiers;
    }

    public void setLightIdentifiers(List<String> lightIdentifiers) {
        this.lightIdentifiers = lightIdentifiers;
    }

}

