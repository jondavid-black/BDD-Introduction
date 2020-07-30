package guestbook;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class Stepdefs {

    private Process guestbookService = null;

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @After
    public void shutdownTheService() throws InterruptedException {
        if (guestbookService != null) {
            guestbookService.destroy();
            Thread.sleep(500);
            int exitValue = guestbookService.exitValue();

            System.out.println("Shutdown the GuestBook service with exitValue " + exitValue);
        }
    }

    @Given("the GuestBook service is running")
    public void the_guest_book_service_is_running() throws IOException, InterruptedException {
        
        if (guestbookService == null) {
            System.out.println("Starting the GuestBook service");
            guestbookService = new ProcessBuilder("./server")
                .directory(new File("/home/jd/Documents/dev/repos/BDD-Introduction/lib"))
                .inheritIO()
                //.redirectOutput(new File("/home/jd/Documents/dev/output.txt"))
                //.redirectError(new File("/home/jd/Documents/dev/error.txt"))
                .start();
            System.out.println("GuestBook service:" + guestbookService.pid());

        }
        
        Thread.sleep(1000);
        System.out.println("Started the GuestBook service");
        
    }

    @Given("the GuestBook is empty")
    public void the_guest_book_is_empty() throws IOException, InterruptedException {

        System.out.println("GuestBook service:" + guestbookService.pid());

        if (!"[]".equals(getGuestBookContent())) {
            HttpRequest deleteRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://127.0.0.1:3000/guestbook"))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .build();

            HttpResponse<String> deleteResponse = httpClient.send(deleteRequest, BodyHandlers.ofString());
            if (deleteResponse.statusCode() != 200) {
                //TODO need to test this somehow
            }
        }
                
        System.out.println("The GuestBook is empty");
    }

    @When("the user sends request to add a guest entry")
    public void the_user_sends_request_to_add_a_guest_entry() throws IOException, InterruptedException {
        System.out.println("User sends add entry request to GuestBook");

        addGuestEntry(0, "name@test.com", "Hello World", "This is content for a test entry to the guest book.");
    }

    @Then("the GuestBook contians our entry")
    public void the_guest_book_contians_our_entry() throws IOException, InterruptedException {
        String entries = getGuestBookContent();
        System.out.println(entries);
        assertTrue(entries.contains("name@test.com"));
        assertTrue(entries.contains("Hello World"));
    }

    @Given("there are {int} entries in the GuestBook")
    public void there_are_entries_in_the_guest_book(Integer int1) throws IOException, InterruptedException {
        for (int i = 0; i< int1; i++) {
            addGuestEntry(i, "user_" + i, "title_" + i, "entry content");
        }
    }

    @When("the user requests the list of GuestBook entries")
    public void the_user_requests_the_list_of_guest_book_entries() {
        // no op
    }

    @Then("the GuestBook returns {int} entries")
    public void the_guest_book_returns_entries(Integer int1) throws IOException, InterruptedException {
        String entries = getGuestBookContent();
        for (int i=0; i<int1; i++) {
            assertTrue(entries.contains("user_" + i));
            assertTrue(entries.contains("title_" + i));
        }
    }
    
    private String getGuestBookContent() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("http://127.0.0.1:3000/guestbook"))
            .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
            .build();
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        return response.body();
    }

    private void addGuestEntry(int id, String email, String title, String content)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .POST(BodyPublishers.ofString(
                    "{\"Id\":" + id + ",\"Email\":\""+ email + "\",\"Title\":\"" + title + "\",\"Content\":\"" + content + "\"}"))
                .uri(URI.create("http://127.0.0.1:3000/guestbook"))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.out.println("ERROR: addGuestEntry statusCode = " + response.statusCode());
        }

    }
}