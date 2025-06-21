package oopsops.app.anonymization.client;

/*import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import oopsops.app.document.models.AnonymizeRequest;
import oopsops.app.document.models.GenAiResponse;*/

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import oopsops.app.anonymization.models.AnonymizeRequest;
import oopsops.app.anonymization.models.GenAiResponse;

//@FeignClient(name = "genai-client", url = "${genai.service.url}")
public interface GenAiClient {
    @PostMapping("/anonymize")
    GenAiResponse anonymize(@RequestBody AnonymizeRequest request);
}
