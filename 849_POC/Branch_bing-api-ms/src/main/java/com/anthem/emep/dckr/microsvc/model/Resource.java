
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
    "__type",
    "destinations",
    "errorMessage",
    "origins",
    "results"
})
public class Resource {

    @JsonProperty("__type")
    private String type;
    @JsonProperty("destinations")
    private List<Destination> destinations = null;
    @JsonProperty("errorMessage")
    private String errorMessage;
    @JsonProperty("origins")
    private List<Origin> origins = null;
    @JsonProperty("results")
    private List<Result> results = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("__type")
    public String getType() {
        return type;
    }

    @JsonProperty("__type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("destinations")
    public List<Destination> getDestinations() {
        return destinations;
    }

    @JsonProperty("destinations")
    public void setDestinations(List<Destination> destinations) {
        this.destinations = destinations;
    }

    @JsonProperty("errorMessage")
    public String getErrorMessage() {
        return errorMessage;
    }

    @JsonProperty("errorMessage")
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @JsonProperty("origins")
    public List<Origin> getOrigins() {
        return origins;
    }

    @JsonProperty("origins")
    public void setOrigins(List<Origin> origins) {
        this.origins = origins;
    }

    @JsonProperty("results")
    public List<Result> getResults() {
        return results;
    }

    @JsonProperty("results")
    public void setResults(List<Result> results) {
        this.results = results;
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
