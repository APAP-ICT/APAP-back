package com.example.apapbackend.global.gemini;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GeminiRequest {

    // 요청 내용
    private List<Content> contents;

    // 텍스트로만 요청 시
    public GeminiRequest(String text) {
        Part part = new TextPart(text);
        Content content = new Content(Collections.singletonList(part));
        this.contents = Arrays.asList(content);
    }

    // 텍스트 + 이미지로 요청 시
    public GeminiRequest(String text, InlineData inlineData) {
        List<Content> contents = List.of(
            new Content(
                List.of(
                    new TextPart(text),
                    new InlineDataPart(inlineData)
                )
            )
        );

        this.contents = contents;
    }

    @Getter
    @AllArgsConstructor
    private static class Content {

        private List<Part> parts;
    }

    interface Part {

    }

    @Getter
    @AllArgsConstructor
    private static class TextPart implements Part {

        public String text;
    }

    @Getter
    @AllArgsConstructor
    private static class InlineDataPart implements Part {

        public InlineData inlineData;
    }

    @Getter
    @AllArgsConstructor
    public static class InlineData {

        private String mimeType;
        private String data;
    }
}
