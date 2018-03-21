package io.github.agroportal.ncboproxy.model;

import com.eclipsesource.json.JsonArray;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public interface NCBOCollection extends NCBOOutputModel, Collection<NCBOOutputModel> {
    static NCBOCollection create(final JsonArray array) {
        return new NCBOCollectionImpl(array);
    }

    default Stream<String> asStringStream() {
        return stream()
                .filter(NCBOOutputModel::isValue)
                .map(NCBOOutputModel::asValue)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(value -> value
                        .asString()
                        .orElse(""));
    }


}
