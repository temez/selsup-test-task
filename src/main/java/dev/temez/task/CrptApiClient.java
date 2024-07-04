package dev.temez.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CrptApiClient {

  private final HttpClient httpClient = HttpClient.newBuilder().build();

  private final ObjectMapper objectMapper = new ObjectMapper();

  private final RateLimiter rateLimiter;

  public CrptApiClient(TimeUnit timeUnit, int requestLimit) {
    rateLimiter = new RateLimiter(timeUnit, requestLimit);
  }

  public void createDocument(DocumentCreateRequest request) throws IOException, InterruptedException {
    rateLimiter.acquire();
    String json = objectMapper.writeValueAsString(request);
    HttpRequest httpRequest = HttpRequest.newBuilder()
        .uri(URI.create("https://ismp.crpt.ru/api/v3/lk/documents/create"))
        .POST(HttpRequest.BodyPublishers.ofString(json))
        .build();
//    httpClient.send(httpRequest, HttpResponse.BodyHandlers.discarding());
  }

  public void shutdown() {
    rateLimiter.getExecutorService().shutdown();
  }

  public static class DocumentCreateRequest {

    Description description;

    @JsonProperty("doc_id")
    String documentId;

    @JsonProperty("doc_status")
    String documentStatus;

    //Other fields...

    public Description getDescription() {
      return description;
    }

    public void setDescription(Description description) {
      this.description = description;
    }

    public String getDocumentId() {
      return documentId;
    }

    public void setDocumentId(String documentId) {
      this.documentId = documentId;
    }

    public String getDocumentStatus() {
      return documentStatus;
    }

    public void setDocumentStatus(String documentStatus) {
      this.documentStatus = documentStatus;
    }

    public static class Description {

      private String participantInn;

      public String getParticipantInn() {
        return participantInn;
      }

      public void setParticipantInn(String participantInn) {
        this.participantInn = participantInn;
      }
    }

  }

  public static class RateLimiter {

    private final TimeUnit timeUnit;

    private final Semaphore semaphore;

    private final ScheduledExecutorService executorService;

    public RateLimiter(TimeUnit timeUnit, int requestLimit) {
      this.timeUnit = timeUnit;
      this.semaphore = new Semaphore(requestLimit);
      executorService = Executors.newSingleThreadScheduledExecutor();
      Runnable releaseTask = semaphore::drainPermits;
      executorService.scheduleWithFixedDelay(releaseTask, 1, 1, timeUnit);
    }

    public void acquire() {
      try {
        semaphore.tryAcquire(1, timeUnit);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    public ScheduledExecutorService getExecutorService() {
      return executorService;
    }
  }
}
