package io.github.agroportal.ncboproxy.model;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.List;
import java.util.stream.Collectors;

public interface JSONLDLink {


    String getLinkName();

    String getLinkValue();

    String getLinkContext();

    static JSONLDLink create(final String linkName, final String linkValue, final String linkContext) {
        return new JSONLDLinkImpl(linkName, linkValue, linkContext);
    }

    static List<JSONLDLink> create(final JsonObject linksObject) {
        final JsonValue contextValue = linksObject.get(JSONLDConstants.CONTEXT);
        final JsonObject atContext = (contextValue != null) ? contextValue.asObject() : null;
        return linksObject
                .names()
                .stream()
                .filter(name -> !name.equals("@context"))
                .map(name -> create(name,
                        linksObject.getString("name", ""),
                        (atContext != null) ? atContext.getString(name, "") : ""))
                .collect(Collectors.toList());
    }
}
