
package com.anthem.emep.dckr.microsvc.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "estimatedTotal",
    "resources"
})
public class ResourceSet {

    @JsonProperty("estimatedTotal")
    private Integer estimatedTotal;
    @JsonProperty("resources")
    private List<Resource> resources = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("estimatedTotal")
    public Integer getEstimatedTotal() {
        return estimatedTotal;
    }

    @JsonProperty("estimatedTotal")
    public void setEstimatedTotal(Integer estimatedTotal) {
        this.estimatedTotal = estimatedTotal;
    }

    @JsonProperty("resources")
    public List<Resource> getResources() {
        return resources;
    }

    @JsonProperty("resources")
    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
