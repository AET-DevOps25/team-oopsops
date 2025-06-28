package oopsops.app.anonymization.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import oopsops.app.anonymization.models.ChangedTerm;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.postgresql.util.PGobject;

@Converter
public class ChangedTermsConverter implements AttributeConverter<List<ChangedTerm>, Object> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object convertToDatabaseColumn(final List<ChangedTerm> changedTerms) {
        try {
            PGobject jsonbObj = new PGobject();
            jsonbObj.setType("jsonb");
            jsonbObj.setValue(mapper.writeValueAsString(changedTerms));
            return jsonbObj;
        } catch (JsonProcessingException | SQLException e) {
            throw new RuntimeException("Error converting changedTerms to JSON", e);
        }
    }

    @Override
    public List<ChangedTerm> convertToEntityAttribute(final Object dbData) {
        if (dbData == null) {
            return Collections.emptyList();
        }
        try {
            String json = dbData.toString();
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error reading changedTerms JSON", e);
        }
    }
}

