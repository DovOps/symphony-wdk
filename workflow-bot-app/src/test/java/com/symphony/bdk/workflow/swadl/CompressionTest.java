package com.symphony.bdk.workflow.swadl;

import com.symphony.bdk.workflow.management.BigStringCompressor;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class CompressionTest {

  @Test
  void compressString() throws IOException {
    String inputString = """
        id: my-workflow
        
        activities:
          - send-message:
              id: counter
              on:
                message-received:
                  content: /count
              content: "version1"
          - execute-script:
              id: vars
              script: |
                counter = wdk.readShared('test', 'counter')
                counter++
                wdk.writeShared('test', 'counter', counter)
          - send-message:
              id: send_counter
              content: ${readShared('test', 'counter')}
        """;

    BigStringCompressor compressor = new BigStringCompressor();
    byte[] converted = compressor.convertToDatabaseColumn(inputString);
    assertThat(converted.length).isLessThan(inputString.length());

    String attribute = compressor.convertToEntityAttribute(converted);
    assertThat(attribute).isEqualTo(inputString);
  }
}
