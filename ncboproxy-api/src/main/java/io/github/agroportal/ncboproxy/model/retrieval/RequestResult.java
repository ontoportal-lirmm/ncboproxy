package io.github.agroportal.ncboproxy.model.retrieval;

public interface RequestResult {
    int getCode();

    String getMessage();

    static RequestResult empty() {
        return new BioportalRESTRequest.EmptyRequestResult();
    }

}
