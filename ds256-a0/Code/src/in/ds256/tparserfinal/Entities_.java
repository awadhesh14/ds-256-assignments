
package in.ds256.tparserfinal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "hashtags",
    "urls",
    "user_mentions",
    "symbols"
})
public class Entities_ {

    @JsonProperty("hashtags")
    private List<Hashtag_> hashtags = null;
    @JsonProperty("urls")
    private List<Object> urls = null;
    @JsonProperty("user_mentions")
    private List<UserMention> userMentions = null;
    @JsonProperty("symbols")
    private List<Object> symbols = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Entities_() {
    }

    /**
     * 
     * @param symbols
     * @param urls
     * @param hashtags
     * @param userMentions
     */
    public Entities_(List<Hashtag_> hashtags, List<Object> urls, List<UserMention> userMentions, List<Object> symbols) {
        super();
        this.hashtags = hashtags;
        this.urls = urls;
        this.userMentions = userMentions;
        this.symbols = symbols;
    }

    @JsonProperty("hashtags")
    public List<Hashtag_> getHashtags() {
        return hashtags;
    }

    @JsonProperty("hashtags")
    public void setHashtags(List<Hashtag_> hashtags) {
        this.hashtags = hashtags;
    }

    @JsonProperty("urls")
    public List<Object> getUrls() {
        return urls;
    }

    @JsonProperty("urls")
    public void setUrls(List<Object> urls) {
        this.urls = urls;
    }

    @JsonProperty("user_mentions")
    public List<UserMention> getUserMentions() {
        return userMentions;
    }

    @JsonProperty("user_mentions")
    public void setUserMentions(List<UserMention> userMentions) {
        this.userMentions = userMentions;
    }

    @JsonProperty("symbols")
    public List<Object> getSymbols() {
        return symbols;
    }

    @JsonProperty("symbols")
    public void setSymbols(List<Object> symbols) {
        this.symbols = symbols;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("hashtags", hashtags).append("urls", urls).append("userMentions", userMentions).append("symbols", symbols).append("additionalProperties", additionalProperties).toString();
    }

}
