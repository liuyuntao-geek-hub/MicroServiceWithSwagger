
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
    "authenticationResultCode",
    "brandLogoUri",
    "copyright",
    "resourceSets",
    "statusCode",
    "statusDescription",
    "traceId"
})
public class BingResponse {

    @JsonProperty("authenticationResultCode")
    private String authenticationResultCode;
    @JsonProperty("brandLogoUri")
    private String brandLogoUri;
    @JsonProperty("copyright")
    private String copyright;
    @JsonProperty("resourceSets")
    private List<ResourceSet> resourceSets = null;
    @JsonProperty("statusCode")
    private Integer statusCode;
    @JsonProperty("statusDescription")
    private String statusDescription;
    @JsonProperty("traceId")
    private String traceId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("authenticationResultCode")
    public String getAuthenticationResultCode() {
        return authenticationResultCode;
    }

    @JsonProperty("authenticationResultCode")
    public void setAuthenticationResultCode(String authenticationResultCode) {
        this.authenticationResultCode = authenticationResultCode;
    }

    @JsonProperty("brandLogoUri")
    public String getBrandLogoUri() {
        return brandLogoUri;
    }

    @JsonProperty("brandLogoUri")
    public void setBrandLogoUri(String brandLogoUri) {
        this.brandLogoUri = brandLogoUri;
    }

    @JsonProperty("copyright")
    public String getCopyright() {
        return copyright;
    }

    @JsonProperty("copyright")
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    @JsonProperty("resourceSets")
    public List<ResourceSet> getResourceSets() {
        return resourceSets;
    }

    @JsonProperty("resourceSets")
    public void setResourceSets(List<ResourceSet> resourceSets) {
        this.resourceSets = resourceSets;
    }

    @JsonProperty("statusCode")
    public Integer getStatusCode() {
        return statusCode;
    }

    @JsonProperty("statusCode")
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    @JsonProperty("statusDescription")
    public String getStatusDescription() {
        return statusDescription;
    }

    @JsonProperty("statusDescription")
    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    @JsonProperty("traceId")
    public String getTraceId() {
        return traceId;
    }

    @JsonProperty("traceId")
    public void setTraceId(String traceId) {
        this.traceId = traceId;
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
