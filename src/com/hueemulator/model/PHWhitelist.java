package com.hueemulator.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonProperty;

public class PHWhitelist
{
    @JsonProperty("whitelist")
    private Map<String, PHWhitelistEntry> whitelist;

    public Map<String, PHWhitelistEntry> getWhitelist()
    {
        return whitelist;
    }

    public void setWhitelist(Map<String, PHWhitelistEntry> whitelist)
    {
        this.whitelist = whitelist;
    }

    @JsonAnySetter
    public void handleUnknown(String key, Object value)
    {
        LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>) value;
        PHWhitelistEntry entry = new PHWhitelistEntry();
        entry.setCreateDate((String) map.get("create date"));
        entry.setLastUseDate((String) map.get("last use date"));
        entry.setName((String) map.get("name"));

        whitelist = new HashMap<String, PHWhitelistEntry>();
        whitelist.put(key, entry);
    }
}
