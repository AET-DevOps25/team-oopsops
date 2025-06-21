package oopsops.app.anonymization.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChangedTerm {
    private List<Term> changedTerms;

    public ChangedTerm() {
    }

    public List<Term> getChangedTerms() {
        return changedTerms;
    }

    public void setChangedTerms(final List<Term> changedTerms) {
        this.changedTerms = changedTerms;
    }
}

