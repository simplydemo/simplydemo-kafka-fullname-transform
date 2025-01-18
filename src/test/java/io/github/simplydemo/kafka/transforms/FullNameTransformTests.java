package io.github.simplydemo.kafka.transforms;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FullNameTransformTests {
    private FullNameTransform<SinkRecord> transform;

    @BeforeEach
    void setUp() {
        transform = new FullNameTransform<>();
        transform.configure(null); // Custom configurations can be set here if required
    }

    @Test
    void testTransformAddsFullName() {
        // Arrange: Define the input schema and record
        Schema schema = SchemaBuilder.struct()
                .field("first_name", Schema.STRING_SCHEMA)
                .field("last_name", Schema.STRING_SCHEMA)
                .field("email", Schema.STRING_SCHEMA)
                .build();

        Struct value = new Struct(schema)
                .put("first_name", "Alice")
                .put("last_name", "Smith")
                .put("email", "alice.smith@example.com");

        SinkRecord record = new SinkRecord(
                "test-topic",
                0,
                null,
                null,
                schema,
                value,
                0
        );

        // Act: Apply the transformation
        SinkRecord transformedRecord = transform.apply(record);

        // Assert: Verify the transformed record contains the full_name field
        Struct transformedValue = (Struct) transformedRecord.value();
        assertNotNull(transformedValue);
        assertEquals("Alice Smith", transformedValue.getString("full_name"));
    }

    @Test
    void testTransformHandlesMissingFieldsGracefully() {
        // Arrange: Define the input schema and record with missing last_name
        Schema schema = SchemaBuilder.struct()
                .field("first_name", Schema.STRING_SCHEMA)
                .field("email", Schema.STRING_SCHEMA)
                .build();

        Struct value = new Struct(schema)
                .put("first_name", "Alice")
                .put("email", "alice.smith@example.com");

        SinkRecord record = new SinkRecord(
                "test-topic",
                0,
                null,
                null,
                schema,
                value,
                0
        );

        // Act: Apply the transformation
        SinkRecord transformedRecord = transform.apply(record);

        // Assert: Verify the transformed record contains a full_name field with only the first_name
        Struct transformedValue = (Struct) transformedRecord.value();
        assertNotNull(transformedValue);
        assertEquals("Alice ", transformedValue.getString("full_name"));
    }

    @Test
    void testTransformHandlesNullValues() {
        // Arrange: Define the input schema and record with null values
        Schema schema = SchemaBuilder.struct()
                .field("first_name", Schema.OPTIONAL_STRING_SCHEMA)
                .field("last_name", Schema.OPTIONAL_STRING_SCHEMA)
                .build();

        Struct value = new Struct(schema)
                .put("first_name", null)
                .put("last_name", null);

        SinkRecord record = new SinkRecord(
                "test-topic",
                0,
                null,
                null,
                schema,
                value,
                0
        );

        // Act: Apply the transformation
        SinkRecord transformedRecord = transform.apply(record);

        // Assert: Verify the transformed record contains a full_name field with an empty string
        Struct transformedValue = (Struct) transformedRecord.value();
        assertNotNull(transformedValue);
        assertEquals(" ", transformedValue.getString("full_name"));
    }

    @Test
    void testTransformDoesNothingForNonStructValues() {
        // Arrange: Create a record with a non-Struct value
        SinkRecord record = new SinkRecord(
                "test-topic",
                0,
                null,
                null,
                Schema.STRING_SCHEMA,
                "non-struct-value",
                0
        );

        // Act: Apply the transformation
        SinkRecord transformedRecord = transform.apply(record);

        // Assert: Verify the record remains unchanged
        assertSame(record, transformedRecord);
    }
}
