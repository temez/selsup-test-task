package dev.temez.task;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {

  public static void main(String[] args) throws InterruptedException, IOException {
    CrptApiClient crptApiClient = new CrptApiClient(TimeUnit.SECONDS, 3);

    CrptApiClient.DocumentCreateRequest request = new CrptApiClient.DocumentCreateRequest();
    CrptApiClient.DocumentCreateRequest.Description description = new CrptApiClient.DocumentCreateRequest.Description();
    description.setParticipantInn("inn");
    request.setDocumentId("id");
    request.setDocumentStatus("unknown");
    request.setDescription(description);

    crptApiClient.createDocument(request);
    crptApiClient.createDocument(request);
    crptApiClient.createDocument(request);
    crptApiClient.createDocument(request);
    crptApiClient.createDocument(request);
    crptApiClient.createDocument(request);
    crptApiClient.shutdown();
  }
}
