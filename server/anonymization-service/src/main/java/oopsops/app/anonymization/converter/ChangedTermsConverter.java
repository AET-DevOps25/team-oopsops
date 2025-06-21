package oopsops.app.anonymization.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import oopsops.app.anonymization.models.ChangedTerm;

import java.util.Optional;

@Converter
public class ChangedTermsConverter implements AttributeConverter<ChangedTerm, String> {

    @Override
    public String convertToDatabaseColumn(final ChangedTerm optionValue) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final ChangedTerm changedTerm = Optional.ofNullable(optionValue)
                .orElseGet(ChangedTerm::new);
            return mapper.writeValueAsString(changedTerm);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChangedTerm convertToEntityAttribute(final String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(dbData, ChangedTerm.class);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

