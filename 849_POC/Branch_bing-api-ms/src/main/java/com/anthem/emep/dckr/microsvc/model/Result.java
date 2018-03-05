
package com.anthem.emep.dckr.microsvc.model;

import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "destinationIndex",
    "originIndex",
    "totalWalkDuration",
    "travelDistance",
    "travelDuration"
})
public class Result {

    @JsonProperty("destinationIndex")
    private Integer destinationIndex;
    @JsonProperty("originIndex")
    private Integer originIndex;
    @JsonProperty("totalWalkDuration")
    private Integer totalWalkDuration;
    @JsonProperty("travelDistance")
    private Double travelDistance;
    @JsonProperty("travelDuration")
    private Double travelDuration;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("destinationIndex")
    public Integer getDestinationIndex() {
        return destinationIndex;
    }

    @JsonProperty("destinationIndex")
    public void setDestinationIndex(Integer destinationIndex) {
        this.destinationIndex = destinationIndex;
    }

    @JsonProperty("originIndex")
    public Integer getOriginIndex() {
        return originIndex;
    }

    @JsonProperty("originIndex")
    public void setOriginIndex(Integer originIndex) {
        this.originIndex = originIndex;
    }

    @JsonProperty("totalWalkDuration")
    public Integer getTotalWalkDuration() {
        return totalWalkDuration;
    }

    @JsonProperty("totalWalkDuration")
    public void setTotalWalkDuration(Integer totalWalkDuration) {
        this.totalWalkDuration = totalWalkDuration;
    }

    @JsonProperty("travelDistance")
    public Double getTravelDistance() {
        return travelDistance;
    }

    @JsonProperty("travelDistance")
    public void setTravelDistance(Double travelDistance) {
        this.travelDistance = travelDistance;
    }

    @JsonProperty("travelDuration")
    public Double getTravelDuration() {
        return travelDuration;
    }

    @JsonProperty("travelDuration")
    public void setTravelDuration(Double travelDuration) {
        this.travelDuration = travelDuration;
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
