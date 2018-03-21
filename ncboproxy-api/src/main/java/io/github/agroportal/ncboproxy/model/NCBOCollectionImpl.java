package io.github.agroportal.ncboproxy.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public class NCBOCollectionImpl implements NCBOCollection {

    private final Collection<NCBOOutputModel> objects;
    private final JsonArray jsonArray;


    NCBOCollectionImpl(final JsonArray jsonArray) {
        this.jsonArray = jsonArray;
        objects = new ArrayList<>();
        for(int i=0; i< jsonArray.size(); i++){
            final JsonValue value = jsonArray.get(i);
            if(value.isObject()){
                objects.add(JSONLDObject.create(value.asObject()));
            } else if(value.isNumber() || value.isBoolean() || value.isString()){
                objects.add(JSONValue.create(value));
            } else if(value.isArray()){
                objects.add(NCBOCollection.create(value.asArray()));
            }
        }
    }

    @Override
    public JsonValue getModelRoot() {
        return jsonArray;
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public Optional<NCBOCollection> asCollection() {
        return Optional.of(this);
    }

    @Override
    public int size() {
        return objects.size();
    }

    @Override
    public boolean isEmpty() {
        return objects.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return objects.contains(o);
    }

    @Override
    public Iterator<NCBOOutputModel> iterator() {
        return objects.iterator();
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @SuppressWarnings("all")
    @Override
    public <T> T[] toArray(final T[] a) {
        return objects.toArray(a);
    }

    @Override
    public boolean add(final NCBOOutputModel jsonldObject) {
        return objects.add(jsonldObject);
    }

    @Override
    public boolean remove(final Object o) {
        return objects.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return objects.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends NCBOOutputModel> c) {
        return objects.addAll(c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return objects.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return objects.retainAll(c);
    }

    @Override
    public void clear() {
        objects.clear();
    }
}
