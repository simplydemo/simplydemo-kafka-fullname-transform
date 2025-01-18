package io.github.simplydemo.kafka.transforms;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.transforms.Transformation;

import java.util.Map;

public class FullNameTransform<R extends ConnectRecord<R>> implements Transformation<R> {

    @Override
    public R apply(R record) {
        // Check if the record's value is a Struct
        if (record.value() instanceof Struct value && record.valueSchema() != null) {
            var schema = record.valueSchema();

            // Verify the required fields exist in the schema
            if (schema.field("first_name") != null || schema.field("last_name") != null) {
                var firstName = schema.field("first_name") != null ? value.getString("first_name") : null;
                var lastName = schema.field("last_name") != null ? value.getString("last_name") : null;

                // Generate the full_name field
                var fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");

                // Build a new schema including all existing fields and the full_name field
                var newSchemaBuilder = SchemaBuilder.struct().name(schema.name());
                schema.fields().forEach(field -> newSchemaBuilder.field(field.name(), field.schema()));
                newSchemaBuilder.field("full_name", Schema.STRING_SCHEMA); // Add full_name field
                var newSchema = newSchemaBuilder.build();

                // Create a new Struct for the updated value
                var newValue = new Struct(newSchema);
                // Copy all existing fields to the new Struct
                schema.fields().forEach(field -> newValue.put(field.name(), value.get(field.name())));
                // Add the new full_name field
                newValue.put("full_name", fullName);

                return record.newRecord(record.topic(), record.kafkaPartition(), record.keySchema(), record.key(), newSchema, newValue, record.timestamp());
            }
        }
        // Return the record as is if conditions are not met
        return record;
    }

    public ConfigDef config() {
        return new ConfigDef();
    }

    public void close() {
    }

    public void configure(Map<String, ?> configs) {
    }
}